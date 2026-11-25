package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.NearbyEvents;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class JpaNearbyEvents implements NearbyEvents {

    private static final double KM_PER_DEGREE_OF_LATITUDE = 111.32;

    private final SpringDataEventRepository springData;

    public JpaNearbyEvents(SpringDataEventRepository springData) {
        this.springData = springData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findUpcomingAround(Coordinates center, double radiusKm, Instant now, int limit) {
        double latDelta = radiusKm / KM_PER_DEGREE_OF_LATITUDE;
        double lngDelta = radiusKm / (KM_PER_DEGREE_OF_LATITUDE
                * Math.max(Math.cos(Math.toRadians(center.latitude())), 0.01));

        return springData.findUpcomingAround(
                        center.latitude(), center.longitude(),
                        center.latitude() - latDelta, center.latitude() + latDelta,
                        center.longitude() - lngDelta, center.longitude() + lngDelta,
                        radiusKm, now, limit)
                .stream()
                .map(JpaEventRepository::toDomain)
                .toList();
    }
}
