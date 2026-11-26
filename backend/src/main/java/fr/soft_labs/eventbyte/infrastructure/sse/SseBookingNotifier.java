package fr.soft_labs.eventbyte.infrastructure.sse;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingNotifier;
import fr.soft_labs.eventbyte.presentation.dto.BookingResponse;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class SseBookingNotifier implements BookingNotifier {

    private final Map<String, Set<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();
    private final Supplier<SseEmitter> emitterFactory;

    public SseBookingNotifier() {
        // 0 = la connexion n'expire jamais : le front se ferme à la première erreur et ne se reconnecte pas
        this(() -> new SseEmitter(0L));
    }

    SseBookingNotifier(Supplier<SseEmitter> emitterFactory) {
        this.emitterFactory = emitterFactory;
    }

    public SseEmitter subscribe(UserId userId) {
        SseEmitter emitter = emitterFactory.get();
        Set<SseEmitter> emitters = emittersByUser.computeIfAbsent(userId.value(), _ -> ConcurrentHashMap.newKeySet());
        emitters.add(emitter);

        Runnable remove = () -> emitters.remove(emitter);
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(_ -> remove.run());
        return emitter;
    }

    @Override
    public void bookingUpdated(Booking booking) {
        BookingResponse payload = BookingResponse.from(booking);
        String userId = booking.userId().value();
        // uniquement les connexions du propriétaire de la réservation
        for (SseEmitter emitter : emittersByUser.getOrDefault(userId, Set.of())) {
            send(userId, emitter, SseEmitter.event()
                    .name("booking_updated")
                    .data(payload, MediaType.APPLICATION_JSON));
        }
    }

    // Toutes les 25 secondes : détecte les onglets fermés (l'envoi échoue) pour ne pas garder leurs connexions
    @Scheduled(fixedRate = 25_000)
    public void heartbeat() {
        emittersByUser.forEach((userId, emitters) ->
                emitters.forEach(emitter -> send(userId, emitter, SseEmitter.event().comment("keep-alive"))));
    }

    private void send(String userId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (Exception e) {
            // connexion coupée : on l'oublie, sans jamais faire échouer la réservation
            emittersByUser.getOrDefault(userId, Set.of()).remove(emitter);
        }
    }
}