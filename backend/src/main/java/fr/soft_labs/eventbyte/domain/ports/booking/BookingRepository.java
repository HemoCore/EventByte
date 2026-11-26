package fr.soft_labs.eventbyte.domain.ports.booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.user.UserId;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findById(String id);

    /** US-17: the booking paid by this payment (payment id sent back by Stripe). */
    Optional<Booking> findByPaymentId(String paymentId);

    /** All the bookings of a user, most recent first. */
    List<Booking> findByUserId(UserId userId);

    /** Bookings in Booking.EXPIRABLE_STATUSES that were booked before the given instant. */
    List<Booking> findExpirable(Instant bookedBefore);
}
