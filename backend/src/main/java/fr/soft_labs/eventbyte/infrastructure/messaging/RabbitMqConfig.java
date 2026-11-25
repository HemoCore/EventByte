package fr.soft_labs.eventbyte.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EVENTS_EXCHANGE = "events";
    public static final String EVENTS_QUEUE = "eventbyte.backend.events";
    public static final String EVENTS_ROUTING_KEY = "event.*";

    @Bean
    TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    Queue eventsQueue() {
        return QueueBuilder.durable(EVENTS_QUEUE).build();
    }

    @Bean
    Binding eventsBinding(Queue eventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with(EVENTS_ROUTING_KEY);
    }
}