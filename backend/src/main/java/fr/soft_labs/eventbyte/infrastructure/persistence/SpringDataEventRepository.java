package fr.soft_labs.eventbyte.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

interface SpringDataEventRepository extends JpaRepository<EventEntity, String> {

    @Query(value = """
            SELECT * FROM events e
            WHERE e.venue_latitude BETWEEN :minLat AND :maxLat
              AND e.venue_longitude BETWEEN :minLng AND :maxLng
              AND e.start_at >= :now
              AND 2 * 6371 * asin(sqrt(
                    power(sin(radians(e.venue_latitude - :lat) / 2), 2)
                    + cos(radians(:lat)) * cos(radians(e.venue_latitude))
                    * power(sin(radians(e.venue_longitude - :lng) / 2), 2))) <= :radiusKm
            ORDER BY e.start_at, e.id
            LIMIT :limit
            """, nativeQuery = true)
    List<EventEntity> findUpcomingAround(@Param("lat") double lat,
                                         @Param("lng") double lng,
                                         @Param("minLat") double minLat,
                                         @Param("maxLat") double maxLat,
                                         @Param("minLng") double minLng,
                                         @Param("maxLng") double maxLng,
                                         @Param("radiusKm") double radiusKm,
                                         @Param("now") Instant now,
                                         @Param("limit") int limit);
}
