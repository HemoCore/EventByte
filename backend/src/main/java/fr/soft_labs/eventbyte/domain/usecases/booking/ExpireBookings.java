package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.StockReservation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** US-14: unpaid bookings expire after a delay and their seats go back on sale. */
public class ExpireBookings {

    private final BookingRepository bookingRepository;
    private final StockReservation stockReservation;
    private final Clock clock;
    private final Duration delay;

    public ExpireBookings(BookingRepository bookingRepository, StockReservation stockReservation,
                          Clock clock, Duration delay) {
        if (delay == null || delay.isNegative() || delay.isZero()) {
            throw new IllegalArgumentException("Expiration delay must be positive: " + delay);
        }
        this.bookingRepository = bookingRepository;
        this.stockReservation = stockReservation;
        this.clock = clock;
        this.delay = delay;
    }

    /** @return how many bookings expired */
    public int execute() {
        Instant cutoff = clock.instant().minus(delay);
        List<Booking> expirable = bookingRepository.findExpirable(cutoff);

        for (Booking booking : expirable) {
            booking.expire();
            // Save EXPIRED first, then give the stock back: if we crash in between,
            // a few seats stay locked (undersell), but stock is never given back twice (oversell).
            bookingRepository.save(booking);
            stockReservation.release(quantities(booking));
        }
        return expirable.size();
    }

    private static Map<String, Integer> quantities(Booking booking) {
        return booking.lines().stream()
                .collect(Collectors.toMap(BookingLine::ticketId, BookingLine::quantity));
    }
}
