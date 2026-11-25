package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;

public class DeleteEvent {

    private final EventRepository eventRepository;

    public DeleteEvent(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void execute(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'événement est obligatoire");
        }
        eventRepository.deleteById(eventId);
    }
}
