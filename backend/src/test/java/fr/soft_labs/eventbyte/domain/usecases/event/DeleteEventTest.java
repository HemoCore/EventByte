package fr.soft_labs.eventbyte.domain.usecases.event;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DeleteEventTest {

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final InMemoryEventSearchIndex searchIndex = new InMemoryEventSearchIndex();
    private final DeleteEvent deleteEvent = new DeleteEvent(repository, searchIndex);

    @Test
    void deletes_an_existing_event() {
        repository.save(anEvent().withId("evt_1").build());

        deleteEvent.execute("evt_1");

        assertThat(repository.findById("evt_1")).isEmpty();
    }

    @Test
    void deleting_twice_does_not_throw() {
        repository.save(anEvent().withId("evt_1").build());

        deleteEvent.execute("evt_1");

        assertThatCode(() -> deleteEvent.execute("evt_1")).doesNotThrowAnyException();
    }

    @Test
    void removes_the_event_from_the_search_index() {
        var event = anEvent().withId("evt_1").build();
        repository.save(event);
        searchIndex.index(List.of(event));

        deleteEvent.execute("evt_1");

        assertThat(searchIndex.contains("evt_1")).isFalse();
    }
}
