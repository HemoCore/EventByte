package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.infrastructure.sse.SseBookingNotifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class BookingStreamController {

    private final SseBookingNotifier notifier;
    private final CurrentUser currentUser;

    public BookingStreamController(SseBookingNotifier notifier, CurrentUser currentUser) {
        this.notifier = notifier;
        this.currentUser = currentUser;
    }

    @GetMapping(path = "/bookings/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return notifier.subscribe(currentUser.get());
    }
}