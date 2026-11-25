package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.usecases.event.DiscoverEvents;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DiscoverEventsControllerTest {

    private final Event event = anEvent()
            .withId("evt_1")
            .withStartAt(Instant.parse("2026-12-29T17:00:00Z"))
            .build();

    private final DiscoverEvents discoverEvents = new DiscoverEvents(
            (center, radiusKm, now, limit) -> List.of(event),
            new EventSummaryAssembler(ids -> Map.of("evt_1", 2L), tickets -> Map.of("tkt_1", 0, "tkt_2", 0)),
            Clock.systemUTC());

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new DiscoverEventsController(discoverEvents))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returns_the_events_in_the_api_format() throws Exception {
        mockMvc.perform(get("/events/discover").param("lat", "43.6047").param("lng", "1.4442"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("evt_1"))
                .andExpect(jsonPath("$[0].likedCount").value(2))
                .andExpect(jsonPath("$[0].isSoldOut").value(true))
                .andExpect(jsonPath("$[0].lowestPrice").value(2751))
                .andExpect(jsonPath("$[0].startAt").value("2026-12-29T17:00:00Z"))
                .andExpect(jsonPath("$[0].venue.adresse").exists())
                .andExpect(jsonPath("$[0].venue.coordinates.latitude").exists());
    }

    @Test
    void returns_400_when_the_position_is_missing() throws Exception {
        mockMvc.perform(get("/events/discover").param("lat", "43.6"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns_400_when_the_position_is_invalid() throws Exception {
        mockMvc.perform(get("/events/discover").param("lat", "120").param("lng", "1.4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Latitude invalide : 120.0"));
    }
}
