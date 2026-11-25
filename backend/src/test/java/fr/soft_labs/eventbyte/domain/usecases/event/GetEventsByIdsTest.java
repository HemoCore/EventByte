package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class GetEventsByIdsTest {

    private final List<Event> stored = new ArrayList<>();
    private final List<List<String>> requestedToAdapter = new ArrayList<>();

    private final GetEventsByIds getEventsByIds = new GetEventsByIds(
            ids -> {
                requestedToAdapter.add(List.copyOf(ids));
                return stored.stream().filter(e -> ids.contains(e.id())).toList();
            },
            new EventSummaryAssembler(
                    ids -> Map.of("evt_b", 7L),
                    tickets -> tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity, (a, b) -> a))));

    private void store(String... ids) {
        Arrays.stream(ids).forEach(id -> stored.add(anEvent().withId(id).build()));
    }

    private List<String> foundIds(String... ids) {
        return getEventsByIds.execute(List.of(ids)).stream().map(s -> s.event().id()).toList();
    }

    @Test
    void returns_the_requested_events() {
        store("evt_a", "evt_b", "evt_c");

        assertThat(foundIds("evt_a", "evt_c")).containsExactly("evt_a", "evt_c");
    }

    @Test
    void keeps_the_order_of_the_requested_ids() {
        store("evt_a", "evt_b", "evt_c");

        assertThat(foundIds("evt_c", "evt_a", "evt_b")).containsExactly("evt_c", "evt_a", "evt_b");
    }

    @Test
    void ignores_the_unknown_ids() {
        store("evt_a");

        assertThat(foundIds("evt_a", "deleted_event")).containsExactly("evt_a");
    }

    @Test
    void returns_each_event_once_even_if_requested_twice() {
        store("evt_a");

        assertThat(foundIds("evt_a", "evt_a")).containsExactly("evt_a");
    }

    @Test
    void returns_nothing_without_calling_the_database_when_no_id_is_given() {
        assertThat(getEventsByIds.execute(List.of())).isEmpty();
        assertThat(getEventsByIds.execute(List.of("", " "))).isEmpty();
        assertThat(requestedToAdapter).isEmpty();
    }

    @Test
    void each_event_comes_with_its_computed_fields() {
        store("evt_a", "evt_b");

        var summaries = getEventsByIds.execute(List.of("evt_a", "evt_b"));

        assertThat(summaries.get(0).likedCount()).isZero();
        assertThat(summaries.get(1).likedCount()).isEqualTo(7);
        assertThat(summaries.get(1).lowestPrice().cents()).isEqualTo(2751);
    }
}
