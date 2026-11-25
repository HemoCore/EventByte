package fr.soft_labs.eventbyte.domain.models.event;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record Event(
        String id,
        String title,
        String description,
        String cover,
        Instant startAt,
        Venue venue,
        List<Ticket> tickets
) {

    public Event {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'événement est obligatoire");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre de l'événement est obligatoire");
        }
        if (venue == null) {
            throw new IllegalArgumentException("Le lieu de l'événement est obligatoire");
        }
        tickets = tickets == null ? List.of() : List.copyOf(tickets);
    }

    public Optional<Money> lowestPrice() {
        return tickets.stream()
                .map(Ticket::price)
                .reduce((a, b) -> a.isLessThan(b) ? a : b);
    }
}
