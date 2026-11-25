package fr.soft_labs.eventbyte.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StrapiEventMessage(
        String id,
        String title,
        String description,
        String cover,
        Instant startAt,
        VenueMessage venue,
        List<TicketMessage> tickets
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VenueMessage(String name, String address, Double latitude, Double longitude) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TicketMessage(String id, String name, Integer quantity, BigDecimal price) {
    }
}
