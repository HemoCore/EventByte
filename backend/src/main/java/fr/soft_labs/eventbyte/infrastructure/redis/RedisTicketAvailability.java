package fr.soft_labs.eventbyte.infrastructure.redis;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.infrastructure.persistence.InitialStockTicketAvailability;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Real remaining stock, used everywhere a TicketAvailability is needed (@Primary).
 * Starts from the database quantity (InitialStockTicketAvailability), then uses the
 * Redis counter stock:<ticketId> for every ticket that has already been booked.
 */
@Primary
@Component
public class RedisTicketAvailability implements TicketAvailability {

    private final StringRedisTemplate redis;
    private final InitialStockTicketAvailability initialStock;

    public RedisTicketAvailability(StringRedisTemplate redis, InitialStockTicketAvailability initialStock) {
        this.redis = redis;
        this.initialStock = initialStock;
    }

    @Override
    public Map<String, Integer> availableByTicketId(Collection<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return Map.of();
        }
        // 1. database quantity for every ticket (= stock when nobody booked yet)
        Map<String, Integer> available = new HashMap<>(initialStock.availableByTicketId(tickets));

        // 2. replace it with the Redis counter when the ticket was booked
        List<String> ticketIds = List.copyOf(available.keySet());
        List<String> keys = ticketIds.stream().map(id -> RedisStockReservation.KEY_PREFIX + id).toList();
        List<String> values = redis.opsForValue().multiGet(keys);
        if (values != null) {
            for (int i = 0; i < ticketIds.size(); i++) {
                if (values.get(i) != null) {
                    available.put(ticketIds.get(i), Integer.parseInt(values.get(i)));
                }
            }
        }
        return available;
    }
}
