package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.usecases.event.DiscoverEvents;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DiscoverEventsController {

    private final DiscoverEvents discoverEvents;

    public DiscoverEventsController(DiscoverEvents discoverEvents) {
        this.discoverEvents = discoverEvents;
    }

    @GetMapping("/events/discover")
    public List<EventResponse> discover(@RequestParam double lat, @RequestParam double lng) {
        return discoverEvents.execute(new Coordinates(lat, lng)).stream()
                .map(EventResponse::from)
                .toList();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Void> badCoordinates() {
        return ResponseEntity.badRequest().build();
    }
}
