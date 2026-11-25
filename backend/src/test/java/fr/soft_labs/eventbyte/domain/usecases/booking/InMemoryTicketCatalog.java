package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class InMemoryTicketCatalog implements TicketCatalog {

    private final Map<String, TicketOffer> offers = new HashMap<>();

    InMemoryTicketCatalog add(TicketOffer offer) {
        offers.put(offer.ticketId(), offer);
        return this;
    }

    @Override
    public Map<String, TicketOffer> findByIds(Collection<String> ticketIds) {
        Map<String, TicketOffer> found = new HashMap<>();
        for (String id : ticketIds) {
            if (offers.containsKey(id)) {
                found.put(id, offers.get(id));
            }
        }
        return found;
    }
}
