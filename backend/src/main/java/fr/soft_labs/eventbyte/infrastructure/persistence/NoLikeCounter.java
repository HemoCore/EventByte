package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Adapter provisoire : 0 like partout.
 * À supprimer quand les likes (US-8) seront mergés, au profit d'un adapter
 * qui compte les lignes de la table likes.
 */
@Component
public class NoLikeCounter implements LikeCounter {

    @Override
    public Map<String, Long> countByEventIds(Collection<String> eventIds) {
        return Map.of();
    }
}
