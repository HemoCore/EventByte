package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.booking.GetBooking;
import fr.soft_labs.eventbyte.domain.usecases.booking.GetUserBookings;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.dto.BookingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetBookingsController {

    private final GetBooking getBooking;
    private final GetUserBookings getUserBookings;
    private final CurrentUser currentUser;

    public GetBookingsController(GetBooking getBooking, GetUserBookings getUserBookings, CurrentUser currentUser) {
        this.getBooking = getBooking;
        this.getUserBookings = getUserBookings;
        this.currentUser = currentUser;
    }

    // "/bookings/user" is an exact path: Spring picks it before "/bookings/{id}"
    @GetMapping("/bookings/user")
    public List<BookingResponse> userBookings() {
        return getUserBookings.execute(currentUser.get()).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @GetMapping("/bookings/{id}")
    public BookingResponse byId(@PathVariable String id) {
        return BookingResponse.from(getBooking.execute(currentUser.get(), id));
    }
}