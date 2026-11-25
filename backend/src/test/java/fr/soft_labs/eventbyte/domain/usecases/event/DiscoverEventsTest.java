package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.event.Venue;
import fr.soft_labs.eventbyte.domain.ports.event.NearbyEvents;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class DiscoverEventsTest {

    private static final Instant NOW = Instant.parse("2026-11-25T12:00:00Z");
    private static final Coordinates PARIS = new Coordinates(48.8585, 2.3475);

    private static final Venue IN_PARIS = new Venue("Bercy", "8 Bd de Bercy, Paris", 48.8386, 2.3785);
    private static final Venue IN_VERSAILLES = new Venue("Château", "Place d'Armes, Versailles", 48.8049, 2.1204);
    private static final Venue IN_LYON = new Venue("Halle Tony Garnier", "20 Pl. Docteurs, Lyon", 45.7319, 4.8188);

    private static class InMemoryNearbyEvents implements NearbyEvents {
        final List<Event> events = new ArrayList<>();

        @Override
        public List<Event> findUpcomingAround(Coordinates center, double radiusKm, Instant now, int limit) {
            return events.stream()
                    .filter(e -> e.startAt() != null && !e.startAt().isBefore(now))
                    .filter(e -> center.distanceKmTo(
                            new Coordinates(e.venue().latitude(), e.venue().longitude())) <= radiusKm)
                    .sorted(Comparator.comparing(Event::startAt))
                    .limit(limit)
                    .toList();
        }
    }

    private final InMemoryNearbyEvents nearbyEvents = new InMemoryNearbyEvents();
    private final DiscoverEvents discoverEvents = new DiscoverEvents(
            nearbyEvents,
            new EventSummaryAssembler(ids -> Map.of(),
                    tickets -> tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity, (a, b) -> a))),
            Clock.fixed(NOW, ZoneOffset.UTC));

    private void addEvent(String id, Venue venue, String startAt) {
        nearbyEvents.events.add(anEvent().withId(id).withVenue(venue).withStartAt(Instant.parse(startAt)).build());
    }

    private List<String> discoveredIds() {
        return discoverEvents.execute(PARIS).stream().map(s -> s.event().id()).toList();
    }

    @Test
    void finds_the_events_around_the_position() {
        addEvent("bercy", IN_PARIS, "2026-12-01T20:00:00Z");
        addEvent("versailles", IN_VERSAILLES, "2026-12-02T20:00:00Z");

        assertThat(discoveredIds()).containsExactly("bercy", "versailles");
    }

    @Test
    void ignores_the_events_more_than_50_km_away() {
        addEvent("bercy", IN_PARIS, "2026-12-01T20:00:00Z");
        addEvent("lyon", IN_LYON, "2026-12-01T20:00:00Z");

        assertThat(discoveredIds()).containsExactly("bercy");
    }

    @Test
    void ignores_the_events_already_started() {
        addEvent("past", IN_PARIS, "2026-11-20T20:00:00Z");
        addEvent("future", IN_PARIS, "2026-12-01T20:00:00Z");

        assertThat(discoveredIds()).containsExactly("future");
    }

    @Test
    void the_soonest_events_come_first() {
        addEvent("later", IN_PARIS, "2027-01-15T20:00:00Z");
        addEvent("sooner", IN_VERSAILLES, "2026-12-01T20:00:00Z");

        assertThat(discoveredIds()).containsExactly("sooner", "later");
    }

    @Test
    void returns_at_most_50_events() {
        for (int i = 0; i < 60; i++) {
            addEvent("evt_" + i, IN_PARIS, "2026-12-01T20:00:00Z");
        }

        assertThat(discoveredIds()).hasSize(DiscoverEvents.MAX_RESULTS);
    }

    @Test
    void each_event_comes_with_its_computed_fields() {
        addEvent("bercy", IN_PARIS, "2026-12-01T20:00:00Z");

        EventSummary summary = discoverEvents.execute(PARIS).getFirst();

        assertThat(summary.likedCount()).isZero();
        assertThat(summary.soldOut()).isFalse();
        assertThat(summary.lowestPrice().cents()).isEqualTo(2751);
    }
}
