package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingNotFoundException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetBookingTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final GetBooking getBooking = new GetBooking(repository);

    private void givenBooking(String id, UserId owner) {
        repository.save(Booking.request(id, owner, "evt_1", Instant.parse("2026-11-25T10:00:00Z"),
                List.of(new BookingLine("tkt_vip", "VIP", 2, Money.ofCents(5000)))));
    }

    @Test
    void returns_my_booking() {
        givenBooking("bkg_1", ALICE);

        assertThat(getBooking.execute(ALICE, "bkg_1").id()).isEqualTo("bkg_1");
    }

    @Test
    void fails_when_the_booking_does_not_exist() {
        assertThatThrownBy(() -> getBooking.execute(ALICE, "unknown"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("Booking not found: unknown");
    }

    @Test
    void never_returns_the_booking_of_another_user() {
        givenBooking("bkg_bob", BOB);

        assertThatThrownBy(() -> getBooking.execute(ALICE, "bkg_bob"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("Booking not found: bkg_bob");
    }
}