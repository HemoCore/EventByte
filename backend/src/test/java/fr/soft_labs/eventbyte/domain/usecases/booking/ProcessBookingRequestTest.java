package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog.TicketOffer;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessBookingRequestTest {

    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");

    private final InMemoryTicketCatalog catalog = new InMemoryTicketCatalog()
            .add(new TicketOffer("tkt_vip", "evt_1", "VIP", Money.ofCents(5000), 3));
    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final InMemoryStockReservation stock = new InMemoryStockReservation();

    private final FakePaymentGateway payments = new FakePaymentGateway();

    private final ProcessBookingRequest process = new ProcessBookingRequest(repository, catalog, stock, payments);

    private void givenBooking(String id, int quantity) {
        repository.save(Booking.request(id, new UserId("user-1"), "evt_1", NOW,
                List.of(new BookingLine("tkt_vip", "VIP", quantity, Money.ofCents(5000)))));
    }

    private BookingStatus statusOf(String id) {
        return repository.findById(id).orElseThrow().status();
    }

    @Test
    void reserves_when_stock_is_enough_then_awaits_payment() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1);
    }

    @Test
    void rejects_when_stock_is_not_enough() {
        givenBooking("bkg_1", 4);

        process.execute("bkg_1");

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.REJECTED);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(3);
    }

    @Test
    void the_second_booking_is_rejected_when_the_first_took_the_last_tickets() {
        givenBooking("bkg_1", 3);
        givenBooking("bkg_2", 1);

        process.execute("bkg_1");
        process.execute("bkg_2");

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.AWAITING_PAYMENT);
        assertThat(statusOf("bkg_2")).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void a_duplicate_message_does_not_reserve_twice() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");
        process.execute("bkg_1");

        assertThat(stock.calls).isEqualTo(1);
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1);
    }

    @Test
    void an_unknown_booking_is_ignored() {
        process.execute("nope");

        assertThat(stock.calls).isZero();
        assertThat(payments.calls).isEmpty();
    }

    // --- payment (US-16) ---

    @Test
    void a_reserved_booking_gets_its_payment_and_client_secret() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");

        Booking booking = repository.findById("bkg_1").orElseThrow();
        assertThat(booking.status()).isEqualTo(BookingStatus.AWAITING_PAYMENT);
        assertThat(booking.paymentIntentId()).isEqualTo("pi_bkg_1");
        assertThat(booking.clientSecret()).isEqualTo("pi_bkg_1_secret_123");
    }

    @Test
    void the_payment_is_asked_for_the_total_with_fees() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");

        // 2 x 5000 + 2 tickets x 30 cents of fees
        assertThat(payments.calls).containsExactly(new FakePaymentGateway.Call("bkg_1", Money.ofCents(10060)));
    }

    @Test
    void a_rejected_booking_asks_no_payment() {
        givenBooking("bkg_1", 4);

        process.execute("bkg_1");

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.REJECTED);
        assertThat(payments.calls).isEmpty();
        assertThat(repository.findById("bkg_1").orElseThrow().clientSecret()).isNull();
    }

    @Test
    void the_booking_stays_reserved_when_the_payment_cannot_be_created() {
        payments.failing = true;
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");

        Booking booking = repository.findById("bkg_1").orElseThrow();
        assertThat(booking.status()).isEqualTo(BookingStatus.RESERVED);
        assertThat(booking.clientSecret()).isNull();
        assertThat(stock.stock.get("tkt_vip")).isEqualTo(1); // still held: the US-14 job gives it back
    }

    @Test
    void a_duplicate_message_does_not_ask_a_second_payment() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");
        process.execute("bkg_1");

        assertThat(payments.calls).hasSize(1);
    }
}
