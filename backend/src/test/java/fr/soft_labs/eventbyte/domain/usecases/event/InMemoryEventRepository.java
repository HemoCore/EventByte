package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryEventRepository implements EventRepository, EventsByIds {

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
    public List<Event> findAllByIds(Collection<String> ids) {
        return ids.stream().map(events::get).filter(Objects::nonNull).toList();
    }

    @Override
    public void deleteById(String id) {
        events.remove(id);
    }

    public int count() {
        return events.size();
    }
}