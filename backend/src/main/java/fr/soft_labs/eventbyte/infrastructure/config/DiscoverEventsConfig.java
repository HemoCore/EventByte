package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.NearbyEvents;
import fr.soft_labs.eventbyte.domain.usecases.event.DiscoverEvents;
import fr.soft_labs.eventbyte.domain.usecases.event.EventSummaryAssembler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DiscoverEventsConfig {

    @Bean
    DiscoverEvents discoverEvents(NearbyEvents nearbyEvents, EventSummaryAssembler eventSummaryAssembler) {
        return new DiscoverEvents(nearbyEvents, eventSummaryAssembler, Clock.systemUTC());
    }
}
