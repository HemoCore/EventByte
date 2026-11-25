package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.LikeCounter;
import fr.soft_labs.eventbyte.domain.ports.event.TicketAvailability;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventSummaryConfig {

    @Bean
    EventSummaryAssembler eventSummaryAssembler(LikeCounter likeCounter, TicketAvailability ticketAvailability) {
        return new EventSummaryAssembler(likeCounter, ticketAvailability);
    }
}
