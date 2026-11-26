package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEvent;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventTickets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetEventConfig {

    @Bean
    GetEvent getEvent(EventRepository eventRepository, EventSummaryAssembler eventSummaryAssembler) {
        return new GetEvent(eventRepository, eventSummaryAssembler);
    }

    @Bean
    GetEventTickets getEventTickets(EventRepository eventRepository, TicketAvailability ticketAvailability) {
        return new GetEventTickets(eventRepository, ticketAvailability);
    }
}