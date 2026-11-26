package fr.soft_labs.eventbyte.domain.models.booking;

import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");
    private static final UserId USER = new UserId("user-1");

    private static final List<BookingLine> LINES = List.of(
            new BookingLine("tkt_1", "VIP", 2, Money.ofCents(5000)),
            new BookingLine("tkt_2", "Early", 1, Money.ofCents(1500)));

    private Booking newBooking() {
        return Booking.request("bkg_1", USER, "evt_1", NOW, LINES);
    }

    // --- creation ---

    @Test
    void a_new_booking_is_requested() {
        Booking booking = newBooking();

        assertThat(booking.status()).isEqualTo(BookingStatus.REQUESTED);
        assertThat(booking.isRequested()).isTrue();
    }

    @Test
    void a_new_booking_keeps_the_given_values() {
        Booking booking = newBooking();

        assertThat(booking.id()).isEqualTo("bkg_1");
        assertThat(booking.userId()).isEqualTo(USER);
        assertThat(booking.eventId()).isEqualTo("evt_1");
        assertThat(booking.bookedAt()).isEqualTo(NOW);
        assertThat(booking.lines()).containsExactlyElementsOf(LINES);
    }

    @Test
    void lines_cannot_be_modified_from_outside() {
        Booking booking = newBooking();

        assertThatThrownBy(() -> booking.lines().add(new BookingLine("tkt_3", "X", 1, Money.ofCents(1))))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // --- amounts (US-16) ---

    @Test
    void subtotal_is_the_sum_of_lines() {
        // 2 x 5000 + 1 x 1500 = 11500
        assertThat(newBooking().subtotal()).isEqualTo(Money.ofCents(11500));
    }

    @Test
    void fees_are_30_cents_per_ticket() {
        // 2 + 1 = 3 tickets
        assertThat(newBooking().fees()).isEqualTo(Money.ofCents(90));
    }

    @Test
    void total_is_lines_plus_fees() {
        assertThat(newBooking().total()).isEqualTo(Money.ofCents(11590));
    }

    // --- payment (US-16) ---

    @Test
    void a_reserved_booking_awaits_payment_with_the_client_secret() {
        Booking booking = newBooking();
        booking.reserve();

        booking.awaitPayment(new Payment("pi_1", "pi_1_secret_abc"));

        assertThat(booking.status()).isEqualTo(BookingStatus.AWAITING_PAYMENT);
        assertThat(booking.paymentIntentId()).isEqualTo("pi_1");
        assertThat(booking.clientSecret()).isEqualTo("pi_1_secret_abc");
    }

    @Test
    void a_new_booking_has_no_payment_yet() {
        assertThat(newBooking().paymentIntentId()).isNull();
        assertThat(newBooking().clientSecret()).isNull();
    }

    @Test
    void only_a_reserved_booking_can_await_payment() {
        Payment payment = new Payment("pi_1", "pi_1_secret_abc");
        for (BookingStatus status : List.of(BookingStatus.REQUESTED, BookingStatus.REJECTED,
                BookingStatus.AWAITING_PAYMENT, BookingStatus.EXPIRED, BookingStatus.CONFIRMED)) {
            Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, status);
            assertThatThrownBy(() -> booking.awaitPayment(payment)).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void awaiting_payment_needs_a_payment() {
        Booking booking = newBooking();
        booking.reserve();

        assertThatThrownBy(() -> booking.awaitPayment(null)).isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_payment_needs_an_id_and_a_client_secret() {
        assertThatThrownBy(() -> new Payment(" ", "secret")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Payment("pi_1", null)).isInstanceOf(IllegalArgumentException.class);
    }

    // --- confirmation (US-17) ---

    @Test
    void an_awaiting_payment_booking_can_be_confirmed() {
        Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, BookingStatus.AWAITING_PAYMENT,
                "pi_1", "pi_1_secret_abc");

        booking.confirm();

        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void only_an_awaiting_payment_booking_can_be_confirmed() {
        for (BookingStatus status : List.of(BookingStatus.REQUESTED, BookingStatus.RESERVED,
                BookingStatus.REJECTED, BookingStatus.EXPIRED, BookingStatus.CONFIRMED)) {
            Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, status);
            assertThatThrownBy(booking::confirm).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void a_confirmed_booking_never_expires() {
        Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, BookingStatus.CONFIRMED);

        assertThatThrownBy(booking::expire).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void an_awaiting_payment_booking_still_expires() {
        Booking booking = newBooking();
        booking.reserve();
        booking.awaitPayment(new Payment("pi_1", "pi_1_secret_abc"));

        booking.expire();

        assertThat(booking.status()).isEqualTo(BookingStatus.EXPIRED);
    }

    // --- status changes ---

    @Test
    void a_requested_booking_can_be_reserved() {
        Booking booking = newBooking();

        booking.reserve();

        assertThat(booking.status()).isEqualTo(BookingStatus.RESERVED);
        assertThat(booking.isRequested()).isFalse();
    }

    @Test
    void a_requested_booking_can_be_rejected() {
        Booking booking = newBooking();

        booking.reject();

        assertThat(booking.status()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void cannot_reserve_twice() {
        Booking booking = newBooking();
        booking.reserve();

        assertThatThrownBy(booking::reserve).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cannot_reject_a_reserved_booking() {
        Booking booking = newBooking();
        booking.reserve();

        assertThatThrownBy(booking::reject).isInstanceOf(IllegalStateException.class);
    }

    // --- expiration (US-14) ---

    @Test
    void a_reserved_booking_can_expire() {
        Booking booking = newBooking();
        booking.reserve();

        booking.expire();

        assertThat(booking.status()).isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    void an_awaiting_payment_booking_can_expire() {
        Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, BookingStatus.AWAITING_PAYMENT);

        booking.expire();

        assertThat(booking.status()).isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    void a_requested_booking_cannot_expire() {
        assertThatThrownBy(newBooking()::expire).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void a_rejected_or_confirmed_or_expired_booking_cannot_expire() {
        for (BookingStatus status : List.of(BookingStatus.REJECTED, BookingStatus.CONFIRMED, BookingStatus.EXPIRED)) {
            Booking booking = new Booking("bkg_1", USER, "evt_1", NOW, LINES, status);
            assertThatThrownBy(booking::expire).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void cannot_reserve_a_rejected_booking() {
        Booking booking = newBooking();
        booking.reject();

        assertThatThrownBy(booking::reserve).isInstanceOf(IllegalStateException.class);
    }

    // --- validation ---

    @Test
    void a_booking_needs_an_id() {
        assertThatThrownBy(() -> Booking.request(" ", USER, "evt_1", NOW, LINES))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_booking_needs_a_user() {
        assertThatThrownBy(() -> Booking.request("bkg_1", null, "evt_1", NOW, LINES))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_booking_needs_an_event() {
        assertThatThrownBy(() -> Booking.request("bkg_1", USER, "", NOW, LINES))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_booking_needs_a_date() {
        assertThatThrownBy(() -> Booking.request("bkg_1", USER, "evt_1", null, LINES))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_booking_needs_lines() {
        assertThatThrownBy(() -> Booking.request("bkg_1", USER, "evt_1", NOW, List.of()))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void a_booking_needs_a_status() {
        assertThatThrownBy(() -> new Booking("bkg_1", USER, "evt_1", NOW, LINES, null))
                .isInstanceOf(InvalidBookingException.class);
    }
}