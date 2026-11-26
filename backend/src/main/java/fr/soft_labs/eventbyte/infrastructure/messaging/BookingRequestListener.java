package fr.soft_labs.eventbyte.infrastructure.messaging;

import fr.soft_labs.eventbyte.domain.usecases.booking.ProcessBookingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookingRequestListener {

    private static final Logger log = LoggerFactory.getLogger(BookingRequestListener.class);

    private final ProcessBookingRequest processBookingRequest;

    public BookingRequestListener(ProcessBookingRequest processBookingRequest) {
        this.processBookingRequest = processBookingRequest;
    }

    @RabbitListener(queues = RabbitMqConfig.BOOKINGS_QUEUE)
    public void onMessage(String bookingId) {
        processBookingRequest.execute(bookingId);
        log.info("Booking {} processed", bookingId);
    }
}
