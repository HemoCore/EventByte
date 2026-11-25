package fr.soft_labs.eventbyte.domain.usecases.like;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UnlikeEventTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryLikeRepository likeRepository = new InMemoryLikeRepository();
    private final UnlikeEvent unlikeEvent = new UnlikeEvent(likeRepository);

    @Test
    void removes_an_existing_like() {
        likeRepository.like(ALICE, "evt_1");

        unlikeEvent.execute(ALICE, "evt_1");

        assertThat(likeRepository.isLiked(ALICE, "evt_1")).isFalse();
        assertThat(likeRepository.countByEventId("evt_1")).isZero();
    }

    @Test
    void only_removes_the_like_of_the_current_user() {
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(BOB, "evt_1");

        unlikeEvent.execute(ALICE, "evt_1");

        assertThat(likeRepository.isLiked(BOB, "evt_1")).isTrue();
        assertThat(likeRepository.countByEventId("evt_1")).isEqualTo(1);
    }

    @Test
    void unliking_an_event_that_was_not_liked_does_not_throw() {
        assertThatCode(() -> unlikeEvent.execute(ALICE, "evt_1"))
                .doesNotThrowAnyException();
    }
}