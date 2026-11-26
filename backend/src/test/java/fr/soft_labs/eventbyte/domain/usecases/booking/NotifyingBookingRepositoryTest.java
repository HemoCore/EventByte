package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingNotifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotifyingBookingRepositoryTest {

    private static final UserId ALICE = new UserId("alice");
    private static final Instant NOW = Instant.parse("2026-11-26T10:00:00Z");

    private final InMemoryBookingRepository delegate = new InMemoryBookingRepository();
    private final List<BookingStatus> notifiedStatuses = new ArrayList<>();
    private final List<Boolean> savedBeforeNotification = new ArrayList<>();

    private final BookingNotifier notifier = booking -> {
        notifiedStatuses.add(booking.status());
        savedBeforeNotification.add(delegate.findById(booking.id()).isPresent());
    };

    private final NotifyingBookingRepository repository = new NotifyingBookingRepository(delegate, notifier);

    private Booking aBooking() {
        return Booking.request("bkg_1", ALICE, "evt_1", NOW,
                List.of(new BookingLine("tkt_1", "Standard", 2, Money.ofCents(2751))));
    }

    @Test
    void saving_a_booking_stores_it_and_notifies() {
        repository.save(aBooking());

        assertThat(delegate.findById("bkg_1")).isPresent();
        assertThat(notifiedStatuses).containsExactly(BookingStatus.REQUESTED);
    }

    @Test
    void each_status_change_sends_a_notification() {
        Booking booking = aBooking();
        repository.save(booking);

        booking.reserve();
        repository.save(booking);

        booking.expire();
        repository.save(booking);

        assertThat(notifiedStatuses).containsExactly(
                BookingStatus.REQUESTED, BookingStatus.RESERVED, BookingStatus.EXPIRED);
    }

    @Test
    void the_booking_is_saved_before_the_notification() {
        repository.save(aBooking());

        assertThat(savedBeforeNotification).containsExactly(true);
    }

    @Test
    void reading_bookings_does_not_notify() {
        delegate.save(aBooking());

        repository.findById("bkg_1");
        repository.findByUserId(ALICE);
        repository.findExpirable(NOW);

        assertThat(notifiedStatuses).isEmpty();
    }
}