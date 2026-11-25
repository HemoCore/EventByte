package fr.soft_labs.eventbyte.infrastructure.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.soft_labs.eventbyte.domain.ports.shared.IdGenerator;

@Configuration 
public class CommonConfig {
    
    @Bean 
    IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }
}
