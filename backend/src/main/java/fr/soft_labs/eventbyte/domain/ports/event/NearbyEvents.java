package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.time.Instant;
import java.util.List;

public interface NearbyEvents {

    List<Event> findUpcomingAround(Coordinates center, double radiusKm, Instant now, int limit);
}
