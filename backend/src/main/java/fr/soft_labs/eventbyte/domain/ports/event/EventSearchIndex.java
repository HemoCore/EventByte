package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface EventSearchIndex {

    void index(Collection<Event> events);

    void remove(String eventId);

    List<String> search(String query, Coordinates center, double radiusKm, Instant from, int limit);
}
