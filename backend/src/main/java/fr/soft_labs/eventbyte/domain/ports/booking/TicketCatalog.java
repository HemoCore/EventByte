package fr.soft_labs.eventbyte.domain.ports.booking;

import fr.soft_labs.eventbyte.domain.models.event.Money;

import java.util.Collection;
import java.util.Map;

public interface TicketCatalog {

    /** Returns the tickets found, by ticket id. Unknown ids are simply missing from the map. */
    Map<String, TicketOffer> findByIds(Collection<String> ticketIds);

    record TicketOffer(String ticketId, String eventId, String name, Money price, int initialQuantity) {
    }
}