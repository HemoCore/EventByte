package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class JpaLikeRepository implements LikeRepository {

    private final SpringDataLikeRepository springData;

    JpaLikeRepository(SpringDataLikeRepository springData) {
        this.springData = springData;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(UserId userId, String eventId) {
        return springData.existsById(new LikeId(userId.value(), eventId));
    }

    @Override
    @Transactional
    public void like(UserId userId, String eventId) {
        springData.insertIfAbsent(userId.value(), eventId);
    }

    @Override
    @Transactional
    public void unlike(UserId userId, String eventId) {
        springData.deleteById(new LikeId(userId.value(), eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByEventId(String eventId) {
        return springData.countByIdEventId(eventId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<String> findEventIdsLikedBy(UserId userId) {
        return springData.findByIdUserIdOrderByCreatedAtDesc(userId.value()).stream()
                .map(like -> like.getId().getEventId())
                .toList();
    }
}