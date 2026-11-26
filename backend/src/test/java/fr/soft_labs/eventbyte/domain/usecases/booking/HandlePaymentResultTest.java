package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentFailedException;
import fr.soft_labs.eventbyte.domain.models.booking.PaymentOutcome;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static fr.soft_labs.eventbyte.domain.usecases.booking.HandlePaymentResult.Result.CONFIRMED;
import static fr.soft_labs.eventbyte.domain.usecases.booking.HandlePaymentResult.Result.IGNORED;
import static fr.soft_labs.eventbyte.domain.usecases.booking.HandlePaymentResult.Result.REFUNDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HandlePaymentResultTest {

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final FakePaymentGateway payments = new FakePaymentGateway();
    private final HandlePaymentResult handle = new HandlePaymentResult(repository, payments);

    private void givenBooking(String id, BookingStatus status, String paymentId) {
        repository.save(new Booking(id, new UserId("user-1"), "evt_1", Instant.parse("2026-11-25T10:00:00Z"),
                List.of(new BookingLine("tkt_vip", "VIP", 1, Money.ofCents(5000))),
                status, paymentId, paymentId == null ? null : paymentId + "_secret_x"));
    }

    private BookingStatus statusOf(String id) {
        return repository.findById(id).orElseThrow().status();
    }

    @Test
    void a_succeeded_payment_confirms_the_booking() {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");

        assertThat(handle.execute("pi_1", PaymentOutcome.SUCCEEDED)).isEqualTo(CONFIRMED);

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(payments.refunded).isEmpty();
    }

    @Test
    void the_same_event_twice_changes_nothing_the_second_time() {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");

        handle.execute("pi_1", PaymentOutcome.SUCCEEDED);
        assertThat(handle.execute("pi_1", PaymentOutcome.SUCCEEDED)).isEqualTo(IGNORED);

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(payments.refunded).isEmpty();
    }

    @Test
    void a_payment_after_the_expiry_is_refunded_and_the_booking_stays_expired() {
        givenBooking("bkg_1", BookingStatus.EXPIRED, "pi_1");

        assertThat(handle.execute("pi_1", PaymentOutcome.SUCCEEDED)).isEqualTo(REFUNDED);

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(payments.refunded).containsExactly("pi_1");
    }

    @Test
    void a_failed_refund_is_reported_so_stripe_sends_the_event_again() {
        givenBooking("bkg_1", BookingStatus.EXPIRED, "pi_1");
        payments.refundFailing = true;

        assertThatThrownBy(() -> handle.execute("pi_1", PaymentOutcome.SUCCEEDED))
                .isInstanceOf(PaymentFailedException.class);
        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    void a_failed_payment_leaves_the_booking_awaiting_payment() {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");

        assertThat(handle.execute("pi_1", PaymentOutcome.FAILED)).isEqualTo(IGNORED);

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
    }

    @Test
    void an_unknown_payment_is_ignored() {
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, "pi_1");

        assertThat(handle.execute("pi_unknown", PaymentOutcome.SUCCEEDED)).isEqualTo(IGNORED);

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
        assertThat(payments.refunded).isEmpty();
    }
}
