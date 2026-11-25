package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InitialStockTicketAvailability implements TicketAvailability {

    @Override
    public Map<String, Integer> availableByTicketId(Collection<Ticket> tickets) {
        return tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity, (a, b) -> a));
    }
}
