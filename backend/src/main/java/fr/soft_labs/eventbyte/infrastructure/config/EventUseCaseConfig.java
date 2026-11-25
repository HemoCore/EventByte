package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.domain.usecases.event.DeleteEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.IngestEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventUseCaseConfig {

    @Bean
    IngestEvent ingestEvent(EventRepository eventRepository) {
        return new IngestEvent(eventRepository);
    }

    @Bean
    DeleteEvent deleteEvent(EventRepository eventRepository) {
        return new DeleteEvent(eventRepository);
    }

    @Bean
    EventSummaryAssembler eventSummaryAssembler(LikeCounter likeCounter, TicketAvailability ticketAvailability) {
        return new EventSummaryAssembler(likeCounter, ticketAvailability);
    }

    @Bean
    GetEvent getEvent(EventRepository eventRepository, EventSummaryAssembler eventSummaryAssembler) {
        return new GetEvent(eventRepository, eventSummaryAssembler);
    }
}
