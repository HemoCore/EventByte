package fr.soft_labs.eventbyte.domain.models.booking;

import fr.soft_labs.eventbyte.domain.models.event.Money;

public record BookingLine(String ticketId, String name, int quantity, Money unitPrice) {

    public BookingLine {
        if(ticketId == null || ticketId.isBlank()) {
            throw new InvalidBookingException("Ticket id is required");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidBookingException("Ticket name is required");
        }
        if(quantity <= 0) {
            throw new InvalidBookingException("Quantity must be positive: " + quantity);
        }
        if(unitPrice == null) {
            throw new InvalidBookingException("Unit price is required");
        }
    }

    public Money subtotal() {
        return Money.ofCents(unitPrice.cents() * quantity);
    }
}
