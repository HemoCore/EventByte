package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;

import java.util.List;
import java.util.Optional;

public class GetLikedEvents {

    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;
    private final EventSummaryAssembler assembler;

    public GetLikedEvents(LikeRepository likeRepository, EventRepository eventRepository,
                          EventSummaryAssembler assembler) {
        this.likeRepository = likeRepository;
        this.eventRepository = eventRepository;
        this.assembler = assembler;
    }

    public List<EventSummary> execute(UserId userId) {
        List<Event> likedEvents = likeRepository.findEventIdsLikedBy(userId).stream()
                .map(eventRepository::findById)
                .flatMap(Optional::stream)
                .toList();
        return assembler.assemble(likedEvents);
    }
}