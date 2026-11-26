package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;

import java.util.List;

public class GetUserBookings {

    private final BookingRepository bookingRepository;

    public GetUserBookings(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> execute(UserId userId) {
        return bookingRepository.findByUserId(userId);
    }
}