package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.ports.booking.PaymentGateway;

import java.util.ArrayList;
import java.util.List;

/** Records every call; each operation can be switched to "Stripe is down". */
public class FakePaymentGateway implements PaymentGateway {

    public record Call(String bookingId, Money amount) {
    }

    public final List<Call> calls = new ArrayList<>();
    public final List<String> cancelled = new ArrayList<>();
    public final List<String> refunded = new ArrayList<>();
    public boolean failing = false;
    public boolean cancelFailing = false;
    public boolean refundFailing = false;

    @Override
    public Payment createPayment(String bookingId, Money amount) {
        calls.add(new Call(bookingId, amount));
        if (failing) {
            throw new PaymentFailedException("Stripe is down", null);
        }
        return new Payment("pi_" + bookingId, "pi_" + bookingId + "_secret_123");
    }

    @Override
    public void cancel(String paymentId) {
        if (cancelFailing) {
            throw new PaymentFailedException("Stripe is down", null);
        }
        cancelled.add(paymentId);
    }

    @Override
    public void refund(String paymentId) {
        if (refundFailing) {
            throw new PaymentFailedException("Stripe is down", null);
        }
        refunded.add(paymentId);
    }
}
