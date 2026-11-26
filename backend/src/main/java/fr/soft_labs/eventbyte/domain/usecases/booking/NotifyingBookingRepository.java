package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingNotifier;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Enveloppe le vrai repository et prévient l'utilisateur à chaque enregistrement.
 * Chaque changement de statut passe par save(), donc chacun déclenche une notification,
 * sans modifier les cas d'usage des réservations.
 */
public class NotifyingBookingRepository implements BookingRepository {

    private final BookingRepository delegate;
    private final BookingNotifier notifier;

    public NotifyingBookingRepository(BookingRepository delegate, BookingNotifier notifier) {
        this.delegate = delegate;
        this.notifier = notifier;
    }

    @Override
    public void save(Booking booking) {
        delegate.save(booking);
        notifier.bookingUpdated(booking);
    }

    @Override
    public Optional<Booking> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public List<Booking> findByUserId(UserId userId) {
        return delegate.findByUserId(userId);
    }

    @Override
    public List<Booking> findExpirable(Instant bookedBefore) {
        return delegate.findExpirable(bookedBefore);
    }
}
