import type { Core } from '@strapi/strapi';
import { v7 } from 'uuid';
import { RabbitMQ } from './services/rabbitMQ';
import { OutboxProcessor } from './services/outbox-processor';
import { OutboxRepository } from './services/outbox-repository';

export default {
    /**
     * An asynchronous register function that runs before
     * your application is initialized.
     *
     * This gives you an opportunity to extend code.
     */
    register(/* { strapi }: { strapi: Core.Strapi } */) { },

    /**
     * An asynchronous bootstrap function that runs before
     * your application gets started.
     *
     * This gives you an opportunity to set up your data model,
     * run jobs, or perform some special logic.
     */
    async bootstrap({ strapi }: { strapi: Core.Strapi }) {
        const rabbitMQ = await RabbitMQ.connect(process.env.RABBITMQ_URL);
        const outboxRepository = new OutboxRepository(strapi.db)
        strapi.add(OutboxRepository.toString(), outboxRepository)
        strapi.add(OutboxProcessor.toString(), new OutboxProcessor(outboxRepository, strapi.db, rabbitMQ))

        strapi.db.lifecycles.subscribe({
            models: ["value-object.ticket"], beforeCreate: async (event: any) => {
                const { data } = event.params;
                if (!data.uuid) {
                    data.uuid = `tkt_${v7()}`;
                }
            },
        });
    },
};
