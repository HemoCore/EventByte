package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryEventRepository implements EventRepository {

    private final Map<String, Event> events = new HashMap<>();

    @Override
    public void save(Event event) {
        events.put(event.id(), event);
    }

    @Override
    public Optional<Event> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public void deleteById(String id) {
        events.remove(id);
    }

    public int count() {
        return events.size();
    }
}