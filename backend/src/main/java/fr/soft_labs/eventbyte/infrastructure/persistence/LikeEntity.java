package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "likes")
public class LikeEntity {

    @EmbeddedId
    private LikeId id;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    private Instant createdAt;

    protected LikeEntity() {
    }

    public LikeEntity(LikeId id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public LikeId getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
}