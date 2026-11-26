package fr.soft_labs.eventbyte.infrastructure.messaging;

import fr.soft_labs.eventbyte.domain.ports.booking.BookingRequestPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitBookingRequestPublisher implements BookingRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitBookingRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String bookingId) {
        // "" = RabbitMQ default exchange: the message goes straight to the queue named by the routing key
        rabbitTemplate.convertAndSend("", RabbitMqConfig.BOOKINGS_QUEUE, bookingId);
    }
}
