package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BookingLineEmbeddable {

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price_cents", nullable = false)
    private long unitPriceCents;

    protected BookingLineEmbeddable() {
    }

    public BookingLineEmbeddable(String ticketId, String name, int quantity, long unitPriceCents) {
        this.ticketId = ticketId;
        this.name = name;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
    }

    public String getTicketId() { return ticketId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public long getUnitPriceCents() { return unitPriceCents; }
}
