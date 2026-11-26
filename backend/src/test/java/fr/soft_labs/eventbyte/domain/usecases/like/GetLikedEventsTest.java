package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.InMemoryEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Collectors;


import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;

class GetLikedEventsTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryLikeRepository likeRepository = new InMemoryLikeRepository();
    private final InMemoryEventRepository eventRepository = new InMemoryEventRepository();

    private final LikeCounter likeCounter = eventIds -> eventIds.stream()
            .collect(Collectors.toMap(Function.identity(), likeRepository::countByEventId));
    private final TicketAvailability ticketAvailability = tickets -> tickets.stream()
            .collect(Collectors.toMap(Ticket::id, Ticket::quantity));

    private final GetLikedEvents getLikedEvents = new GetLikedEvents(
            likeRepository, eventRepository, new EventSummaryAssembler(likeCounter, ticketAvailability));

    @BeforeEach
    void setUp() {
        eventRepository.save(anEvent().withId("evt_1").withTitle("Concert").build());
        eventRepository.save(anEvent().withId("evt_2").withTitle("Festival")
                .withTickets(new Ticket("tkt_3", "Standard", Money.ofCents(1500), 10))
                .build());
    }

    @Test
    void returns_only_the_events_liked_by_the_user() {
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(BOB, "evt_2");

        assertThat(getLikedEvents.execute(ALICE))
                .extracting(summary -> summary.event().id())
                .containsExactly("evt_1");
    }

    @Test
    void returns_the_most_recent_likes_first() {
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(ALICE, "evt_2");

        assertThat(getLikedEvents.execute(ALICE))
                .extracting(summary -> summary.event().id())
                .containsExactly("evt_2", "evt_1");
    }

    @Test
    void returns_an_empty_list_when_the_user_has_no_likes() {
        assertThat(getLikedEvents.execute(ALICE)).isEmpty();
    }

    @Test
    void ignores_likes_on_events_that_no_longer_exist() {
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(ALICE, "evt_deleted");

        assertThat(getLikedEvents.execute(ALICE))
                .extracting(summary -> summary.event().id())
                .containsExactly("evt_1");
    }

    @Test
    void liked_events_include_the_total_number_of_likes() {
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(BOB, "evt_1");

        assertThat(getLikedEvents.execute(ALICE))
                .extracting(EventSummary::likedCount)
                .containsExactly(2L);
    }
}