package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.ports.event.NearbyEvents;

import java.time.Clock;
import java.util.List;

/**
 * US-4 : découvrir les événements près de soi.
 * Choix du groupe : événements à venir à moins de 50 km, triés par date, 50 au maximum.
 */
public class DiscoverEvents {

    public static final double RADIUS_KM = 50;
    public static final int MAX_RESULTS = 50;

    private final NearbyEvents nearbyEvents;
    private final EventSummaryAssembler assembler;
    private final Clock clock;

    public DiscoverEvents(NearbyEvents nearbyEvents, EventSummaryAssembler assembler, Clock clock) {
        this.nearbyEvents = nearbyEvents;
        this.assembler = assembler;
        this.clock = clock;
    }

    public List<EventSummary> execute(Coordinates position) {
        return assembler.assemble(
                nearbyEvents.findUpcomingAround(position, RADIUS_KM, clock.instant(), MAX_RESULTS));
    }
}
