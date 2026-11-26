package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventTickets;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import fr.soft_labs.eventbyte.presentation.dto.TicketResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEvent getEvent;
    private final GetEventTickets getEventTickets;

    public EventController(GetEvent getEvent, GetEventTickets getEventTickets) {
        this.getEvent = getEvent;
        this.getEventTickets = getEventTickets;
    }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable String id) {
        return EventResponse.from(getEvent.execute(id));
    }

    @GetMapping("/{eventId}/tickets")
    public List<TicketResponse> getTickets(@PathVariable String eventId) {
        return getEventTickets.execute(eventId).stream()
                .map(TicketResponse::from)
                .toList();
    }
}