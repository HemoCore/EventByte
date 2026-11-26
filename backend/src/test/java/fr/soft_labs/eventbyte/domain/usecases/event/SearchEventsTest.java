package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.event.Venue;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchEventsTest {

    private static final Instant NOW = Instant.parse("2026-11-25T12:00:00Z");
    private static final Coordinates PARIS = new Coordinates(48.8585, 2.3475);

    private static final Venue BERCY = new Venue("Accor Arena", "8 Bd de Bercy, Paris", 48.8386, 2.3785);
    private static final Venue VERSAILLES = new Venue("Château", "Place d'Armes, Versailles", 48.8049, 2.1204);
    private static final Venue LYON = new Venue("Halle Tony Garnier", "20 Pl. Docteurs, Lyon", 45.7319, 4.8188);

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final InMemoryEventSearchIndex searchIndex = new InMemoryEventSearchIndex();
    private final EventSummaryAssembler assembler = new EventSummaryAssembler(
            ids -> Map.of(),
            tickets -> tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity, (a, b) -> a)));

    private final SearchEvents searchEvents = new SearchEvents(
            searchIndex, repository, assembler, Clock.fixed(NOW, ZoneOffset.UTC));

    private void addEvent(String id, String title, Venue venue, String startAt) {
        Event event = anEvent().withId(id).withTitle(title).withVenue(venue)
                .withStartAt(Instant.parse(startAt)).build();
        repository.save(event);
        searchIndex.index(List.of(event));
    }

    private List<String> search(String query, double radiusKm) {
        return searchEvents.execute(query, PARIS, radiusKm).stream()
                .map(summary -> summary.event().id())
                .toList();
    }

    @Test
    void finds_the_events_whose_title_matches_the_query() {
        addEvent("evt_jazz", "Concert de jazz", BERCY, "2026-12-01T20:00:00Z");
        addEvent("evt_rock", "Festival rock", BERCY, "2026-12-02T20:00:00Z");

        assertThat(search("jazz", 50)).containsExactly("evt_jazz");
    }

    @Test
    void also_matches_the_venue_name_ignoring_case() {
        addEvent("evt_1", "Concert", BERCY, "2026-12-01T20:00:00Z");

        assertThat(search("ACCOR", 50)).containsExactly("evt_1");
    }

    @Test
    void an_empty_query_returns_every_upcoming_event_in_the_radius() {
        addEvent("evt_1", "Concert", BERCY, "2026-12-01T20:00:00Z");
        addEvent("evt_2", "Visite", VERSAILLES, "2026-12-03T10:00:00Z");

        assertThat(search("  ", 50)).containsExactlyInAnyOrder("evt_1", "evt_2");
    }

    @Test
    void ignores_the_events_outside_the_radius() {
        addEvent("evt_paris", "Concert", BERCY, "2026-12-01T20:00:00Z");
        addEvent("evt_versailles", "Concert", VERSAILLES, "2026-12-01T20:00:00Z");
        addEvent("evt_lyon", "Concert", LYON, "2026-12-01T20:00:00Z");

        assertThat(search("concert", 10)).containsExactly("evt_paris");
        assertThat(search("concert", 30)).containsExactlyInAnyOrder("evt_paris", "evt_versailles");
    }

    @Test
    void ignores_the_events_already_started() {
        addEvent("evt_past", "Concert", BERCY, "2026-11-20T20:00:00Z");
        addEvent("evt_next", "Concert", BERCY, "2026-12-01T20:00:00Z");

        assertThat(search("concert", 50)).containsExactly("evt_next");
    }

    @Test
    void keeps_the_relevance_order_of_the_index() {
        Event first = anEvent().withId("evt_1").withStartAt(Instant.parse("2026-12-01T20:00:00Z")).build();
        Event second = anEvent().withId("evt_2").withStartAt(Instant.parse("2026-12-02T20:00:00Z")).build();
        repository.save(first);
        repository.save(second);
        SearchEvents byRelevance = new SearchEvents(
                InMemoryEventSearchIndex.returning("evt_2", "evt_1"),
                repository, assembler, Clock.fixed(NOW, ZoneOffset.UTC));

        List<EventSummary> found = byRelevance.execute("brunch", PARIS, 50);

        assertThat(found).extracting(summary -> summary.event().id()).containsExactly("evt_2", "evt_1");
    }

    @Test
    void skips_the_ids_that_are_no_longer_in_the_database() {
        SearchEvents withStaleIndex = new SearchEvents(
                InMemoryEventSearchIndex.returning("evt_deleted"),
                repository, assembler, Clock.fixed(NOW, ZoneOffset.UTC));

        assertThat(withStaleIndex.execute("brunch", PARIS, 50)).isEmpty();
    }

    @Test
    void returns_the_like_count_and_availability_of_each_event() {
        addEvent("evt_1", "Concert", BERCY, "2026-12-01T20:00:00Z");
        SearchEvents withLikes = new SearchEvents(searchIndex, repository,
                new EventSummaryAssembler(ids -> Map.of("evt_1", 7L), tickets -> Map.of()),
                Clock.fixed(NOW, ZoneOffset.UTC));

        EventSummary summary = withLikes.execute("concert", PARIS, 50).getFirst();

        assertThat(summary.likedCount()).isEqualTo(7);
        assertThat(summary.soldOut()).isTrue();
    }

    @Test
    void rejects_a_radius_that_is_not_positive() {
        assertThatThrownBy(() -> searchEvents.execute("jazz", PARIS, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rayon invalide : 0.0");
        assertThatThrownBy(() -> searchEvents.execute("jazz", PARIS, -5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
