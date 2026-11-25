package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
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
    public EventResponse getById(@PathVariable String id) {
        return EventResponse.from(getEvent.execute(id));
    }
}
