package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;

public class IngestEvent {
    private final EventRepository eventRepository;

    public IngestEvent(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void execute(Event event) {
        eventRepository.save(event);
    }
}
