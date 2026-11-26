package fr.soft_labs.eventbyte.infrastructure.payment;

import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.RefundListParams;
import fr.soft_labs.eventbyte.domain.models.booking.Payment;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StripePaymentGatewayTest {

    @Test
    void without_a_key_every_call_fails_cleanly() {
        StripePaymentGateway gateway = new StripePaymentGateway("");

        assertThatThrownBy(() -> gateway.createPayment("bkg_1", Money.ofCents(1000)))
                .isInstanceOf(PaymentFailedException.class);
        assertThatThrownBy(() -> gateway.cancel("pi_1")).isInstanceOf(PaymentFailedException.class);
        assertThatThrownBy(() -> gateway.refund("pi_1")).isInstanceOf(PaymentFailedException.class);
    }

    // The tests below call the REAL Stripe API in test mode (no money is moved).
    // They only run when STRIPE_SECRET_KEY is set to a test key (sk_test_...); otherwise they are skipped.

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void creates_a_real_test_payment_intent() {
        StripePaymentGateway gateway = new StripePaymentGateway(System.getenv("STRIPE_SECRET_KEY"));

        Payment payment = gateway.createPayment("test_" + UUID.randomUUID(), Money.ofCents(10060));

        assertThat(payment.id()).startsWith("pi_");
        assertThat(payment.clientSecret()).startsWith(payment.id() + "_secret_");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void asking_twice_for_the_same_booking_returns_the_same_payment() {
        StripePaymentGateway gateway = new StripePaymentGateway(System.getenv("STRIPE_SECRET_KEY"));
        String bookingId = "test_" + UUID.randomUUID();

        Payment first = gateway.createPayment(bookingId, Money.ofCents(500));
        Payment second = gateway.createPayment(bookingId, Money.ofCents(500));

        assertThat(second.id()).isEqualTo(first.id());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void cancels_an_unpaid_payment() throws Exception {
        String key = System.getenv("STRIPE_SECRET_KEY");
        StripePaymentGateway gateway = new StripePaymentGateway(key);
        Payment payment = gateway.createPayment("test_" + UUID.randomUUID(), Money.ofCents(500));

        gateway.cancel(payment.id());

        assertThat(PaymentIntent.retrieve(payment.id(), RequestOptions.builder().setApiKey(key).build())
                .getStatus()).isEqualTo("canceled");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void refunds_a_paid_payment_once_even_if_asked_twice() throws Exception {
        String key = System.getenv("STRIPE_SECRET_KEY");
        RequestOptions options = RequestOptions.builder().setApiKey(key).build();
        StripePaymentGateway gateway = new StripePaymentGateway(key);
        Payment payment = gateway.createPayment("test_" + UUID.randomUUID(), Money.ofCents(500));
        // pay it with Stripe's test card, like the frontend does
        PaymentIntent.retrieve(payment.id(), options).confirm(PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa").setReturnUrl("https://example.com").build(), options);

        gateway.refund(payment.id());
        gateway.refund(payment.id());

        assertThat(Refund.list(RefundListParams.builder().setPaymentIntent(payment.id()).build(), options)
                .getData()).hasSize(1);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void cancelling_an_already_paid_payment_fails_cleanly() throws Exception {
        String key = System.getenv("STRIPE_SECRET_KEY");
        RequestOptions options = RequestOptions.builder().setApiKey(key).build();
        StripePaymentGateway gateway = new StripePaymentGateway(key);
        Payment payment = gateway.createPayment("test_" + UUID.randomUUID(), Money.ofCents(500));
        PaymentIntent.retrieve(payment.id(), options).confirm(PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa").setReturnUrl("https://example.com").build(), options);

        assertThatThrownBy(() -> gateway.cancel(payment.id())).isInstanceOf(PaymentFailedException.class);
        gateway.refund(payment.id()); // leave the test account clean
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "STRIPE_SECRET_KEY", matches = "sk_test_.+")
    void a_wrong_key_fails_cleanly() {
        StripePaymentGateway gateway = new StripePaymentGateway("sk_test_wrong_key");

        assertThatThrownBy(() -> gateway.createPayment("test_" + UUID.randomUUID(), Money.ofCents(500)))
                .isInstanceOf(PaymentFailedException.class);
    }
}
