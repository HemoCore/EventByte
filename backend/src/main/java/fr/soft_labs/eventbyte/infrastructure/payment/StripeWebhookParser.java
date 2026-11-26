package fr.soft_labs.eventbyte.infrastructure.payment;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * US-17: checks that a webhook really comes from Stripe (Stripe-Signature header, HMAC with the signing secret)
 * and turns it into domain values. Stripe types never leave this class.
 */
@Component
public class StripeWebhookParser {

    /** A payment result, in domain terms. */
    public record PaymentEvent(String paymentId, PaymentOutcome outcome) {
    }

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookParser.class);

    private final String webhookSecret;

    public StripeWebhookParser(@Value("${stripe.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret == null ? "" : webhookSecret.trim();
        if (this.webhookSecret.isEmpty()) {
            log.warn("STRIPE_WEBHOOK_SECRET is not set: Stripe webhooks will be refused, bookings stay 'awaiting-payment'");
        }
    }

    /**
     * @return the payment result, or empty for an event we don't handle (Stripe sends many types)
     * @throws InvalidStripeWebhookException if the signature is missing or wrong
     */
    public Optional<PaymentEvent> parse(String payload, String signatureHeader) {
        if (webhookSecret.isEmpty()) {
            throw new InvalidStripeWebhookException("STRIPE_WEBHOOK_SECRET is not configured", null);
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new InvalidStripeWebhookException("Missing Stripe-Signature header", null);
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new InvalidStripeWebhookException("Invalid Stripe signature", e);
        } catch (RuntimeException e) {
            throw new InvalidStripeWebhookException("Unreadable Stripe event", e);
        }

        PaymentOutcome outcome = switch (event.getType()) {
            case "payment_intent.succeeded" -> PaymentOutcome.SUCCEEDED;
            case "payment_intent.payment_failed" -> PaymentOutcome.FAILED;
            default -> null;
        };
        if (outcome == null) {
            return Optional.empty();
        }
        return Optional.of(new PaymentEvent(paymentIntentId(event), outcome));
    }

    private static String paymentIntentId(Event event) {
        try {
            // deserializeUnsafe: also works when the event's API version differs from the library's
            StripeObject object = event.getDataObjectDeserializer().deserializeUnsafe();
            return ((PaymentIntent) object).getId();
        } catch (EventDataObjectDeserializationException | ClassCastException e) {
            throw new InvalidStripeWebhookException("Stripe event without a PaymentIntent", e);
        }
    }
}
