package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.usecases.booking.GetBooking;
import fr.soft_labs.eventbyte.domain.usecases.booking.GetUserBookings;
import fr.soft_labs.eventbyte.domain.usecases.booking.InMemoryBookingRepository;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetBookingsControllerTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();

    private final CurrentUser currentUser = new CurrentUser() {
        @Override
        public UserId get() {
            return ALICE;
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new GetBookingsController(
                    new GetBooking(repository), new GetUserBookings(repository), currentUser))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    private void givenBooking(String id, UserId owner, String bookedAt, BookingStatus status) {
        repository.save(new Booking(id, owner, "evt_1", Instant.parse(bookedAt), List.of(
                new BookingLine("tkt_vip", "VIP", 2, Money.ofCents(5000)),
                new BookingLine("tkt_early", "Early Bird", 1, Money.ofCents(1500))), status));
    }

    @Test
    void returns_my_booking_in_the_api_format() throws Exception {
        givenBooking("bkg_1", ALICE, "2026-11-25T10:00:00Z", BookingStatus.AWAITING_PAYMENT);

        mockMvc.perform(get("/bookings/bkg_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("bkg_1"))
                .andExpect(jsonPath("$.eventId").value("evt_1"))
                .andExpect(jsonPath("$.bookedAt").value("2026-11-25T10:00:00Z"))
                .andExpect(jsonPath("$.lines.length()").value(2))
                .andExpect(jsonPath("$.lines[0].id").value("tkt_vip"))
                .andExpect(jsonPath("$.lines[0].name").value("VIP"))
                .andExpect(jsonPath("$.lines[0].quantity").value(2))
                .andExpect(jsonPath("$.lines[0].price").value(5000))
                .andExpect(jsonPath("$.status").value("awaiting-payment"))
                .andExpect(jsonPath("$.total").value(11500))
                .andExpect(jsonPath("$.fees").value(0));
    }

    @Test
    void returns_404_when_the_booking_does_not_exist() throws Exception {
        mockMvc.perform(get("/bookings/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Booking not found: unknown"));
    }

    @Test
    void returns_404_for_the_booking_of_another_user() throws Exception {
        givenBooking("bkg_bob", BOB, "2026-11-25T10:00:00Z", BookingStatus.RESERVED);

        mockMvc.perform(get("/bookings/bkg_bob"))
                .andExpect(status().isNotFound());
    }

    @Test
    void lists_only_my_bookings_most_recent_first() throws Exception {
        givenBooking("bkg_old", ALICE, "2026-11-20T10:00:00Z", BookingStatus.EXPIRED);
        givenBooking("bkg_bob", BOB, "2026-11-22T10:00:00Z", BookingStatus.RESERVED);
        givenBooking("bkg_new", ALICE, "2026-11-25T10:00:00Z", BookingStatus.RESERVED);

        mockMvc.perform(get("/bookings/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("bkg_new"))
                .andExpect(jsonPath("$[0].status").value("reserved"))
                .andExpect(jsonPath("$[1].id").value("bkg_old"))
                .andExpect(jsonPath("$[1].status").value("expired"));
    }
}