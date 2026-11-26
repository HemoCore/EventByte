package fr.soft_labs.eventbyte.infrastructure.redis;

import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.infrastructure.persistence.InitialStockTicketAvailability;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Needs the Redis from docker compose (docker compose up -d). */
class RedisTicketAvailabilityTest {

    private final LocalRedis redis = new LocalRedis();
    private final RedisTicketAvailability availability =
            new RedisTicketAvailability(redis.template, new InitialStockTicketAvailability());

    private final String booked = "test_" + UUID.randomUUID();
    private final String soldOut = "test_" + UUID.randomUUID();
    private final String neverBooked = "test_" + UUID.randomUUID();

    @AfterEach
    void cleanUp() {
        redis.template.delete(List.of("stock:" + booked, "stock:" + soldOut, "stock:" + neverBooked));
        redis.close();
    }

    private static Ticket ticket(String id, int quantity) {
        return new Ticket(id, "VIP", Money.ofCents(1000), quantity);
    }

    @Test
    void uses_the_redis_stock_when_the_ticket_was_booked() {
        redis.template.opsForValue().set("stock:" + booked, "7");

        Map<String, Integer> result = availability.availableByTicketId(List.of(ticket(booked, 10)));

        assertThat(result).containsEntry(booked, 7);
    }

    @Test
    void returns_zero_when_the_ticket_is_sold_out() {
        redis.template.opsForValue().set("stock:" + soldOut, "0");

        Map<String, Integer> result = availability.availableByTicketId(List.of(ticket(soldOut, 10)));

        assertThat(result).containsEntry(soldOut, 0);
    }

    @Test
    void uses_the_database_quantity_when_nobody_booked_yet() {
        Map<String, Integer> result = availability.availableByTicketId(List.of(ticket(neverBooked, 10)));

        assertThat(result).containsEntry(neverBooked, 10);
    }

    @Test
    void handles_several_tickets_at_once() {
        redis.template.opsForValue().set("stock:" + soldOut, "0");

        Map<String, Integer> result = availability.availableByTicketId(
                List.of(ticket(soldOut, 10), ticket(neverBooked, 4)));

        assertThat(result).containsEntry(soldOut, 0).containsEntry(neverBooked, 4);
    }

    @Test
    void returns_an_empty_map_for_no_tickets() {
        assertThat(availability.availableByTicketId(List.of())).isEmpty();
    }
}
