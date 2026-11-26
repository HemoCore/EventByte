package fr.soft_labs.eventbyte.domain.ports.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;

public interface BookingNotifier {

    /** Ne lève jamais d'exception : une connexion coupée ne doit pas faire échouer une réservation. */
    void bookingUpdated(Booking booking);
}
