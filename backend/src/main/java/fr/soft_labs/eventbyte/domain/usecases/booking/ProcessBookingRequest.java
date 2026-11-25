package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.StockReservation;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog.TicketOffer;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProcessBookingRequest {

    private final BookingRepository bookingRepository;
    private final TicketCatalog ticketCatalog;
    private final StockReservation stockReservation;

    public ProcessBookingRequest(BookingRepository bookingRepository, TicketCatalog ticketCatalog,
                                 StockReservation stockReservation) {
        this.bookingRepository = bookingRepository;
        this.ticketCatalog = ticketCatalog;
        this.stockReservation = stockReservation;
    }

    public void execute(String bookingId) {
        Optional<Booking> found = bookingRepository.findById(bookingId);
        if (found.isEmpty() || !found.get().isRequested()) {
            return; // unknown, or already processed (duplicate message): do nothing
        }
        Booking booking = found.get();

        Map<String, Integer> quantities = booking.lines().stream()
                .collect(Collectors.toMap(BookingLine::ticketId, BookingLine::quantity));

        Map<String, Integer> initialStock = ticketCatalog.findByIds(quantities.keySet()).values().stream()
                .collect(Collectors.toMap(TicketOffer::ticketId, TicketOffer::initialQuantity));

        if (stockReservation.reserve(quantities, initialStock)) {
            booking.reserve();
        } else {
            booking.reject();
        }
        bookingRepository.save(booking);
    }
}
