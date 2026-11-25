package fr.soft_labs.eventbyte.infrastructure.messaging;

import fr.soft_labs.eventbyte.domain.usecases.event.DeleteEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.IngestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class StrapiEventListener {

    private static final Logger log = LoggerFactory.getLogger(StrapiEventListener.class);

    private final JsonMapper jsonMapper;
    private final IngestEvent ingestEvent;
    private final DeleteEvent deleteEvent;

    public StrapiEventListener(JsonMapper jsonMapper, IngestEvent ingestEvent, DeleteEvent deleteEvent) {
        this.jsonMapper = jsonMapper;
        this.ingestEvent = ingestEvent;
        this.deleteEvent = deleteEvent;
    }

    @RabbitListener(queues = RabbitMqConfig.EVENTS_QUEUE)
    public void onMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        byte[] body = message.getBody();

        switch (routingKey) {
            case "event.created", "event.updated" -> {
                StrapiEventMessage event = jsonMapper.readValue(body, StrapiEventMessage.class);
                ingestEvent.execute(StrapiEventMapper.toDomain(event));
                log.info("Event {} received ({})", event.id(), routingKey);
            }
            case "event.deleted" -> {
                StrapiDeletedEventMessage deleted = jsonMapper.readValue(body, StrapiDeletedEventMessage.class);
                deleteEvent.execute(deleted.id());
                log.info("Event {} deleted", deleted.id());
            }
            default -> log.warn("Ignored message, unknown routing key: {}", routingKey);
        }
    }
}
