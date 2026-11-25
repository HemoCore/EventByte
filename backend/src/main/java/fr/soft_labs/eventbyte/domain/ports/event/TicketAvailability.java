package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;

import java.util.Collection;
import java.util.Map;

/**
 * Donne le stock encore disponible de chaque ticket (réservations en cours déduites),
 * indexé par identifiant de ticket.
 */
public interface TicketAvailability {

    Map<String, Integer> availableByTicketId(Collection<Ticket> tickets);
}
