package fr.soft_labs.eventbyte.domain.ports.event;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;

import java.util.Collection;
import java.util.Map;

public interface TicketAvailability {

    Map<String, Integer> availableByTicketId(Collection<Ticket> tickets);
}
