package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.like.GetLikedEvents;
import fr.soft_labs.eventbyte.domain.usecases.like.LikeEvent;
import fr.soft_labs.eventbyte.domain.usecases.like.UnlikeEvent;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LikeController {

    private final LikeEvent likeEvent;
    private final UnlikeEvent unlikeEvent;
    private final GetLikedEvents getLikedEvents;
    private final CurrentUser currentUser;

    public LikeController(LikeEvent likeEvent, UnlikeEvent unlikeEvent, GetLikedEvents getLikedEvents,
                          CurrentUser currentUser) {
        this.likeEvent = likeEvent;
        this.unlikeEvent = unlikeEvent;
        this.getLikedEvents = getLikedEvents;
        this.currentUser = currentUser;
    }

    @GetMapping("/events/liked")
    public List<EventResponse> liked() {
        return getLikedEvents.execute(currentUser.get()).stream()
                .map(EventResponse::from)
                .toList();
    }

    @PostMapping("/events/{eventId}/like")
    public ResponseEntity<Void> like(@PathVariable String eventId) {
        likeEvent.execute(currentUser.get(), eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/events/{eventId}/like")
    public ResponseEntity<Void> unlike(@PathVariable String eventId) {
        unlikeEvent.execute(currentUser.get(), eventId);
        return ResponseEntity.ok().build();
    }
}
