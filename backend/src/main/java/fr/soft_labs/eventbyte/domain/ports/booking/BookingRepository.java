package fr.soft_labs.eventbyte.domain.ports.booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findById(String id);

    /** Bookings in Booking.EXPIRABLE_STATUSES that were booked before the given instant. */
    List<Booking> findExpirable(Instant bookedBefore);
}
