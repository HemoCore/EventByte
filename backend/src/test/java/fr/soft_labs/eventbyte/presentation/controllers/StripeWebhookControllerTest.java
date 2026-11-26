package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.usecases.booking.FakePaymentGateway;
import fr.soft_labs.eventbyte.domain.usecases.booking.HandlePaymentResult;
import fr.soft_labs.eventbyte.domain.usecases.booking.InMemoryBookingRepository;
import fr.soft_labs.eventbyte.infrastructure.payment.StripeWebhookParser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The Stripe-Signature header is computed exactly like Stripe does: t=<time>,v1=HMAC-SHA256(secret, t.payload). */
class StripeWebhookControllerTest {

    private static final String SECRET = "whsec_test_secret";

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final FakePaymentGateway payments = new FakePaymentGateway();

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new StripeWebhookController(
                    new StripeWebhookParser(SECRET), new HandlePaymentResult(repository, payments)))
            .build();

    private void givenBooking(String id, BookingStatus status, String paymentId) {
        repository.save(new Booking(id, new UserId("user-1"), "evt_1", Instant.parse("2026-11-25T10:00:00Z"),
                List.of(new BookingLine("tkt_vip", "VIP", 1, Money.ofCents(5000))),
                status, paymentId, paymentId + "_secret_x"));
    }

    private BookingStatus statusOf(String id) {
        return repository.findById(id).orElseThrow().status();
    }

    private static String event(String type, String paymentId) {
        return """
                {"id":"evt_test_1","object":"event","api_version":"2025-01-27.acacia","created":1764000000,
                 "type":"%s","data":{"object":{"id":"%s","object":"payment_intent","amount":5030,
                 "currency":"eur","status":"succeeded"}}}""".formatted(type, paymentId);
    }

    private static String signature(String payload, String secret) throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String v1 = HexFormat.of().formatHex(
                mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8)));
        return "t=" + timestamp + ",v1=" + v1;
    }

    private static MockHttpServletRequestBuilder webhook(String payload, String signature) {
        MockHttpServletRequestBuilder request = post("/webhooks/stripe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        return signature == null ? request : request.header("Stripe-Signature", signature);
    }

    @Test
    void a_signed_succeeded_event_confirms_the_booking() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String payload = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void the_same_event_twice_answers_200_and_keeps_the_booking_confirmed() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String payload = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());
        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void a_late_payment_is_refunded_and_answers_200() throws Exception {
        givenBooking("bkg_1", BookingStatus.EXPIRED, "pi_1");
        String payload = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(payments.refunded).containsExactly("pi_1");
    }

    @Test
    void a_failed_refund_answers_503_so_stripe_retries() throws Exception {
        givenBooking("bkg_1", BookingStatus.EXPIRED, "pi_1");
        payments.refundFailing = true;
        String payload = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isServiceUnavailable());
    }

    @Test
    void a_failed_payment_answers_200_and_changes_nothing() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String payload = event("payment_intent.payment_failed", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
    }

    @Test
    void another_event_type_is_ignored_with_200() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String payload = event("payment_intent.created", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isOk());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
    }

    @Test
    void a_wrong_signature_answers_400_and_changes_nothing() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String payload = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(payload, signature(payload, "whsec_someone_else")))
                .andExpect(status().isBadRequest());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
    }

    @Test
    void a_missing_signature_answers_400() throws Exception {
        mockMvc.perform(webhook(event("payment_intent.succeeded", "pi_1"), null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void a_payload_changed_after_signing_answers_400() throws Exception {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");
        String signed = event("payment_intent.payment_failed", "pi_1");
        String tampered = event("payment_intent.succeeded", "pi_1");

        mockMvc.perform(webhook(tampered, signature(signed, SECRET))).andExpect(status().isBadRequest());

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
    }

    @Test
    void without_a_webhook_secret_every_webhook_is_refused() throws Exception {
        MockMvc noSecret = MockMvcBuilders
                .standaloneSetup(new StripeWebhookController(
                        new StripeWebhookParser(""), new HandlePaymentResult(repository, payments)))
                .build();
        String payload = event("payment_intent.succeeded", "pi_1");

        noSecret.perform(webhook(payload, signature(payload, SECRET))).andExpect(status().isBadRequest());
    }
}
