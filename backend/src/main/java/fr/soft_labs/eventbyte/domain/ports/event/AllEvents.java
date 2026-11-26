package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.util.List;

public interface AllEvents {

    List<Event> findAll();
}
