package fr.soft_labs.eventbyte.domain.models.booking;

import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;

import java.time.Instant;
import java.util.List;

public class Booking {

    private final String id;
    private final UserId userId;
    private final String eventId;
    private final Instant bookedAt;
    private final List<BookingLine> lines;
    private BookingStatus status;

    public Booking(String id, UserId userId, String eventId, Instant bookedAt,
                   List<BookingLine> lines, BookingStatus status) {
        if (id == null || id.isBlank()) throw new InvalidBookingException("Booking id is required");
        if (userId == null) throw new InvalidBookingException("User is required");
        if (eventId == null || eventId.isBlank()) throw new InvalidBookingException("Event id is required");
        if (bookedAt == null) throw new InvalidBookingException("Booking date is required");
        if (lines == null || lines.isEmpty()) throw new InvalidBookingException("A booking needs at least one line");
        if (status == null) throw new InvalidBookingException("Status is required");
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.bookedAt = bookedAt;
        this.lines = List.copyOf(lines);
        this.status = status;
    }

    public static Booking request(
        String id, 
        UserId userId, 
        String eventId, 
        Instant now,
        List<BookingLine> lines
    ) {
        return new Booking(id, userId, eventId, now, lines, BookingStatus.REQUESTED);
    }

    public void reserve() {
        requireStatus(BookingStatus.REQUESTED);
        status = BookingStatus.RESERVED;
    }

    public void reject() {
        requireStatus(BookingStatus.REQUESTED);
        status = BookingStatus.REJECTED;
    }

    public boolean isRequested() {
        return status == BookingStatus.REQUESTED;
    }

    public Money total() {
        return Money.ofCents(lines.stream().mapToLong(l -> l.subtotal().cents()).sum());
    }

    private void requireStatus(BookingStatus expected) {
        if(status != expected) {
            throw new IllegalStateException("Booking " + id + " is " + status + ", expected " + expected);
        }
    }

    public String id() { return id; }
    public UserId userId() { return userId; }
    public String eventId() { return eventId; }
    public Instant bookedAt() { return bookedAt; }
    public List<BookingLine> lines() { return lines; }
    public BookingStatus status() { return status; }
}
