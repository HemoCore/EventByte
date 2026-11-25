package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class GetEventTest {

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final Map<String, Long> likes = new HashMap<>();
    private final Map<String, Integer> stockOverrides = new HashMap<>();

    // Stock = quantité du ticket, sauf si le test en impose un autre
    private final TicketAvailability availability = tickets -> tickets.stream()
            .collect(Collectors.toMap(Ticket::id, t -> stockOverrides.getOrDefault(t.id(), t.quantity())));

    private final GetEvent getEvent = new GetEvent(repository,
            new EventSummaryAssembler(ids -> likes, availability));

    @Test
    void returns_nothing_when_the_event_does_not_exist() {
        assertThat(getEvent.execute("unknown")).isEmpty();
    }

    @Test
    void returns_the_event_with_its_like_count() {
        repository.save(anEvent().withId("evt_1").build());
        likes.put("evt_1", 3L);

        EventSummary summary = getEvent.execute("evt_1").orElseThrow();

        assertThat(summary.event().id()).isEqualTo("evt_1");
        assertThat(summary.likedCount()).isEqualTo(3);
    }

    @Test
    void an_event_without_likes_has_zero_likes() {
        repository.save(anEvent().withId("evt_1").build());

        assertThat(getEvent.execute("evt_1").orElseThrow().likedCount()).isZero();
    }

    @Test
    void the_lowest_price_is_the_cheapest_ticket_in_cents() {
        repository.save(anEvent().withId("evt_1").withTickets(
                new Ticket("tkt_vip", "VIP", Money.ofCents(13477), 10),
                new Ticket("tkt_std", "Standard", Money.ofCents(1541), 10)).build());

        assertThat(getEvent.execute("evt_1").orElseThrow().lowestPrice()).isEqualTo(Money.ofCents(1541));
    }

    @Test
    void is_not_sold_out_while_one_ticket_type_is_still_available() {
        repository.save(anEvent().withId("evt_1").withTickets(
                new Ticket("tkt_1", "Standard", Money.ofCents(1000), 0),
                new Ticket("tkt_2", "VIP", Money.ofCents(5000), 2)).build());

        assertThat(getEvent.execute("evt_1").orElseThrow().soldOut()).isFalse();
    }

    @Test
    void is_sold_out_when_no_ticket_is_available_anymore() {
        repository.save(anEvent().withId("evt_1").withTickets(
                new Ticket("tkt_1", "Standard", Money.ofCents(1000), 50),
                new Ticket("tkt_2", "VIP", Money.ofCents(5000), 5)).build());
        // Toutes les places sont prises par des réservations en cours
        stockOverrides.put("tkt_1", 0);
        stockOverrides.put("tkt_2", 0);

        assertThat(getEvent.execute("evt_1").orElseThrow().soldOut()).isTrue();
    }
}
