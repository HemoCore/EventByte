package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.util.Collection;
import java.util.List;

public interface EventsByIds {

    List<Event> findAllByIds(Collection<String> ids);
}
