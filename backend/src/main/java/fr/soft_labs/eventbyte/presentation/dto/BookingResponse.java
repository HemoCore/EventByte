package fr.soft_labs.eventbyte.presentation.dto;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;

import java.time.Instant;
import java.util.List;

// README: Booking = { id, eventId, bookedAt, lines, status, total, fees }
public record BookingResponse(
        String id,
        String eventId,
        Instant bookedAt,
        List<LineResponse> lines,
        String status,
        long total,
        long fees
) {

    public record LineResponse(String id, String name, int quantity, long price) {

        static LineResponse from(BookingLine line) {
            return new LineResponse(line.ticketId(), line.name(), line.quantity(), line.unitPrice().cents());
        }
    }

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.id(),
                booking.eventId(),
                booking.bookedAt(),
                booking.lines().stream().map(LineResponse::from).toList(),
                booking.status().value(),
                booking.total().cents(),
                0);
    }
}