package fr.soft_labs.eventbyte.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataLikeRepository extends JpaRepository<LikeEntity, LikeId> {

    long countByIdEventId(String eventId);

    @Modifying
    @Query(value = "INSERT INTO likes (user_id, event_id) VALUES (:userId, :eventId) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void insertIfAbsent(@Param("userId") String userId, @Param("eventId") String eventId);
}