package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.booking.InvalidBookingException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.TicketNotFoundException;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog.TicketOffer;
import fr.soft_labs.eventbyte.domain.usecases.booking.RequestBooking.Item;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestBookingTest {

    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");
    private static final UserId USER = new UserId("user-1");

    private final InMemoryTicketCatalog catalog = new InMemoryTicketCatalog()
            .add(new TicketOffer("tkt_vip", "evt_1", "VIP", Money.ofCents(5000), 10))
            .add(new TicketOffer("tkt_early", "evt_1", "Early", Money.ofCents(1500), 50))
            .add(new TicketOffer("tkt_other", "evt_2", "Other", Money.ofCents(900), 5));
    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final RecordingPublisher publisher = new RecordingPublisher();

    private final RequestBooking requestBooking = new RequestBooking(
            catalog, repository, publisher, () -> "bkg_1", Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void creates_a_requested_booking_and_returns_its_id() {
        String id = requestBooking.execute(USER, List.of(new Item("tkt_vip", 2), new Item("tkt_early", 1)));

        assertThat(id).isEqualTo("bkg_1");
        Booking saved = repository.findById("bkg_1").orElseThrow();
        assertThat(saved.status()).isEqualTo(BookingStatus.REQUESTED);
        assertThat(saved.userId()).isEqualTo(USER);
        assertThat(saved.eventId()).isEqualTo("evt_1");
        assertThat(saved.bookedAt()).isEqualTo(NOW);
        assertThat(saved.total()).isEqualTo(Money.ofCents(11500));
    }

    @Test
    void takes_name_and_price_from_the_catalog() {
        requestBooking.execute(USER, List.of(new Item("tkt_vip", 1)));

        BookingLine line = repository.findById("bkg_1").orElseThrow().lines().getFirst();
        assertThat(line.name()).isEqualTo("VIP");
        assertThat(line.unitPrice()).isEqualTo(Money.ofCents(5000));
    }

    @Test
    void publishes_the_booking_for_processing() {
        requestBooking.execute(USER, List.of(new Item("tkt_vip", 1)));

        assertThat(publisher.published).containsExactly("bkg_1");
    }

    @Test
    void rejects_an_empty_request() {
        assertThatThrownBy(() -> requestBooking.execute(USER, List.of()))
                .isInstanceOf(InvalidBookingException.class);
        assertThat(publisher.published).isEmpty();
    }

    @Test
    void rejects_the_same_ticket_twice() {
        assertThatThrownBy(() -> requestBooking.execute(USER,
                List.of(new Item("tkt_vip", 1), new Item("tkt_vip", 2))))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void rejects_an_unknown_ticket() {
        assertThatThrownBy(() -> requestBooking.execute(USER, List.of(new Item("nope", 1))))
                .isInstanceOf(TicketNotFoundException.class);
        assertThat(repository.findById("bkg_1")).isEmpty();
    }

    @Test
    void rejects_tickets_from_different_events() {
        assertThatThrownBy(() -> requestBooking.execute(USER,
                List.of(new Item("tkt_vip", 1), new Item("tkt_other", 1))))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    void rejects_a_zero_quantity() {
        assertThatThrownBy(() -> requestBooking.execute(USER, List.of(new Item("tkt_vip", 0))))
                .isInstanceOf(InvalidBookingException.class);
    }
}
