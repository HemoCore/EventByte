package fr.soft_labs.eventbyte.infrastructure.sse;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.presentation.dto.BookingResponse;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// Like Spring 7's SseEmitter: parameters are non-null unless marked otherwise
@NullMarked
class SseBookingNotifierTest {

    /** Fausse connexion : enregistre ce qu'on lui envoie au lieu de l'envoyer à un navigateur. */
    static class RecordingEmitter extends SseEmitter {
        final List<Object> sent = new ArrayList<>();

        @Override
        public void send(SseEventBuilder builder) {
            builder.build().forEach(part -> sent.add(part.getData()));
        }
    }

    /** Fausse connexion coupée : chaque envoi échoue, comme un onglet fermé. */
    static class BrokenEmitter extends SseEmitter {
        int attempts;

        @Override
        public void send(SseEventBuilder builder) throws IOException {
            attempts++;
            throw new IOException("Connection closed");
        }
    }

    private final List<RecordingEmitter> created = new ArrayList<>();
    private final SseBookingNotifier notifier = new SseBookingNotifier(() -> {
        RecordingEmitter emitter = new RecordingEmitter();
        created.add(emitter);
        return emitter;
    });

    private Booking bookingOf(String id, String userId) {
        return new Booking(id, new UserId(userId), "evt_1", Instant.parse("2026-11-26T10:00:00Z"),
                List.of(new BookingLine("tkt_vip", "VIP", 1, Money.ofCents(5000))), BookingStatus.RESERVED);
    }

    @Test
    void sends_booking_updated_to_the_owner() {
        notifier.subscribe(new UserId("alice"));

        notifier.bookingUpdated(bookingOf("bkg_1", "alice"));

        assertThat(created.getFirst().sent)
                .anySatisfy(data -> assertThat(data.toString()).contains("event:booking_updated"))
                .anySatisfy(data -> assertThat(data).isInstanceOf(BookingResponse.class));
    }

    @Test
    void never_sends_the_booking_of_another_user() {
        notifier.subscribe(new UserId("alice"));

        notifier.bookingUpdated(bookingOf("bkg_bob", "bob"));

        assertThat(created.getFirst().sent).isEmpty();
    }

    @Test
    void sends_to_every_open_tab_of_the_owner() {
        notifier.subscribe(new UserId("alice"));
        notifier.subscribe(new UserId("alice"));

        notifier.bookingUpdated(bookingOf("bkg_1", "alice"));

        assertThat(created).allSatisfy(emitter -> assertThat(emitter.sent).isNotEmpty());
    }

    @Test
    void a_closed_connection_never_throws() {
        SseBookingNotifier withBrokenConnection = new SseBookingNotifier(BrokenEmitter::new);
        withBrokenConnection.subscribe(new UserId("alice"));

        assertThatCode(() -> withBrokenConnection.bookingUpdated(bookingOf("bkg_1", "alice")))
                .doesNotThrowAnyException();
    }

    @Test
    void a_closed_connection_is_forgotten_after_the_first_failure() {
        BrokenEmitter broken = new BrokenEmitter();
        SseBookingNotifier withBrokenConnection = new SseBookingNotifier(() -> broken);
        withBrokenConnection.subscribe(new UserId("alice"));

        withBrokenConnection.bookingUpdated(bookingOf("bkg_1", "alice"));
        withBrokenConnection.bookingUpdated(bookingOf("bkg_2", "alice"));

        assertThat(broken.attempts).isEqualTo(1);
    }
}
