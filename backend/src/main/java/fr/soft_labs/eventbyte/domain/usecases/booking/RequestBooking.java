package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.models.booking.Booking;
import fr.soft_labs.eventbyte.domain.models.booking.BookingLine;
import fr.soft_labs.eventbyte.domain.models.booking.InvalidBookingException;
import fr.soft_labs.eventbyte.domain.models.event.TicketNotFoundException;
import fr.soft_labs.eventbyte.domain.models.user.UserId;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRequestPublisher;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog.TicketOffer;
import fr.soft_labs.eventbyte.domain.ports.shared.IdGenerator;

import java.time.Clock;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestBooking {

    public record Item(String ticketId, int quantity) {
    }

    private final TicketCatalog ticketCatalog;
    private final BookingRepository bookingRepository;
    private final BookingRequestPublisher publisher;
    private final IdGenerator idGenerator;
    private final Clock clock;

    public RequestBooking(TicketCatalog ticketCatalog, BookingRepository bookingRepository,
                          BookingRequestPublisher publisher, IdGenerator idGenerator, Clock clock) {
        this.ticketCatalog = ticketCatalog;
        this.bookingRepository = bookingRepository;
        this.publisher = publisher;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    public String execute(UserId userId, List<Item> items) {
        // 1. at least one item, no ticket twice
        if (items == null || items.isEmpty()) {
            throw new InvalidBookingException("At least one ticket is required");
        }
        Set<String> ticketIds = new LinkedHashSet<>();
        for (Item item : items) {
            if (item == null || item.ticketId() == null || item.ticketId().isBlank()) {
                throw new InvalidBookingException("Ticket id is required");
            }
            if (!ticketIds.add(item.ticketId())) {
                throw new InvalidBookingException("Ticket listed twice: " + item.ticketId());
            }
        }

        // 2. every ticket must exist
        Map<String, TicketOffer> offers = ticketCatalog.findByIds(ticketIds);
        for (String ticketId : ticketIds) {
            if (!offers.containsKey(ticketId)) {
                throw new TicketNotFoundException(ticketId);
            }
        }

        // 3. one booking = one event (README: a booking has a single eventId)
        Set<String> eventIds = offers.values().stream()
                .map(TicketOffer::eventId)
                .collect(Collectors.toSet());
        if (eventIds.size() != 1) {
            throw new InvalidBookingException("All tickets must belong to the same event");
        }

        // 4. name and price come from the catalog, never from the client
        List<BookingLine> lines = items.stream()
                .map(item -> {
                    TicketOffer offer = offers.get(item.ticketId());
                    return new BookingLine(offer.ticketId(), offer.name(), item.quantity(), offer.price());
                })
                .toList();

        // 5. create as REQUESTED, save, send to the queue, return the id
        Booking booking = Booking.request(
                idGenerator.nextId(), userId, eventIds.iterator().next(), clock.instant(), lines);
        bookingRepository.save(booking);
        publisher.publish(booking.id());
        return booking.id();
    }
}
