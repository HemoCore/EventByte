package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.ports.booking.PaymentGateway;

import java.util.ArrayList;
import java.util.List;

/** Records every payment asked; can be switched to "Stripe is down". */
class FakePaymentGateway implements PaymentGateway {

    record Call(String bookingId, Money amount) {
    }

    final List<Call> calls = new ArrayList<>();
    boolean failing = false;

    @Override
    public Payment createPayment(String bookingId, Money amount) {
        calls.add(new Call(bookingId, amount));
        if (failing) {
            throw new PaymentFailedException("Stripe is down", null);
        }
        return new Payment("pi_" + bookingId, "pi_" + bookingId + "_secret_123");
    }
}
