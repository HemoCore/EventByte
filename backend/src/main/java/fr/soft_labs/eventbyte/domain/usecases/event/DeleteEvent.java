package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;

public class DeleteEvent {

    private final EventRepository eventRepository;
    private final EventSearchIndex searchIndex;

    public DeleteEvent(EventRepository eventRepository, EventSearchIndex searchIndex) {
        this.eventRepository = eventRepository;
        this.searchIndex = searchIndex;
    }

    public void execute(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'événement est obligatoire");
        }
        eventRepository.deleteById(eventId);
        searchIndex.remove(eventId);
    }
}
