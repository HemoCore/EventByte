package fr.soft_labs.eventbyte.domain.ports.event;

import java.util.Collection;
import java.util.Map;

public interface LikeCounter {

    Map<String, Long> countByEventIds(Collection<String> eventIds);
}
