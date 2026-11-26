package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.AvailableTicket;
import fr.soft_labs.eventbyte.domain.models.event.EventNotFoundException;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetEventTicketsTest {

    private final InMemoryEventRepository repository = new InMemoryEventRepository();

    // stock restant par ticket ; sans entrée, on garde la quantité initiale
    private final Map<String, Integer> remaining = new HashMap<>();
    private final TicketAvailability availability = tickets -> tickets.stream()
            .collect(Collectors.toMap(Ticket::id, t -> remaining.getOrDefault(t.id(), t.quantity())));

    private final GetEventTickets getEventTickets = new GetEventTickets(repository, availability);

    @Test
    void fails_when_the_event_does_not_exist() {
        assertThatThrownBy(() -> getEventTickets.execute("unknown"))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessage("Event not found: unknown");
    }

    @Test
    void returns_every_ticket_with_its_event_name_and_price_in_cents() {
        repository.save(anEvent().withId("evt_1").build());

        assertThat(getEventTickets.execute("evt_1")).containsExactly(
                new AvailableTicket("tkt_1", "evt_1", "Standard", Money.ofCents(2751), 21),
                new AvailableTicket("tkt_2", "evt_1", "Premium", Money.ofCents(4339), 78));
    }

    @Test
    void quantity_is_the_remaining_stock_not_the_initial_one() {
        repository.save(anEvent().withId("evt_1").build());
        remaining.put("tkt_1", 3);
        remaining.put("tkt_2", 0);

        assertThat(getEventTickets.execute("evt_1"))
                .extracting(AvailableTicket::quantity)
                .containsExactly(3, 0);
    }

    @Test
    void a_ticket_missing_from_the_availability_is_shown_as_unavailable() {
        repository.save(anEvent().withId("evt_1").build());
        GetEventTickets withPartialAvailability =
                new GetEventTickets(repository, _ -> Map.of("tkt_1", 10));

        assertThat(withPartialAvailability.execute("evt_1"))
                .extracting(AvailableTicket::quantity)
                .containsExactly(10, 0);
    }

    @Test
    void an_event_without_tickets_returns_an_empty_list() {
        repository.save(anEvent().withId("evt_1").withTickets().build());

        assertThat(getEventTickets.execute("evt_1")).isEmpty();
    }
}