package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.AvailableTicket;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventNotFoundException;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;

import java.util.List;
import java.util.Map;

public class GetEventTickets {

    private final EventRepository eventRepository;
    private final TicketAvailability ticketAvailability;

    public GetEventTickets(EventRepository eventRepository, TicketAvailability ticketAvailability) {
        this.eventRepository = eventRepository;
        this.ticketAvailability = ticketAvailability;
    }

    public List<AvailableTicket> execute(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Map<String, Integer> available = ticketAvailability.availableByTicketId(event.tickets());

        return event.tickets().stream()
                .map(ticket -> new AvailableTicket(
                        ticket.id(),
                        event.id(),
                        ticket.name(),
                        ticket.price(),
                        Math.max(0, available.getOrDefault(ticket.id(), 0))))
                .toList();
    }
}