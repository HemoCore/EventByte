package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentOutcome;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.PaymentGateway;

import java.util.Optional;

/**
 * US-17: the payment provider tells us the result of a payment.
 * <ul>
 *   <li>succeeded + awaiting payment → CONFIRMED</li>
 *   <li>succeeded + already confirmed → nothing (the provider can send the same event twice)</li>
 *   <li>succeeded + EXPIRED → refund, the booking stays expired: its seats may already be sold again,
 *       confirming it could oversell (US-13)</li>
 *   <li>failed → nothing: the user can retry with the same payment form until the booking expires</li>
 *   <li>unknown payment → nothing</li>
 * </ul>
 */
public class HandlePaymentResult {

    public enum Result {
        CONFIRMED,
        REFUNDED,
        IGNORED
    }

    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;

    public HandlePaymentResult(BookingRepository bookingRepository, PaymentGateway paymentGateway) {
        this.bookingRepository = bookingRepository;
        this.paymentGateway = paymentGateway;
    }

    /** @throws fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException if a late payment cannot be refunded */
    public Result execute(String paymentId, PaymentOutcome outcome) {
        Optional<Booking> found = bookingRepository.findByPaymentId(paymentId);
        if (found.isEmpty() || outcome != PaymentOutcome.SUCCEEDED) {
            return Result.IGNORED;
        }
        Booking booking = found.get();

        if (booking.status() == BookingStatus.AWAITING_PAYMENT) {
            booking.confirm();
            bookingRepository.save(booking);
            return Result.CONFIRMED;
        }
        if (booking.status() == BookingStatus.EXPIRED) {
            paymentGateway.refund(paymentId);
            return Result.REFUNDED;
        }
        return Result.IGNORED; // already confirmed (duplicate event)
    }
}
