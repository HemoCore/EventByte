package fr.soft_labs.eventbyte.domain.ports.like;

import fr.soft_labs.eventbyte.domain.models.user.UserId;

public interface LikeRepository {
    boolean isLiked(UserId userId, String eventId);

    /** Enregistre le like. Ne fait rien si l'utilisateur a déjà liké cet événement. */
    void like(UserId userId, String eventId);

    /** Retire le like. Ne fait rien si l'utilisateur n'avait pas liké cet événement. */
    void unlike(UserId userId, String eventId);

    long countByEventId(String eventId);
}
