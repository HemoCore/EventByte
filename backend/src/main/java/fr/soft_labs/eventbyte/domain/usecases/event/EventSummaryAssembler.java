package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;

import java.util.List;
import java.util.Map;

public class EventSummaryAssembler {

    private final LikeCounter likeCounter;
    private final TicketAvailability ticketAvailability;

    public EventSummaryAssembler(LikeCounter likeCounter, TicketAvailability ticketAvailability) {
        this.likeCounter = likeCounter;
        this.ticketAvailability = ticketAvailability;
    }

    public List<EventSummary> assemble(List<Event> events) {
        if (events.isEmpty()) {
            return List.of();
        }
        Map<String, Long> likes = likeCounter.countByEventIds(events.stream().map(Event::id).toList());
        Map<String, Integer> available = ticketAvailability.availableByTicketId(
                events.stream().flatMap(event -> event.tickets().stream()).toList());

        return events.stream()
                .map(event -> new EventSummary(
                        event,
                        likes.getOrDefault(event.id(), 0L),
                        isSoldOut(event, available)))
                .toList();
    }

    public EventSummary assemble(Event event) {
        return assemble(List.of(event)).getFirst();
    }

    private static boolean isSoldOut(Event event, Map<String, Integer> available) {
        return event.tickets().stream()
                .map(Ticket::id)
                .noneMatch(ticketId -> available.getOrDefault(ticketId, 0) > 0);
    }
}
