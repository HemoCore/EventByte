package fr.soft_labs.eventbyte.domain.models.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.event.Venue;

import java.time.Instant;
import java.util.List;

public class EventBuilder {

    private String id = "evt_1";
    private String title = "Brunch";
    private String description = "Brunch with live music";
    private String cover = "https://example.com/cover.jpg";
    private Instant startAt = null;
    private Venue venue = new Venue("Restaurant Prosacco", "111 Place Voltaire, 31000 Toulouse", 43.626, 1.455);
    private List<Ticket> tickets = List.of(
            new Ticket("tkt_1", "Standard", Money.ofCents(2751), 21),
            new Ticket("tkt_2", "Premium", Money.ofCents(4339), 78)
    );

    private EventBuilder() {
    }

    public static EventBuilder anEvent() {
        return new EventBuilder();
    }

    public EventBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public EventBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public EventBuilder withStartAt(Instant startAt) {
        this.startAt = startAt;
        return this;
    }

    public EventBuilder withVenue(Venue venue) {
        this.venue = venue;
        return this;
    }

    public EventBuilder withTickets(Ticket... tickets) {
        this.tickets = List.of(tickets);
        return this;
    }

    public Event build() {
        return new Event(id, title, description, cover, startAt, venue, tickets);
    }
}