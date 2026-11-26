package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;

import java.util.List;

public class IngestEvent {
    private final EventRepository eventRepository;
    private final EventSearchIndex searchIndex;

    public IngestEvent(EventRepository eventRepository, EventSearchIndex searchIndex) {
        this.eventRepository = eventRepository;
        this.searchIndex = searchIndex;
    }

    public void execute(Event event) {
        eventRepository.save(event);
        searchIndex.index(List.of(event));
    }
}
