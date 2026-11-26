package fr.soft_labs.eventbyte.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Turns on @Scheduled methods (BookingExpirationScheduler)
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
