package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetUserBookingsTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryBookingRepository repository = new InMemoryBookingRepository();
    private final GetUserBookings getUserBookings = new GetUserBookings(repository);

    private void givenBooking(String id, UserId owner, String bookedAt) {
        repository.save(Booking.request(id, owner, "evt_1", Instant.parse(bookedAt),
                List.of(new BookingLine("tkt_vip", "VIP", 1, Money.ofCents(5000)))));
    }

    @Test
    void returns_only_my_bookings_most_recent_first() {
        givenBooking("bkg_old", ALICE, "2026-11-20T10:00:00Z");
        givenBooking("bkg_bob", BOB, "2026-11-22T10:00:00Z");
        givenBooking("bkg_new", ALICE, "2026-11-25T10:00:00Z");

        assertThat(getUserBookings.execute(ALICE))
                .extracting(Booking::id)
                .containsExactly("bkg_new", "bkg_old");
    }

    @Test
    void returns_an_empty_list_when_i_have_no_booking() {
        givenBooking("bkg_bob", BOB, "2026-11-22T10:00:00Z");

        assertThat(getUserBookings.execute(ALICE)).isEmpty();
    }
}