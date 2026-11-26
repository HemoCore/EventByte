package fr.soft_labs.eventbyte.infrastructure.scheduling;

import fr.soft_labs.eventbyte.domain.usecases.booking.ExpireBookings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingExpirationScheduler.class);

    private final ExpireBookings expireBookings;

    public BookingExpirationScheduler(ExpireBookings expireBookings) {
        this.expireBookings = expireBookings;
    }

    // fixedDelay: the next run starts this long after the previous one ENDS, so runs never overlap
    @Scheduled(fixedDelayString = "${eventbyte.booking.expiration-check}")
    public void expireUnpaidBookings() {
        int expired = expireBookings.execute();
        if (expired > 0) {
            log.info("{} booking(s) expired, stock released", expired);
        }
    }
}
