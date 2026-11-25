package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;

import java.util.Optional;

public class GetEvent {

    private final EventRepository eventRepository;
    private final EventSummaryAssembler assembler;

    public GetEvent(EventRepository eventRepository, EventSummaryAssembler assembler) {
        this.eventRepository = eventRepository;
        this.assembler = assembler;
    }

    public Optional<EventSummary> execute(String id) {
        return eventRepository.findById(id).map(assembler::assemble);
    }
}
