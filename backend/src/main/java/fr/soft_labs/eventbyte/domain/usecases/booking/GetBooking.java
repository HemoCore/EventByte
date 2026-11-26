package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingNotFoundException;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;

public class GetBooking {

    private final BookingRepository bookingRepository;

    public GetBooking(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * The booking of another user is treated as not found (404, not 403):
     * we never reveal that a booking id exists for someone else.
     */
    public Booking execute(UserId userId, String bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.userId().equals(userId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}