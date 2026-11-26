package fr.soft_labs.eventbyte.domain.ports.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.event.Money;

public interface PaymentGateway {

    /**
     * US-16: create the payment the user will confirm with their card.
     * Asking twice for the same booking must return the same payment (no double charge).
     * @throws fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException if it cannot be created
     */
    Payment createPayment(String bookingId, Money amount);
}
