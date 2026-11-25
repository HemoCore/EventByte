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

    private final ProcessBookingRequest process = new ProcessBookingRequest(repository, catalog, stock);

    private void givenBooking(String id, int quantity) {
        repository.save(Booking.request(id, new UserId("user-1"), "evt_1", NOW,
                List.of(new BookingLine("tkt_vip", "VIP", quantity, Money.ofCents(5000)))));
    }

    private BookingStatus statusOf(String id) {
        return repository.findById(id).orElseThrow().status();
    }

    @Test
    void reserves_when_stock_is_enough() {
        givenBooking("bkg_1", 2);

        process.execute("bkg_1");

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.RESERVED);
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

        assertThat(statusOf("bkg_1")).isEqualTo(BookingStatus.RESERVED);
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
    }
}
