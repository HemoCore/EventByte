package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration sur la base Postgres de docker compose.
 * Chaque test est annulé à la fin (@Transactional) : aucune donnée ne reste en base.
 */
@SpringBootTest
@Transactional
class JpaLikeCounterTest {

    @Autowired
    private JpaLikeCounter likeCounter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createEvents() {
        insertEvent("evt_test_a");
        insertEvent("evt_test_b");
        insertEvent("evt_test_c");
    }

    @Test
    void counts_the_likes_of_several_events_at_once() {
        like("user_1", "evt_test_a");
        like("user_2", "evt_test_a");
        like("user_3", "evt_test_a");
        like("user_1", "evt_test_b");

        var counts = likeCounter.countByEventIds(List.of("evt_test_a", "evt_test_b", "evt_test_c"));

        assertThat(counts).containsEntry("evt_test_a", 3L).containsEntry("evt_test_b", 1L);
        // Un événement sans like n'est pas dans la map : il compte pour 0
        assertThat(counts).doesNotContainKey("evt_test_c");
    }

    @Test
    void only_counts_the_requested_events() {
        like("user_1", "evt_test_a");
        like("user_1", "evt_test_b");

        assertThat(likeCounter.countByEventIds(List.of("evt_test_b"))).containsOnlyKeys("evt_test_b");
    }

    @Test
    void returns_an_empty_map_for_no_events() {
        assertThat(likeCounter.countByEventIds(List.of())).isEmpty();
    }

    private void insertEvent(String id) {
        entityManager.createNativeQuery("""
                        INSERT INTO events (id, title, venue_name, venue_address, venue_latitude, venue_longitude)
                        VALUES (:id, 'Test', 'Salle', 'Adresse', 48.85, 2.35)
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    private void like(String userId, String eventId) {
        entityManager.createNativeQuery("INSERT INTO likes (user_id, event_id) VALUES (:userId, :eventId)")
                .setParameter("userId", userId)
                .setParameter("eventId", eventId)
                .executeUpdate();
    }
}
