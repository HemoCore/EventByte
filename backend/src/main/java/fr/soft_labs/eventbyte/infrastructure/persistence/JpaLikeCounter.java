package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JpaLikeCounter implements LikeCounter {

    private final EntityManager entityManager;

    public JpaLikeCounter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countByEventIds(Collection<String> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = entityManager.createQuery("""
                        SELECT l.id.eventId, COUNT(l)
                        FROM LikeEntity l
                        WHERE l.id.eventId IN :eventIds
                        GROUP BY l.id.eventId
                        """, Object[].class)
                .setParameter("eventIds", eventIds)
                .getResultList();

        return rows.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()));
    }
}
