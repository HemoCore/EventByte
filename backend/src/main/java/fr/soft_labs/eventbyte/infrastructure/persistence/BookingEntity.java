package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.booking.BookingStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "booked_at", nullable = false, columnDefinition = "timestamptz")
    private Instant bookedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_lines", joinColumns = @JoinColumn(name = "booking_id"))
    private List<BookingLineEmbeddable> lines = new ArrayList<>();

    protected BookingEntity() {
    }

    public BookingEntity(String id, String userId, String eventId, Instant bookedAt,
                         BookingStatus status, List<BookingLineEmbeddable> lines) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.bookedAt = bookedAt;
        this.status = status;
        this.lines = new ArrayList<>(lines);
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public Instant getBookedAt() { return bookedAt; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public List<BookingLineEmbeddable> getLines() { return lines; }
}
