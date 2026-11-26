package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.InMemoryEventRepository;
import fr.soft_labs.eventbyte.domain.usecases.like.GetLikedEvents;
import fr.soft_labs.eventbyte.domain.usecases.like.InMemoryLikeRepository;
import fr.soft_labs.eventbyte.domain.usecases.like.LikeEvent;
import fr.soft_labs.eventbyte.domain.usecases.like.UnlikeEvent;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.errors.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.soft_labs.eventbyte.domain.models.event.EventBuilder.anEvent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LikeControllerTest {

    private static final UserId ALICE = new UserId("alice");
    private static final UserId BOB = new UserId("bob");

    private final InMemoryLikeRepository likeRepository = new InMemoryLikeRepository();
    private final InMemoryEventRepository eventRepository = new InMemoryEventRepository();

    private final EventSummaryAssembler assembler = new EventSummaryAssembler(
            eventIds -> eventIds.stream()
                    .collect(Collectors.toMap(Function.identity(), likeRepository::countByEventId)),
            tickets -> tickets.stream().collect(Collectors.toMap(Ticket::id, Ticket::quantity)));

    private final CurrentUser currentUser = new CurrentUser() {
        @Override
        public UserId get() {
            return ALICE;
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new LikeController(
                    new LikeEvent(likeRepository, eventRepository),
                    new UnlikeEvent(likeRepository),
                    new GetLikedEvents(likeRepository, eventRepository, assembler),
                    currentUser))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void returns_the_events_liked_by_the_current_user_in_the_api_format() throws Exception {
        eventRepository.save(anEvent().withId("evt_1").withTitle("Concert").build());
        eventRepository.save(anEvent().withId("evt_2").withTitle("Festival").build());
        likeRepository.like(ALICE, "evt_1");
        likeRepository.like(BOB, "evt_1");
        likeRepository.like(BOB, "evt_2");

        mockMvc.perform(get("/events/liked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("evt_1"))
                .andExpect(jsonPath("$[0].title").value("Concert"))
                .andExpect(jsonPath("$[0].likedCount").value(2))
                .andExpect(jsonPath("$[0].isSoldOut").exists())
                .andExpect(jsonPath("$[0].lowestPrice").exists())
                .andExpect(jsonPath("$[0].venue.adresse").exists())
                .andExpect(jsonPath("$[0].venue.coordinates.latitude").exists());
    }

    @Test
    void returns_an_empty_list_when_the_user_has_no_likes() throws Exception {
        mockMvc.perform(get("/events/liked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
