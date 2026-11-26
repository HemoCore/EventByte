package fr.soft_labs.eventbyte.infrastructure.search;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class MeilisearchEventSearchIndexTest {

    @Test
    void the_filter_keeps_the_events_in_the_radius_that_have_not_started() {
        String filter = MeilisearchEventSearchIndex.filter(
                new Coordinates(48.8585, 2.3475), 12.5, Instant.parse("2026-11-25T12:00:00Z"));

        assertThat(filter).isEqualTo("_geoRadius(48.858500, 2.347500, 12500) AND startAt >= 1795608000");
    }

    @Test
    void the_filter_uses_dots_even_with_a_french_locale() {
        Locale previous = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
        try {
            String filter = MeilisearchEventSearchIndex.filter(
                    new Coordinates(43.6047, 1.4442), 5, Instant.EPOCH);

            assertThat(filter).startsWith("_geoRadius(43.604700, 1.444200, 5000)");
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void the_document_holds_the_searchable_fields_and_the_position() {
        Map<String, Object> document = MeilisearchEventSearchIndex.toDocument(anEvent()
                .withId("evt_1")
                .withStartAt(Instant.parse("2026-12-29T17:00:00Z"))
                .build());

        assertThat(document)
                .containsEntry("id", "evt_1")
                .containsEntry("title", "Brunch")
                .containsEntry("description", "Brunch with live music")
                .containsEntry("venueName", "Restaurant Prosacco")
                .containsEntry("venueAddress", "111 Place Voltaire, 31000 Toulouse")
                .containsEntry("_geo", Map.of("lat", 43.626, "lng", 1.455))
                .containsEntry("startAt", Instant.parse("2026-12-29T17:00:00Z").getEpochSecond());
    }
}
