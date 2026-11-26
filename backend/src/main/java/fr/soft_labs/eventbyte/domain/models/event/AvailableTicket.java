package fr.soft_labs.eventbyte.domain.models.event;

/** Un type de ticket avec le stock encore disponible (réservations en cours déduites). */
public record AvailableTicket(String id, String eventId, String name, Money price, int quantity) {

    public AvailableTicket {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité disponible ne peut pas être négative : " + quantity);
        }
    }
}