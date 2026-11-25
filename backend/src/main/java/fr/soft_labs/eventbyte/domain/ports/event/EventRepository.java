package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.util.Optional;

public interface EventRepository {

    void save(Event event);

    Optional<Event> findById(String id);

    void deleteById(String id);
}
