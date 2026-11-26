package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class JpaBookingRepository implements BookingRepository {

    private final SpringDataBookingRepository springData;

    JpaBookingRepository(SpringDataBookingRepository springData) {
        this.springData = springData;
    }

    // Creates the booking, or updates its status if it already exists.
    // Lines never change after creation, so only the status is updated.
    @Override
    @Transactional
    public void save(Booking booking) {
        BookingEntity entity = springData.findById(booking.id())
                .orElseGet(() -> toEntity(booking));
        entity.setStatus(booking.status());
        springData.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findById(String id) {
        return springData.findById(id).map(JpaBookingRepository::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByUserId(UserId userId) {
        return springData.findByUserIdOrderByBookedAtDesc(userId.value()).stream()
                .map(JpaBookingRepository::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findExpirable(Instant bookedBefore) {
        return springData.findByStatusInAndBookedAtBefore(Booking.EXPIRABLE_STATUSES, bookedBefore).stream()
                .map(JpaBookingRepository::toDomain)
                .toList();
    }

    private static BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.id(),
                booking.userId().value(),
                booking.eventId(),
                booking.bookedAt(),
                booking.status(),
                booking.lines().stream()
                        .map(l -> new BookingLineEmbeddable(
                                l.ticketId(), l.name(), l.quantity(), l.unitPrice().cents()))
                        .toList());
    }

    static Booking toDomain(BookingEntity entity) {
        return new Booking(
                entity.getId(),
                new UserId(entity.getUserId()),
                entity.getEventId(),
                entity.getBookedAt(),
                entity.getLines().stream()
                        .map(l -> new BookingLine(
                                l.getTicketId(), l.getName(), l.getQuantity(),
                                Money.ofCents(l.getUnitPriceCents())))
                        .toList(),
                entity.getStatus());
    }
}
