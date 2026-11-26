package fr.soft_labs.eventbyte.infrastructure.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/** Needs the Redis from docker compose (docker compose up -d). */
class RedisStockReservationTest {

    private final LocalRedis redis = new LocalRedis();
    private final RedisStockReservation reservation = new RedisStockReservation(redis.template);

    // unique ids so tests never touch real stock keys
    private final String ticketA = "test_" + UUID.randomUUID();
    private final String ticketB = "test_" + UUID.randomUUID();

    @AfterEach
    void cleanUp() {
        redis.template.delete(List.of("stock:" + ticketA, "stock:" + ticketB));
        redis.close();
    }

    private String stockOf(String ticketId) {
        return redis.template.opsForValue().get("stock:" + ticketId);
    }

    @Test
    void only_one_of_twenty_simultaneous_bookings_gets_the_last_ticket() throws Exception {
        redis.template.opsForValue().set("stock:" + ticketA, "1");

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            results.add(pool.submit(() -> {
                ready.countDown();
                go.await(); // every thread waits here, then all start at the same moment
                return reservation.reserve(Map.of(ticketA, 1), Map.of(ticketA, 1));
            }));
        }
        ready.await();
        go.countDown();

        int successes = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                successes++;
            }
        }
        pool.shutdown();

        assertThat(successes).isEqualTo(1);
        assertThat(stockOf(ticketA)).isEqualTo("0");
    }

    @Test
    void the_first_booking_sets_the_stock_from_the_initial_quantity() {
        boolean reserved = reservation.reserve(Map.of(ticketA, 2), Map.of(ticketA, 5));

        assertThat(reserved).isTrue();
        assertThat(stockOf(ticketA)).isEqualTo("3");
    }

    @Test
    void the_initial_quantity_is_ignored_once_the_stock_exists() {
        redis.template.opsForValue().set("stock:" + ticketA, "1");

        boolean reserved = reservation.reserve(Map.of(ticketA, 1), Map.of(ticketA, 100));

        assertThat(reserved).isTrue();
        assertThat(stockOf(ticketA)).isEqualTo("0");
    }

    @Test
    void release_gives_the_stock_back() {
        redis.template.opsForValue().set("stock:" + ticketA, "0");
        redis.template.opsForValue().set("stock:" + ticketB, "3");

        reservation.release(Map.of(ticketA, 2, ticketB, 1));

        assertThat(stockOf(ticketA)).isEqualTo("2");
        assertThat(stockOf(ticketB)).isEqualTo("4");
    }

    @Test
    void release_does_not_create_a_missing_stock() {
        reservation.release(Map.of(ticketA, 2));

        assertThat(stockOf(ticketA)).isNull();
    }

    @Test
    void a_released_seat_can_be_reserved_again() {
        redis.template.opsForValue().set("stock:" + ticketA, "0");
        assertThat(reservation.reserve(Map.of(ticketA, 1), Map.of(ticketA, 1))).isFalse();

        reservation.release(Map.of(ticketA, 1));

        assertThat(reservation.reserve(Map.of(ticketA, 1), Map.of(ticketA, 1))).isTrue();
        assertThat(stockOf(ticketA)).isEqualTo("0");
    }

    @Test
    void nothing_is_taken_when_one_ticket_lacks_stock() {
        redis.template.opsForValue().set("stock:" + ticketA, "5");
        redis.template.opsForValue().set("stock:" + ticketB, "0");

        boolean reserved = reservation.reserve(Map.of(ticketA, 2, ticketB, 1), Map.of(ticketA, 5, ticketB, 0));

        assertThat(reserved).isFalse();
        assertThat(stockOf(ticketA)).isEqualTo("5");
        assertThat(stockOf(ticketB)).isEqualTo("0");
    }
}
