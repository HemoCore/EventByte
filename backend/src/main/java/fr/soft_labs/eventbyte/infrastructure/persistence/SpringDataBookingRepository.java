package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface SpringDataBookingRepository extends JpaRepository<BookingEntity, String> {

    // Spring Data builds the SQL from the name: WHERE status IN (...) AND booked_at < ?
    List<BookingEntity> findByStatusInAndBookedAtBefore(Collection<BookingStatus> statuses, Instant bookedBefore);

    // US-17: WHERE payment_intent_id = ? (unique index from V4)
    Optional<BookingEntity> findByPaymentIntentId(String paymentIntentId);

    // WHERE user_id = ? ORDER BY booked_at DESC (uses idx_bookings_user_id)
    List<BookingEntity> findByUserIdOrderByBookedAtDesc(String userId);
}
