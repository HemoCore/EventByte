package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.like.LikeEvent;
import fr.soft_labs.eventbyte.domain.usecases.like.UnlikeEvent;
import fr.soft_labs.eventbyte.infrastructure.security.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    private final LikeEvent likeEvent;
    private final UnlikeEvent unlikeEvent;
    private final CurrentUser currentUser;

    public LikeController(LikeEvent likeEvent, UnlikeEvent unlikeEvent, CurrentUser currentUser) {
        this.likeEvent = likeEvent;
        this.unlikeEvent = unlikeEvent;
        this.currentUser = currentUser;
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