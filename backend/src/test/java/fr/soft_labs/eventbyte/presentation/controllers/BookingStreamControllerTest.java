package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.infrastructure.sse.SseBookingNotifier;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

class BookingStreamControllerTest {

    private static final UserId ALICE = new UserId("alice");

    private final List<UserId> subscribed = new ArrayList<>();

    private final SseBookingNotifier notifier = new SseBookingNotifier() {
        @Override
        public SseEmitter subscribe(UserId userId) {
            subscribed.add(userId);
            return super.subscribe(userId);
        }
    };

    private final CurrentUser currentUser = new CurrentUser() {
        @Override
        public UserId get() {
            return ALICE;
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new BookingStreamController(notifier, currentUser))
            .build();

    @Test
    void opens_a_stream_for_the_connected_user() throws Exception {
        mockMvc.perform(get("/bookings/stream"))
                .andExpect(request().asyncStarted()); // the connection stays open

        assertThat(subscribed).containsExactly(ALICE);
    }
}