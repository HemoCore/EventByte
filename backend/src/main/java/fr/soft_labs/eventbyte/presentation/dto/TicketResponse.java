package fr.soft_labs.eventbyte.presentation.dto;

import fr.soft_labs.eventbyte.domain.models.event.AvailableTicket;

// README: GET /events/:eventId/tickets -> Array<{ id, eventId, name, price (centimes), quantity (disponible) }>
public record TicketResponse(String id, String eventId, String name, long price, int quantity) {

    public static TicketResponse from(AvailableTicket ticket) {
        return new TicketResponse(
                ticket.id(),
                ticket.eventId(),
                ticket.name(),
                ticket.price().cents(),
                ticket.quantity());
    }
}