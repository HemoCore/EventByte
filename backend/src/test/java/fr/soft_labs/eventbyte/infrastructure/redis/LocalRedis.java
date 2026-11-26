package fr.soft_labs.eventbyte.infrastructure.redis;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Connects tests to the Redis started by docker compose (localhost:6379),
 * or to SPRING_DATA_REDIS_HOST when tests run inside a container.
 */
final class LocalRedis {

    private final LettuceConnectionFactory factory;
    final StringRedisTemplate template;

    LocalRedis() {
        String host = System.getenv().getOrDefault("SPRING_DATA_REDIS_HOST", "localhost");
        factory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, 6379));
        factory.afterPropertiesSet();
        factory.start();
        template = new StringRedisTemplate(factory);
    }

    void close() {
        factory.destroy();
    }
}
