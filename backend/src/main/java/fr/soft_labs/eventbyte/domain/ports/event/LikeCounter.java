package fr.soft_labs.eventbyte.domain.ports.event;

import java.util.Collection;
import java.util.Map;

/**
 * Donne le nombre de likes de plusieurs événements en une seule fois.
 * Un événement sans like peut être absent de la map : il compte alors pour 0.
 */
public interface LikeCounter {

    Map<String, Long> countByEventIds(Collection<String> eventIds);
}
