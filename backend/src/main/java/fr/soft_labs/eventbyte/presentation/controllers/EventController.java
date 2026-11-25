package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEvent getEvent;

    public EventController(GetEvent getEvent) {
        this.getEvent = getEvent;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        return getEvent.execute(id)
                .map(EventResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
