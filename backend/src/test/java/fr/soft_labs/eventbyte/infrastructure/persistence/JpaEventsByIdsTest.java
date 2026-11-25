package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaEventsByIdsTest {

    @Autowired
    private JpaEventsByIds eventsByIds;

    @Autowired
    private EntityManager entityManager;

    @Test
    void loads_the_events_with_their_venue_and_tickets() {
        insertEvent("evt_test_a");
        insertEvent("evt_test_b");
        insertTicket("tkt_test_1", "evt_test_a", 1500, 10);
        insertTicket("tkt_test_2", "evt_test_a", 4500, 3);

        List<Event> events = eventsByIds.findAllByIds(List.of("evt_test_a", "evt_test_b", "unknown"));

        assertThat(events).extracting(Event::id).containsExactlyInAnyOrder("evt_test_a", "evt_test_b");
        Event withTickets = events.stream().filter(e -> e.id().equals("evt_test_a")).findFirst().orElseThrow();
        assertThat(withTickets.venue().name()).isEqualTo("Salle");
        assertThat(withTickets.tickets()).hasSize(2);
    }

    private void insertEvent(String id) {
        entityManager.createNativeQuery("""
                        INSERT INTO events (id, title, venue_name, venue_address, venue_latitude, venue_longitude)
                        VALUES (:id, 'Test', 'Salle', 'Adresse', 48.85, 2.35)
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    private void insertTicket(String id, String eventId, int priceCents, int quantity) {
        entityManager.createNativeQuery("""
                        INSERT INTO tickets (id, event_id, name, price_cents, quantity)
                        VALUES (:id, :eventId, 'Standard', :price, :quantity)
                        """)
                .setParameter("id", id)
                .setParameter("eventId", eventId)
                .setParameter("price", priceCents)
                .setParameter("quantity", quantity)
                .executeUpdate();
    }
}
