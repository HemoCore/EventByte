package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LikeId implements Serializable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "event_id")
    private String eventId;

    protected LikeId() {
    }

    public LikeId(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId other)) return false;
        return Objects.equals(userId, other.userId) && Objects.equals(eventId, other.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}