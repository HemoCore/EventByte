package fr.soft_labs.eventbyte.infrastructure.redis;

import fr.soft_labs.eventbyte.domain.ports.booking.StockReservation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RedisStockReservation implements StockReservation {

    static final String KEY_PREFIX = "stock:";

    private static final RedisScript<Long> RESERVE_SCRIPT = loadScript("redis/reserve-stock.lua");

    private final StringRedisTemplate redis;

    public RedisStockReservation(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean reserve(Map<String, Integer> quantityByTicketId, Map<String, Integer> initialStockByTicketId) {
        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();
        quantityByTicketId.forEach((ticketId, quantity) -> {
            keys.add(KEY_PREFIX + ticketId);
            args.add(String.valueOf(quantity));
            args.add(String.valueOf(initialStockByTicketId.getOrDefault(ticketId, 0)));
        });

        Long result = redis.execute(RESERVE_SCRIPT, keys, args.toArray());
        return result != null && result == 1L;
    }

    private static RedisScript<Long> loadScript(String path) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(path));
        script.setResultType(Long.class);
        return script;
    }
}
