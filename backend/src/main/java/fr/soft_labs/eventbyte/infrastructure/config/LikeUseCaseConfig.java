package fr.soft_labs.eventbyte.infrastructure.config;

import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import fr.soft_labs.eventbyte.domain.ports.like.LikeRepository;
import fr.soft_labs.eventbyte.domain.usecases.like.LikeEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LikeUseCaseConfig {

    @Bean
    LikeEvent likeEvent(LikeRepository likeRepository, EventRepository eventRepository) {
        return new LikeEvent(likeRepository, eventRepository);
    }
}
