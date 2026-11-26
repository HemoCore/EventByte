package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.booking.RequestBooking;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.dto.BookingCreatedResponse;
import fr.soft_labs.eventbyte.presentation.dto.CreateBookingRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookingController {

    private final RequestBooking requestBooking;
    private final CurrentUser currentUser;

    public BookingController(RequestBooking requestBooking, CurrentUser currentUser) {
        this.requestBooking = requestBooking;
        this.currentUser = currentUser;
    }

    @PostMapping("/bookings")
    public BookingCreatedResponse create(@RequestBody CreateBookingRequest request) {
        List<RequestBooking.Item> items = request.items() == null ? List.of() : request.items().stream()
                .map(item -> new RequestBooking.Item(item.id(), item.quantity()))
                .toList();
        return new BookingCreatedResponse(requestBooking.execute(currentUser.get(), items));
    }
}
