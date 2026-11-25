package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;

import java.util.*;

public class InMemoryLikeRepository implements LikeRepository {

    private record LikeKey(UserId userId, String eventId) {
    }

    private final Set<LikeKey> likes = new LinkedHashSet<>();

    @Override
    public boolean isLiked(UserId userId, String eventId) {
        return likes.contains(new LikeKey(userId, eventId));
    }

    @Override
    public void like(UserId userId, String eventId) {
        likes.add(new LikeKey(userId, eventId));
    }

    @Override
    public void unlike(UserId userId, String eventId) {
        likes.remove(new LikeKey(userId, eventId));
    }

    @Override
    public long countByEventId(String eventId) {
        return likes.stream().filter(like -> like.eventId().equals(eventId)).count();
    }


    @Override
    public List<String> findEventIdsLikedBy(UserId userId) {
        List<String> eventIds = new ArrayList<>(likes.stream()
                .filter(like -> like.userId().equals(userId))
                .map(LikeKey::eventId)
                .toList());
        Collections.reverse(eventIds);
        return eventIds;
    }
}
