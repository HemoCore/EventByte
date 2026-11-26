package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventsByIds;

import java.util.List;

public class GetLikedEvents {

    private final LikeRepository likeRepository;
    private final GetEventsByIds getEventsByIds;

    public GetLikedEvents(LikeRepository likeRepository, GetEventsByIds getEventsByIds) {
        this.likeRepository = likeRepository;
        this.getEventsByIds = getEventsByIds;
    }

    public List<EventSummary> execute(UserId userId) {
        return getEventsByIds.execute(likeRepository.findEventIdsLikedBy(userId));
    }
}
