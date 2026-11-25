package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_cents", nullable = false)
    private int priceCents;

    @Column(nullable = false)
    private int quantity;

    protected TicketEntity() {
    }

    public TicketEntity(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public EventEntity getEvent() { return event; }
    public void setEvent(EventEntity event) { this.event = event; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPriceCents() { return priceCents; }
    public void setPriceCents(int priceCents) { this.priceCents = priceCents; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}