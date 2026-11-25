package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;

import java.time.Instant;
import java.util.List;

/** Trouve les événements à venir autour d'une position. */
public interface NearbyEvents {

    /**
     * Événements dont le lieu est à moins de {@code radiusKm} du centre
     * et qui commencent après {@code now}, du plus proche dans le temps au plus lointain.
     */
    List<Event> findUpcomingAround(Coordinates center, double radiusKm, Instant now, int limit);
}
