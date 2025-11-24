import { Core } from "@strapi/strapi";
import { Knex } from "knex";
import { RabbitMQ } from "./rabbitMQ";
import { OutboxRepository } from "./outbox-repository";

export class OutboxProcessor {
    public static CRON_EXPRESSION = '*/1 * * * * *'
    private static EVENT_LIMIT = 200

    constructor(private readonly outboxRepository: OutboxRepository, private readonly db: Core.Strapi["db"], private readonly rabbitmq: RabbitMQ) { }

    async process() {
        await this.db.transaction(async ({ trx }: { trx: Knex.Transaction }) => {
            const events = await this.outboxRepository.getProccesableEvents(trx, OutboxProcessor.EVENT_LIMIT)

            for (const event of events) {
                const success = this.rabbitmq.send(event.event_type, event.payload)

                if (!success) {
                    continue
                }
                await trx.from("outbox_events").where("id", event.id).update({ processed_at: new Date() })
            }
        })
    }
}
