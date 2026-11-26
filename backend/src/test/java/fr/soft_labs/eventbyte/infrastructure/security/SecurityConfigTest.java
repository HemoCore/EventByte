package fr.soft_labs.eventbyte.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.FakeController.class)
@Import({SecurityConfig.class, CurrentUser.class, SecurityConfigTest.FakeController.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @RestController
    static class FakeController {

        private final CurrentUser currentUser;

        FakeController(CurrentUser currentUser) {
            this.currentUser = currentUser;
        }

        @GetMapping("/events/discover")
        String discover() {
            return "ok";
        }

        @GetMapping("/events/liked")
        String liked() {
            return currentUser.get().value();
        }

        @PostMapping("/events/{eventId}/like")
        String like(@PathVariable String eventId) {
            return "ok";
        }

        @GetMapping("/bookings/user")
        String bookings() {
            return currentUser.get().value();
        }

        @PostMapping("/webhooks/stripe")
        String stripeWebhook() {
            return "ok";
        }
    }

    @Test
    void the_stripe_webhook_needs_no_token() throws Exception {
        // US-17: Stripe has no JWT; the controller checks the Stripe signature instead
        mockMvc.perform(post("/webhooks/stripe")).andExpect(status().isOk());
    }

    @Test
    void only_post_is_public_on_the_stripe_webhook() throws Exception {
        mockMvc.perform(get("/webhooks/stripe")).andExpect(status().isUnauthorized());
    }

    @Test
    void public_routes_do_not_require_a_token() throws Exception {
        mockMvc.perform(get("/events/discover"))
                .andExpect(status().isOk());
    }

    @Test
    void protected_routes_return_401_without_a_token() throws Exception {
        mockMvc.perform(get("/events/liked")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/events/evt_1/like")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/bookings/user")).andExpect(status().isUnauthorized());
    }

    @Test
    void protected_routes_accept_a_valid_token() throws Exception {
        mockMvc.perform(post("/events/evt_1/like").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void the_user_id_comes_from_the_token_subject() throws Exception {
        mockMvc.perform(get("/bookings/user").with(jwt().jwt(token -> token.subject("user-123"))))
                .andExpect(status().isOk())
                .andExpect(content().string("user-123"));
    }

    @Test
    void allows_cors_requests_from_the_frontend() throws Exception {
        mockMvc.perform(options("/events/discover")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void rejects_cors_requests_from_an_unknown_origin() throws Exception {
        mockMvc.perform(options("/events/discover")
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}