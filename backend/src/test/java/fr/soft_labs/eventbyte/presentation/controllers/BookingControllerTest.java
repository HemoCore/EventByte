package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog.TicketOffer;
import fr.soft_labs.eventbyte.domain.usecases.booking.RequestBooking;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerTest {

    private final Map<String, TicketOffer> offers = Map.of(
            "tkt_vip", new TicketOffer("tkt_vip", "evt_1", "VIP", Money.ofCents(5000), 10));

    private final TicketCatalog catalog = ids -> {
        Map<String, TicketOffer> found = new HashMap<>();
        ids.stream().filter(offers::containsKey).forEach(id -> found.put(id, offers.get(id)));
        return found;
    };

    private final List<Booking> saved = new ArrayList<>();

    private final BookingRepository repository = new BookingRepository() {
        @Override
        public void save(Booking booking) {
            saved.add(booking);
        }

        @Override
        public Optional<Booking> findById(String id) {
            return Optional.empty();
        }

        @Override
        public List<Booking> findExpirable(Instant bookedBefore) {
            return List.of();
        }
    };

    private final CurrentUser currentUser = new CurrentUser() {
        @Override
        public UserId get() {
            return new UserId("user-1");
        }
    };

    private final RequestBooking requestBooking = new RequestBooking(catalog, repository, id -> { },
            () -> "bkg_1", Clock.fixed(Instant.parse("2026-11-25T10:00:00Z"), ZoneOffset.UTC));

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new BookingController(requestBooking, currentUser))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returns_the_id_of_the_new_booking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [ { "id": "tkt_vip", "quantity": 2 } ] }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("bkg_1"));

        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().userId()).isEqualTo(new UserId("user-1"));
    }

    @Test
    void returns_404_for_an_unknown_ticket() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [ { "id": "nope", "quantity": 1 } ] }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Ticket not found: nope"));
    }

    @Test
    void returns_400_for_an_empty_request() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [] }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns_400_for_a_zero_quantity() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [ { "id": "tkt_vip", "quantity": 0 } ] }
                                """))
                .andExpect(status().isBadRequest());
    }
}
