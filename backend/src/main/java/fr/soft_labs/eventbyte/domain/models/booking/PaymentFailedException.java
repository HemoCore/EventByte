package fr.soft_labs.eventbyte.domain.models.booking;

/** US-16: the payment could not be created (no Stripe key, Stripe unreachable, refused...). */
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
