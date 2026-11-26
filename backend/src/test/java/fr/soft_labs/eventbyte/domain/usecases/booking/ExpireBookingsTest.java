package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpireBookingsTest {

    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");
    private static final Duration DELAY = Duration.ofMinutes(15);

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final InMemoryStockReservation stock = new InMemoryStockReservation();

    private final FakePaymentGateway payments = new FakePaymentGateway();

    private final ExpireBookings expireBookings =
            new ExpireBookings(repository, stock, payments, Clock.fixed(NOW, ZoneOffset.UTC), DELAY);

    private void givenBooking(String id, BookingStatus status, Instant bookedAt, int vip, int early) {
        List<BookingLine> lines = new java.util.ArrayList<>();
        if (vip > 0) lines.add(new BookingLine("tkt_vip", "VIP", vip, Money.ofCents(5000)));
        if (early > 0) lines.add(new BookingLine("tkt_early", "Early", early, Money.ofCents(1500)));
        repository.save(new Booking(id, new UserId("user-1"), "evt_1", bookedAt, lines, status));
    }

    private BookingStatus statusOf(String id) {
        return repository.findById(id).orElseThrow().status();
    }

    @Test
    void a_reserved_booking_expires_after_the_delay_and_its_stock_comes_back() {
        stock.stock.put("tkt_vip", 1);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofMinutes(16)), 2, 0);

        int expired = expireBookings.execute();

        assertThat(expired).isEqualTo(1);
        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(3);
    }

    @Test
    void a_booking_younger_than_the_delay_does_not_expire() {
        stock.stock.put("tkt_vip", 1);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofMinutes(14)), 2, 0);

        assertThat(expireBookings.execute()).isZero();
        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.RESERVED);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1);
    }

    @Test
    void an_awaiting_payment_booking_also_expires() {
        stock.stock.put("tkt_vip", 0);
        givenBooking("bkg_1", BookingStatus.AWAITING_PAYMENT, NOW.minus(Duration.ofHours(1)), 1, 0);

        expireBookings.execute();

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1);
    }

    @Test
    void every_line_gets_its_stock_back() {
        stock.stock.put("tkt_vip", 0);
        stock.stock.put("tkt_early", 5);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofHours(1)), 2, 3);

        expireBookings.execute();

        assertThat(stock.stock.get("tkt_vip")).isEqualTo(2);
        assertThat(stock.stock.get("tkt_early")).isEqualTo(8);
    }

    @Test
    void requested_rejected_confirmed_and_expired_bookings_are_left_alone() {
        stock.stock.put("tkt_vip", 0);
        Instant old = NOW.minus(Duration.ofHours(1));
        givenBooking("requested", BookingStatus.REQUESTED, old, 1, 0);
        givenBooking("rejected", BookingStatus.REJECTED, old, 1, 0);
        givenBooking("confirmed", BookingStatus.CONFIRMED, old, 1, 0);
        givenBooking("expired", BookingStatus.EXPIRED, old, 1, 0);

        assertThat(expireBookings.execute()).isZero();
        assertThat(statusOf("requested")).isEqualTo(BookingStatus.REQUESTED);
        assertThat(statusOf("rejected")).isEqualTo(BookingStatus.REJECTED);
        assertThat(statusOf("confirmed")).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(statusOf("expired")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(stock.stock.get("tkt_vip")).isZero();
    }

    @Test
    void running_twice_gives_the_stock_back_only_once() {
        stock.stock.put("tkt_vip", 0);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofHours(1)), 2, 0);

        expireBookings.execute();
        int secondRun = expireBookings.execute();

        assertThat(secondRun).isZero();
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(2);
    }

    @Test
    void expired_seats_can_be_booked_again() {
        stock.stock.put("tkt_vip", 0);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofHours(1)), 1, 0);
        expireBookings.execute();

        boolean reservedAgain = stock.reserve(java.util.Map.of("tkt_vip", 1), java.util.Map.of("tkt_vip", 99));

        assertThat(reservedAgain).isTrue();
        assertThat(stock.stock.get("tkt_vip")).isZero();
    }

    @Test
    void the_delay_must_be_positive() {
        assertThatThrownBy(() -> new ExpireBookings(repository, stock, payments, Clock.systemUTC(), Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- payment cancelled on expiry (US-17) ---

    private void givenAwaitingPayment(String id, String paymentId) {
        repository.save(new Booking(id, new UserId("user-1"), "evt_1", NOW.minus(Duration.ofHours(1)),
                List.of(new BookingLine("tkt_vip", "VIP", 1, Money.ofCents(5000))),
                BookingStatus.AWAITING_PAYMENT, paymentId, paymentId + "_secret_x"));
    }

    @Test
    void the_payment_of_an_expired_booking_is_cancelled() {
        stock.stock.put("tkt_vip", 0);
        givenAwaitingPayment("bkg_1", "pi_1");

        expireBookings.execute();

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(payments.cancelled).containsExactly("pi_1");
    }

    @Test
    void a_reserved_booking_without_payment_cancels_nothing() {
        stock.stock.put("tkt_vip", 0);
        givenBooking("bkg_1", BookingStatus.RESERVED, NOW.minus(Duration.ofHours(1)), 1, 0);

        expireBookings.execute();

        assertThat(payments.cancelled).isEmpty();
    }

    @Test
    void if_the_cancel_fails_the_booking_still_expires_and_its_seats_come_back() {
        stock.stock.put("tkt_vip", 0);
        givenAwaitingPayment("bkg_1", "pi_1");
        payments.cancelFailing = true;

        int expired = expireBookings.execute();

        assertThat(expired).isEqualTo(1);
        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.EXPIRED);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1);
    }
}
