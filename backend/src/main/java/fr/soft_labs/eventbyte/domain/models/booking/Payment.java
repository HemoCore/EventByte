package fr.soft_labs.eventbyte.domain.models.booking;

/**
 * US-16: a payment created for a booking (a Stripe PaymentIntent, seen from the domain).
 * id = what the payment provider uses to find it again (US-17 webhook),
 * clientSecret = what the frontend needs to pay with the card.
 */
public record Payment(String id, String clientSecret) {

    public Payment {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Payment id is required");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("Payment client secret is required");
        }
    }
}
