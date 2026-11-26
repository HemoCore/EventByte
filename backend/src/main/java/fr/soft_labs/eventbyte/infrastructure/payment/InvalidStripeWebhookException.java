package fr.soft_labs.eventbyte.infrastructure.payment;

/** US-17: the webhook does not come from Stripe (bad or missing signature, or no signing secret configured). */
public class InvalidStripeWebhookException extends RuntimeException {
    public InvalidStripeWebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}
