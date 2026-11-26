package fr.soft_labs.eventbyte.domain.models.booking;

import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.user.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class Booking {

    /** Statuses that hold stock and are not paid yet: they expire after the delay (US-14). */
    public static final Set<BookingStatus> EXPIRABLE_STATUSES =
            Set.of(BookingStatus.RESERVED, BookingStatus.AWAITING_PAYMENT);

    /** US-16: service fees per ticket, same rule as the frontend cart (0.30 € per ticket). */
    public static final Money FEE_PER_TICKET = Money.ofCents(30);

    private final String id;
    private final UserId userId;
    private final String eventId;
    private final Instant bookedAt;
    private final List<BookingLine> lines;
    private BookingStatus status;
    private String paymentIntentId;
    private String clientSecret;

    public Booking(String id, UserId userId, String eventId, Instant bookedAt,
                   List<BookingLine> lines, BookingStatus status) {
        this(id, userId, eventId, bookedAt, lines, status, null, null);
    }

    /** paymentIntentId and clientSecret stay null until the booking is awaiting payment (US-16). */
    public Booking(String id, UserId userId, String eventId, Instant bookedAt,
                   List<BookingLine> lines, BookingStatus status,
                   String paymentIntentId, String clientSecret) {
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
        this.paymentIntentId = paymentIntentId;
        this.clientSecret = clientSecret;
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

    /** US-16: the seats are held and a payment was created: the user can now pay with their card. */
    public void awaitPayment(Payment payment) {
        requireStatus(BookingStatus.RESERVED);
        if (payment == null) {
            throw new InvalidBookingException("Payment is required");
        }
        this.paymentIntentId = payment.id();
        this.clientSecret = payment.clientSecret();
        status = BookingStatus.AWAITING_PAYMENT;
    }

    /**
     * US-17: Stripe says the payment succeeded. Only a booking awaiting payment can be confirmed:
     * an EXPIRED one may already have its seats sold to someone else (see HandlePaymentResult).
     */
    public void confirm() {
        requireStatus(BookingStatus.AWAITING_PAYMENT);
        status = BookingStatus.CONFIRMED;
    }

    /** Not paid in time: the seats go back on sale. Only a booking that holds stock can expire. */
    public void expire() {
        if (!EXPIRABLE_STATUSES.contains(status)) {
            throw new IllegalStateException("Booking " + id + " is " + status + ", it cannot expire");
        }
        status = BookingStatus.EXPIRED;
    }

    public boolean isRequested() {
        return status == BookingStatus.REQUESTED;
    }

    public boolean isReserved() {
        return status == BookingStatus.RESERVED;
    }

    /** Price of the tickets, without fees. */
    public Money subtotal() {
        return Money.ofCents(lines.stream().mapToLong(l -> l.subtotal().cents()).sum());
    }

    /** US-16: FEE_PER_TICKET for every ticket of the booking. */
    public Money fees() {
        int tickets = lines.stream().mapToInt(BookingLine::quantity).sum();
        return Money.ofCents(FEE_PER_TICKET.cents() * tickets);
    }

    /** What the user pays: tickets + fees (README: total and fees in cents). */
    public Money total() {
        return Money.ofCents(subtotal().cents() + fees().cents());
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
    public String paymentIntentId() { return paymentIntentId; }
    public String clientSecret() { return clientSecret; }
}
