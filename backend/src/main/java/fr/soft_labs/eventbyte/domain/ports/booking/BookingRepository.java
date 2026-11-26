package fr.soft_labs.eventbyte.domain.ports.booking;

import java.util.Optional;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findById(String id);
}
