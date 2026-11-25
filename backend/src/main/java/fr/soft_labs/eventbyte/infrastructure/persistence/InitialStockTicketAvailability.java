package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter provisoire : le stock disponible est la quantité envoyée par Strapi.
 * À remplacer par l'adapter Redis des réservations (US-11 / US-13),
 * qui déduira les réservations en cours.
 */
@Component
public class InitialStockTicketAvailability implements TicketAvailability {

    @Override
    public Map<String, Integer> availableByTicketId(Collection<Ticket> tickets) {
        return tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity, (a, b) -> a));
    }
}
