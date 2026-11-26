package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventTickets;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest {

    private final Event event = anEvent()
            .withId("evt_1")
            .withStartAt(Instant.parse("2026-12-29T17:00:00Z"))
            .build();

    private final EventRepository repository = new EventRepository() {
        @Override
        public void save(Event e) {
        }

        @Override
        public Optional<Event> findById(String id) {
            return id.equals(event.id()) ? Optional.of(event) : Optional.empty();
        }

        @Override
        public void deleteById(String id) {
        }
    };

    // stock restant : 5 Standard (21 au départ), Premium épuisé (78 au départ)
    private final TicketAvailability availability = _ -> Map.of("tkt_1", 5, "tkt_2", 0);

    private final GetEvent getEvent = new GetEvent(repository,
            new EventSummaryAssembler(_ -> Map.of("evt_1", 4L), availability));

    private final GetEventTickets getEventTickets = new GetEventTickets(repository, availability);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new EventController(getEvent, getEventTickets))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returns_the_event_in_the_api_format() throws Exception {
        mockMvc.perform(get("/events/evt_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("evt_1"))
                .andExpect(jsonPath("$.title").value("Brunch"))
                .andExpect(jsonPath("$.likedCount").value(4))
                .andExpect(jsonPath("$.isSoldOut").value(false))
                .andExpect(jsonPath("$.lowestPrice").value(2751))
                .andExpect(jsonPath("$.startAt").value("2026-12-29T17:00:00Z"))
                .andExpect(jsonPath("$.venue.adresse").value("111 Place Voltaire, 31000 Toulouse"))
                .andExpect(jsonPath("$.venue.coordinates.latitude").value(43.626))
                .andExpect(jsonPath("$.venue.coordinates.longitude").value(1.455));
    }

    @Test
    void returns_404_when_the_event_does_not_exist() throws Exception {
        mockMvc.perform(get("/events/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Event not found: unknown"));
    }

    @Test
    void returns_the_tickets_with_their_remaining_stock_in_the_api_format() throws Exception {
        mockMvc.perform(get("/events/evt_1/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("tkt_1"))
                .andExpect(jsonPath("$[0].eventId").value("evt_1"))
                .andExpect(jsonPath("$[0].name").value("Standard"))
                .andExpect(jsonPath("$[0].price").value(2751))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[1].id").value("tkt_2"))
                .andExpect(jsonPath("$[1].quantity").value(0));
    }

    @Test
    void returns_404_for_the_tickets_of_an_unknown_event() throws Exception {
        mockMvc.perform(get("/events/unknown/tickets"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Event not found: unknown"));
    }
}
