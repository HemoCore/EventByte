package fr.soft_labs.eventbyte.presentation.dto;

import fr.soft_labs.eventbyte.domain.models.event.AvailableTicket;

import java.math.BigDecimal;

// GET /events/:eventId/tickets -> Array<{ id, eventId, name, price, quantity (disponible) }>
// price in euros (e.g. 27.51): the frontend displays the number as euros. Inside, Money stays in cents.
public record TicketResponse(String id, String eventId, String name, BigDecimal price, int quantity) {

    public static TicketResponse from(AvailableTicket ticket) {
        return new TicketResponse(
                ticket.id(),
                ticket.eventId(),
                ticket.name(),
                ticket.price().euros(),
                ticket.quantity());
    }
}
