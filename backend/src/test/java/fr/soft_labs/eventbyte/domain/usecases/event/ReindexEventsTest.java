package fr.soft_labs.eventbyte.domain.usecases.event;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class ReindexEventsTest {

    private final InMemoryEventSearchIndex searchIndex = new InMemoryEventSearchIndex();

    @Test
    void sends_every_stored_event_to_the_search_index() {
        ReindexEvents reindexEvents = new ReindexEvents(
                () -> List.of(anEvent().withId("evt_1").build(), anEvent().withId("evt_2").build()),
                searchIndex);

        int indexed = reindexEvents.execute();

        assertThat(indexed).isEqualTo(2);
        assertThat(searchIndex.contains("evt_1")).isTrue();
        assertThat(searchIndex.contains("evt_2")).isTrue();
    }

    @Test
    void does_not_call_the_index_when_there_is_no_event() {
        ReindexEvents reindexEvents = new ReindexEvents(List::of, searchIndex);

        assertThat(reindexEvents.execute()).isZero();
        assertThat(searchIndex.indexCalls()).isZero();
    }
}
