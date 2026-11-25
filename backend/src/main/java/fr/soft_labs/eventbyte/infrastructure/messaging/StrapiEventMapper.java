package fr.soft_labs.eventbyte.infrastructure.messaging;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.event.Venue;

import java.util.List;

public final class StrapiEventMapper {

    private StrapiEventMapper() {
    }

    public static Event toDomain(StrapiEventMessage message) {
        if (message.venue() == null) {
            throw new IllegalArgumentException("Strapi message without venue for event " + message.id());
        }
        StrapiEventMessage.VenueMessage venue = message.venue();

        List<Ticket> tickets = message.tickets() == null ? List.of() : message.tickets().stream()
                .map(ticket -> new Ticket(
                        ticket.id(),
                        ticket.name(),
                        Money.fromEuros(ticket.price()),
                        ticket.quantity() == null ? 0 : ticket.quantity()))
                .toList();

        return new Event(
                message.id(),
                message.title(),
                message.description(),
                message.cover(),
                message.startAt(),
                new Venue(venue.name(), venue.address(),
                        required(venue.latitude(), "latitude"),
                        required(venue.longitude(), "longitude")),
                tickets
        );
    }

    private static double required(Double value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Missing required venue field: " + field);
        }
        return value;
    }
}