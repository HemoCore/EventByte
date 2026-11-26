package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.GetEventsByIds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetEventsByIdsConfig {

    @Bean
    GetEventsByIds getEventsByIds(EventsByIds eventsByIds, EventSummaryAssembler eventSummaryAssembler) {
        return new GetEventsByIds(eventsByIds, eventSummaryAssembler);
    }
}
