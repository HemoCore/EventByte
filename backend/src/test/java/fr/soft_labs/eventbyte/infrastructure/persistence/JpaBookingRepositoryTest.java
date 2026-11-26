package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Real Postgres (docker compose), every test is rolled back. */
@SpringBootTest
@Transactional
class JpaBookingRepositoryTest {

    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");

    @Autowired
    private JpaBookingRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void insertEvent() {
        entityManager.createNativeQuery("""
                INSERT INTO events (id, title, venue_name, venue_address, venue_latitude, venue_longitude)
                VALUES ('evt_test_bkg', 'Test', 'Salle', 'Adresse', 48.85, 2.35)
                """).executeUpdate();
    }

    private void save(String id, BookingStatus status, Instant bookedAt) {
        repository.save(new Booking(id, new UserId("user-1"), "evt_test_bkg", bookedAt,
                List.of(new BookingLine("tkt_test", "VIP", 2, Money.ofCents(5000))), status));
    }

    private List<String> expirableIds(Instant cutoff) {
        return repository.findExpirable(cutoff).stream().map(Booking::id).toList();
    }

    @Test
    void saves_and_reads_back_a_booking() {
        save("bkg_test_1", BookingStatus.RESERVED, NOW);

        Booking found = repository.findById("bkg_test_1").orElseThrow();

        assertThat(found.status()).isEqualTo(BookingStatus.RESERVED);
        assertThat(found.bookedAt()).isEqualTo(NOW);
        assertThat(found.lines()).singleElement()
                .satisfies(l -> assertThat(l.quantity()).isEqualTo(2));
    }

    @Test
    void finds_only_old_reserved_and_awaiting_payment_bookings() {
        Instant old = NOW.minus(Duration.ofHours(1));
        save("bkg_test_reserved_old", BookingStatus.RESERVED, old);
        save("bkg_test_awaiting_old", BookingStatus.AWAITING_PAYMENT, old);
        save("bkg_test_reserved_new", BookingStatus.RESERVED, NOW);
        save("bkg_test_requested_old", BookingStatus.REQUESTED, old);
        save("bkg_test_rejected_old", BookingStatus.REJECTED, old);
        save("bkg_test_expired_old", BookingStatus.EXPIRED, old);
        save("bkg_test_confirmed_old", BookingStatus.CONFIRMED, old);

        assertThat(expirableIds(NOW.minus(Duration.ofMinutes(15))))
                .filteredOn(id -> id.startsWith("bkg_test_"))
                .containsExactlyInAnyOrder("bkg_test_reserved_old", "bkg_test_awaiting_old");
    }

    @Test
    void finds_the_bookings_of_a_user_most_recent_first() {
        save("bkg_test_old", BookingStatus.EXPIRED, NOW.minus(Duration.ofDays(2)));
        save("bkg_test_new", BookingStatus.RESERVED, NOW);
        repository.save(new Booking("bkg_test_other", new UserId("user-2"), "evt_test_bkg", NOW,
                List.of(new BookingLine("tkt_test", "VIP", 1, Money.ofCents(5000))), BookingStatus.RESERVED));

        assertThat(repository.findByUserId(new UserId("user-1")))
                .extracting(Booking::id)
                .filteredOn(id -> id.startsWith("bkg_test_"))
                .containsExactly("bkg_test_new", "bkg_test_old");
    }

    @Test
    void an_expired_booking_is_saved_with_its_new_status() {
        save("bkg_test_1", BookingStatus.RESERVED, NOW.minus(Duration.ofHours(1)));
        Booking booking = repository.findById("bkg_test_1").orElseThrow();

        booking.expire();
        repository.save(booking);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findById("bkg_test_1").orElseThrow().status()).isEqualTo(BookingStatus.EXPIRED);
        assertThat(expirableIds(NOW)).doesNotContain("bkg_test_1");
    }
}
