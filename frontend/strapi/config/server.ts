import { OutboxProcessor } from "../src/services/outbox-processor";
import { Core } from "@strapi/strapi";

export default ({ env }) => ({
    host: env('HOST', '0.0.0.0'),
    port: env.int('PORT', 1337),
    app: {
        keys: env.array('APP_KEYS'),
    },
    cron: {
        enabled: true,
        tasks: {
            test: {
                task: async ({ strapi }: { strapi: Core.Strapi }) => {
                    const outboxProcessor = strapi.get<OutboxProcessor>(OutboxProcessor.toString())
                    await outboxProcessor.process()
                },
                options: {
                    rule: OutboxProcessor.CRON_EXPRESSION
                },
            }
        }
    }
});
