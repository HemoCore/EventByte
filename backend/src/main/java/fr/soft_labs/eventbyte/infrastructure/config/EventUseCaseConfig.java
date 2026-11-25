package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.usecases.event.DeleteEvent;
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
}
