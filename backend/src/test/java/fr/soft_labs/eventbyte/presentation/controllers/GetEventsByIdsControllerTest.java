package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventsByIds;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetEventsByIdsControllerTest {

    private final List<Event> stored = List.of(
            anEvent().withId("evt_a").withStartAt(Instant.parse("2026-12-29T17:00:00Z")).build(),
            anEvent().withId("evt_b").withStartAt(Instant.parse("2027-01-10T20:00:00Z")).build());

    private final GetEventsByIds getEventsByIds = new GetEventsByIds(
            ids -> stored.stream().filter(e -> ids.contains(e.id())).toList(),
            new EventSummaryAssembler(ids -> Map.of(), tickets -> Map.of("tkt_1", 5)));

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new GetEventsByIdsController(getEventsByIds)).build();

    @Test
    void splits_the_comma_separated_ids_and_returns_the_api_format() throws Exception {
        mockMvc.perform(get("/events").param("ids", "evt_b,evt_a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("evt_b"))
                .andExpect(jsonPath("$[1].id").value("evt_a"))
                .andExpect(jsonPath("$[0].startAt").value("2027-01-10T20:00:00Z"))
                .andExpect(jsonPath("$[0].isSoldOut").value(false))
                .andExpect(jsonPath("$[0].venue.adresse").exists())
                .andExpect(jsonPath("$[0].venue.coordinates.longitude").exists());
    }

    @Test
    void returns_an_empty_list_when_ids_is_empty_or_missing() throws Exception {
        mockMvc.perform(get("/events").param("ids", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
