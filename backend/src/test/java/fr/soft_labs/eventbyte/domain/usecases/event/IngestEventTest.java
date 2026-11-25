package fr.soft_labs.eventbyte.domain.usecases.event;

import org.junit.jupiter.api.Test;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class IngestEventTest {

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final IngestEvent ingestEvent = new IngestEvent(repository);

    @Test
    void saves_a_new_event() {
        ingestEvent.execute(anEvent().withId("evt_1").build());

        assertThat(repository.findById("evt_1")).isPresent();
    }

    @Test
    void a_message_received_twice_does_not_create_a_duplicate() {
        ingestEvent.execute(anEvent().withId("evt_1").build());
        ingestEvent.execute(anEvent().withId("evt_1").build());

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void an_update_replaces_the_existing_event() {
        ingestEvent.execute(anEvent().withId("evt_1").withTitle("Brunch").build());
        ingestEvent.execute(anEvent().withId("evt_1").withTitle("Musical brunch").build());

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById("evt_1").orElseThrow().title()).isEqualTo("Musical brunch");
    }

    @Test
    void saves_the_venue_and_tickets_with_the_event() {
        ingestEvent.execute(anEvent().withId("evt_1").build());

        var saved = repository.findById("evt_1").orElseThrow();
        assertThat(saved.venue().name()).isEqualTo("Restaurant Prosacco");
        assertThat(saved.tickets()).hasSize(2);
    }
}