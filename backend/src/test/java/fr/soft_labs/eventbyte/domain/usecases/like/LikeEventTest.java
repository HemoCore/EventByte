package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.event.EventNotFoundException;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.usecases.event.InMemoryEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LikeEventTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryLikeRepository likeRepository = new InMemoryLikeRepository();
    private final InMemoryEventRepository eventRepository = new InMemoryEventRepository();
    private final LikeEvent likeEvent = new LikeEvent(likeRepository, eventRepository);

    @BeforeEach
    void setUp() {
        eventRepository.save(anEvent().withId("evt_1").build());
    }

    @Test
    void likes_an_existing_event() {
        likeEvent.execute(ALICE, "evt_1");

        assertThat(likeRepository.isLiked(ALICE, "evt_1")).isTrue();
        assertThat(likeRepository.countByEventId("evt_1")).isEqualTo(1);
    }

    @Test
    void liking_twice_counts_only_one_like() {
        likeEvent.execute(ALICE, "evt_1");
        likeEvent.execute(ALICE, "evt_1");

        assertThat(likeRepository.countByEventId("evt_1")).isEqualTo(1);
    }

    @Test
    void likes_from_different_users_are_all_counted() {
        likeEvent.execute(ALICE, "evt_1");
        likeEvent.execute(BOB, "evt_1");

        assertThat(likeRepository.countByEventId("evt_1")).isEqualTo(2);
    }

    @Test
    void liking_an_unknown_event_is_rejected() {
        assertThatThrownBy(() -> likeEvent.execute(ALICE, "evt_unknown"))
                .isInstanceOf(EventNotFoundException.class);

        assertThat(likeRepository.isLiked(ALICE, "evt_unknown")).isFalse();
    }
}
