package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.event.EventNotFoundException;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;

public class LikeEvent {

    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;

    public LikeEvent(LikeRepository likeRepository, EventRepository eventRepository) {
        this.likeRepository = likeRepository;
        this.eventRepository = eventRepository;
    }

    public void execute(UserId userId, String eventId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        if (likeRepository.isLiked(userId, eventId)) {
            return;
        }
        likeRepository.like(userId, eventId);
    }

}
