package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JpaTicketCatalog implements TicketCatalog {

    private final SpringDataTicketRepository springData;

    JpaTicketCatalog(SpringDataTicketRepository springData) {
        this.springData = springData;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, TicketOffer> findByIds(Collection<String> ticketIds) {
        return springData.findAllWithEventByIdIn(ticketIds).stream()
                .map(t -> new TicketOffer(
                        t.getId(),
                        t.getEvent().getId(),
                        t.getName(),
                        Money.ofCents(t.getPriceCents()),
                        t.getQuantity()))
                .collect(Collectors.toMap(TicketOffer::ticketId, Function.identity()));
    }
}
