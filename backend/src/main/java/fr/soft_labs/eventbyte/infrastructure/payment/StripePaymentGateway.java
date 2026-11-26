package fr.soft_labs.eventbyte.infrastructure.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.ports.booking.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * US-16: creates a Stripe PaymentIntent; the frontend pays it with the clientSecret (stripe.confirmPayment).
 * US-17: cancels it when the booking expires, refunds it when a payment arrives after the expiry.
 */
@Component
public class StripePaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(StripePaymentGateway.class);

    private final String secretKey;

    public StripePaymentGateway(@Value("${stripe.secret-key:}") String secretKey) {
        this.secretKey = secretKey == null ? "" : secretKey.trim();
        if (this.secretKey.isEmpty()) {
            log.warn("STRIPE_SECRET_KEY is not set: bookings will stay 'reserved' and cannot be paid");
        }
    }

    @Override
    public Payment createPayment(String bookingId, Money amount) {
        requireKey("create the payment of booking " + bookingId);
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.cents())   // README: amounts in cents
                .setCurrency("eur")
                // the frontend uses Stripe Elements + confirmPayment: let Stripe choose the payment methods
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build())
                .putMetadata("bookingId", bookingId)
                .build();
        try {
            // same key for the same booking: a duplicated message never creates a 2nd payment
            PaymentIntent intent = PaymentIntent.create(params, options("booking-" + bookingId));
            return new Payment(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            throw failure("Stripe refused the payment of booking " + bookingId, e);
        }
    }

    @Override
    public void cancel(String paymentId) {
        requireKey("cancel the payment " + paymentId);
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentId, options(null));
            intent.cancel(PaymentIntentCancelParams.builder()
                    .setCancellationReason(PaymentIntentCancelParams.CancellationReason.ABANDONED)
                    .build(), options("cancel-" + paymentId));
            log.info("Payment {} cancelled (booking expired)", paymentId);
        } catch (StripeException e) {
            throw failure("Stripe could not cancel the payment " + paymentId, e);
        }
    }

    @Override
    public void refund(String paymentId) {
        requireKey("refund the payment " + paymentId);
        try {
            // same key for the same payment: a duplicated webhook never refunds twice
            Refund.create(RefundCreateParams.builder().setPaymentIntent(paymentId).build(),
                    options("refund-" + paymentId));
            log.warn("Payment {} arrived after the booking expired: refunded", paymentId);
        } catch (StripeException e) {
            throw failure("Stripe could not refund the payment " + paymentId, e);
        }
    }

    private RequestOptions options(String idempotencyKey) {
        RequestOptions.RequestOptionsBuilder builder = RequestOptions.builder().setApiKey(secretKey);
        if (idempotencyKey != null) {
            builder.setIdempotencyKey(idempotencyKey);
        }
        return builder.build();
    }

    private void requireKey(String action) {
        if (secretKey.isEmpty()) {
            log.error("Cannot {}: STRIPE_SECRET_KEY is not set", action);
            throw new PaymentFailedException("Stripe secret key is not configured", null);
        }
    }

    private PaymentFailedException failure(String message, StripeException e) {
        log.error("{}: {}", message, e.getMessage());
        return new PaymentFailedException(message + ": " + e.getMessage(), e);
    }
}
