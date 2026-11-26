package fr.soft_labs.eventbyte.domain.ports.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.event.Money;

/** All methods throw {@link fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException} on failure. */
public interface PaymentGateway {

    /**
     * US-16: create the payment the user will confirm with their card.
     * Asking twice for the same booking must return the same payment (no double charge).
     */
    Payment createPayment(String bookingId, Money amount);

    /** US-17: the booking expired before it was paid: refuse any later payment. */
    void cancel(String paymentId);

    /** US-17: a payment arrived after the expiry: give the money back. Asking twice refunds once. */
    void refund(String paymentId);
}
