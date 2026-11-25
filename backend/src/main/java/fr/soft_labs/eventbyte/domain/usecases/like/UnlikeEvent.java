package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;

public class UnlikeEvent {

    private final LikeRepository likeRepository;

    public UnlikeEvent(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void execute(UserId userId, String eventId) {
        likeRepository.unlike(userId, eventId);
    }
}