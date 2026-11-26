package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.AllEvents;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;
import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import fr.soft_labs.eventbyte.domain.usecases.event.ReindexEvents;
import fr.soft_labs.eventbyte.domain.usecases.event.SearchEvents;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SearchEventsConfig {

    @Bean
    SearchEvents searchEvents(EventSearchIndex searchIndex, EventsByIds eventsByIds,
                              EventSummaryAssembler eventSummaryAssembler) {
        return new SearchEvents(searchIndex, eventsByIds, eventSummaryAssembler, Clock.systemUTC());
    }

    @Bean
    ReindexEvents reindexEvents(AllEvents allEvents, EventSearchIndex searchIndex) {
        return new ReindexEvents(allEvents, searchIndex);
    }
}
