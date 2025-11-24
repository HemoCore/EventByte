import { Core } from "@strapi/strapi";
import { Knex } from "knex";

const TABLE_NAME = 'outbox_events'

export class OutboxRepository {
    constructor(private readonly db: Core.Strapi["db"]) { }

    async create(eventType: string, payload: Record<string, unknown>) {
        await this.db.transaction(async ({ trx }: { trx: Knex.Transaction }) => {
            await trx.from(TABLE_NAME).insert({
                event_type: eventType,
                payload: payload
            })
        })
    }

    async getProccesableEvents(trx: Knex.Transaction, limit: number) {
        return await trx.from(TABLE_NAME)
            .select("id", "event_type", "payload")
            .where("processed_at", null)
            .limit(limit)
            .forUpdate()
            .skipLocked()
    }
}
