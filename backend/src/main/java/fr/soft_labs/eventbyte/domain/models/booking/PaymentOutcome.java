package fr.soft_labs.eventbyte.domain.models.booking;

/** US-17: what the payment provider tells us about a payment (Stripe webhook), without any Stripe type. */
public enum PaymentOutcome {
    SUCCEEDED,
    FAILED
}
