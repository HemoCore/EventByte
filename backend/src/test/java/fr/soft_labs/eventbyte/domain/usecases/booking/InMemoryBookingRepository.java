package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> bookings = new HashMap<>();

    @Override
    public void save(Booking booking) {
        bookings.put(booking.id(), booking);
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findExpirable(Instant bookedBefore) {
        return bookings.values().stream()
                .filter(b -> Booking.EXPIRABLE_STATUSES.contains(b.status()))
                .filter(b -> b.bookedAt().isBefore(bookedBefore))
                .toList();
    }
}
