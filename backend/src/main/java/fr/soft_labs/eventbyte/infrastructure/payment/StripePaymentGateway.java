package fr.soft_labs.eventbyte.infrastructure.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.ports.booking.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** US-16: creates a Stripe PaymentIntent; the frontend pays it with the clientSecret (stripe.confirmPayment). */
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
        if (secretKey.isEmpty()) {
            log.error("Cannot create the payment of booking {}: STRIPE_SECRET_KEY is not set", bookingId);
            throw new PaymentFailedException("Stripe secret key is not configured", null);
        }
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.cents())   // README: amounts in cents
                .setCurrency("eur")
                // the frontend uses Stripe Elements + confirmPayment: let Stripe choose the payment methods
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build())
                .putMetadata("bookingId", bookingId)
                .build();
        RequestOptions options = RequestOptions.builder()
                .setApiKey(secretKey)
                // same key for the same booking: a duplicated message never creates a 2nd payment
                .setIdempotencyKey("booking-" + bookingId)
                .build();
        try {
            PaymentIntent intent = PaymentIntent.create(params, options);
            return new Payment(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            log.error("Stripe refused the payment of booking {}: {}", bookingId, e.getMessage());
            throw new PaymentFailedException("Stripe payment creation failed: " + e.getMessage(), e);
        }
    }
}
