package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.booking.BookingNotifier;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRepository;
import fr.soft_labs.eventbyte.domain.ports.booking.BookingRequestPublisher;
import fr.soft_labs.eventbyte.domain.ports.booking.StockReservation;
import fr.soft_labs.eventbyte.domain.ports.booking.TicketCatalog;
import fr.soft_labs.eventbyte.domain.ports.shared.IdGenerator;
import fr.soft_labs.eventbyte.domain.usecases.booking.ExpireBookings;
import fr.soft_labs.eventbyte.domain.usecases.booking.GetBooking;
import fr.soft_labs.eventbyte.domain.usecases.booking.GetUserBookings;
import fr.soft_labs.eventbyte.domain.usecases.booking.NotifyingBookingRepository;
import fr.soft_labs.eventbyte.domain.usecases.booking.ProcessBookingRequest;
import fr.soft_labs.eventbyte.domain.usecases.booking.RequestBooking;
import fr.soft_labs.eventbyte.infrastructure.persistence.JpaBookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class BookingUseCaseConfig {

    @Bean
    RequestBooking requestBooking(TicketCatalog ticketCatalog,
                                  BookingRepository bookingRepository,
                                  BookingRequestPublisher publisher,
                                  IdGenerator idGenerator) {
        return new RequestBooking(ticketCatalog, bookingRepository, publisher, idGenerator, Clock.systemUTC());
    }

    @Bean
    ProcessBookingRequest processBookingRequest(BookingRepository bookingRepository,
                                                TicketCatalog ticketCatalog,
                                                StockReservation stockReservation) {
        return new ProcessBookingRequest(bookingRepository, ticketCatalog, stockReservation);
    }

    @Bean
    GetBooking getBooking(BookingRepository bookingRepository) {
        return new GetBooking(bookingRepository);
    }

    @Bean
    GetUserBookings getUserBookings(BookingRepository bookingRepository) {
        return new GetUserBookings(bookingRepository);
    }

    @Bean
    ExpireBookings expireBookings(BookingRepository bookingRepository,
                                  StockReservation stockReservation,
                                  @Value("${eventbyte.booking.expiration}") Duration expiration) {
        return new ExpireBookings(bookingRepository, stockReservation, Clock.systemUTC(), expiration);
    }

    @Bean
    @Primary
    BookingRepository bookingRepository(JpaBookingRepository jpa, BookingNotifier notifier) {
        return new NotifyingBookingRepository(jpa, notifier);
    }
}
