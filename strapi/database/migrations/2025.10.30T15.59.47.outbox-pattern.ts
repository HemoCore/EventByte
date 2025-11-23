import type { Knex } from 'knex';

/**
 * Migration `outbox-pattern`
 */

module.exports = {
    up: async (knex: Knex) => {
        await knex.schema.withSchema('strapi').createTable('outbox_events', (table) => {
            table.increments('id').primary();
            table.string('event_type', 100).notNullable();
            table.json('payload').notNullable();
            table.boolean('processed').defaultTo(false);
            table.timestamp('created_at').defaultTo(knex.fn.now());
            table.timestamp('processed_at').nullable();

            table.index(['processed', 'created_at']);
        })
    },
};
