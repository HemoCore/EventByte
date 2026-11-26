package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.InMemoryEventSearchIndex;
import fr.soft_labs.eventbyte.domain.usecases.event.SearchEvents;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchEventsControllerTest {

    private final Event event = anEvent()
            .withId("evt_1")
            .withStartAt(Instant.parse("2026-12-29T17:00:00Z"))
            .build();

    private final InMemoryEventSearchIndex searchIndex = InMemoryEventSearchIndex.returning("evt_1");

    private final SearchEvents searchEvents = new SearchEvents(
            searchIndex,
            ids -> List.of(event),
            new EventSummaryAssembler(ids -> Map.of("evt_1", 3L), tickets -> Map.of("tkt_1", 5, "tkt_2", 0)),
            Clock.systemUTC());

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new SearchEventsController(searchEvents))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returns_the_matching_events_in_the_api_format() throws Exception {
        mockMvc.perform(get("/events/search")
                        .param("lat", "43.6047").param("lng", "1.4442")
                        .param("radius", "20").param("query", "brunch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("evt_1"))
                .andExpect(jsonPath("$[0].title").value("Brunch"))
                .andExpect(jsonPath("$[0].likedCount").value(3))
                .andExpect(jsonPath("$[0].isSoldOut").value(false))
                .andExpect(jsonPath("$[0].startAt").value("2026-12-29T17:00:00Z"))
                .andExpect(jsonPath("$[0].venue.coordinates.latitude").exists());
        assertThat(searchIndex.queries()).containsExactly("brunch");
    }

    @Test
    void the_query_is_optional() throws Exception {
        mockMvc.perform(get("/events/search")
                        .param("lat", "43.6047").param("lng", "1.4442").param("radius", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("evt_1"));
        assertThat(searchIndex.queries()).containsExactly("");
    }

    @Test
    void returns_400_when_the_radius_is_missing() throws Exception {
        mockMvc.perform(get("/events/search").param("lat", "43.6").param("lng", "1.4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns_400_when_the_radius_is_not_positive() throws Exception {
        mockMvc.perform(get("/events/search")
                        .param("lat", "43.6").param("lng", "1.4").param("radius", "-3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Rayon invalide : -3.0"));
    }

    @Test
    void returns_400_when_the_position_is_invalid() throws Exception {
        mockMvc.perform(get("/events/search")
                        .param("lat", "120").param("lng", "1.4").param("radius", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Latitude invalide : 120.0"));
    }
}
