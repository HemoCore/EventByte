package fr.soft_labs.eventbyte.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.models.event.Venue;

import java.time.Instant;

/** Format d'un événement exposé par l'API, tel que défini dans le README. */
public record EventResponse(
        String id,
        String title,
        String cover,
        long likedCount,
        VenueResponse venue,
        @JsonProperty("isSoldOut") boolean isSoldOut,
        Instant startAt,
        long lowestPrice,
        String description
) {

    public record VenueResponse(String name, String adresse, CoordinatesResponse coordinates) {
    }

    public record CoordinatesResponse(double latitude, double longitude) {
    }

    public static EventResponse from(EventSummary summary) {
        Event event = summary.event();
        Venue venue = event.venue();
        return new EventResponse(
                event.id(),
                event.title(),
                nullToEmpty(event.cover()),
                summary.likedCount(),
                new VenueResponse(venue.name(), venue.address(),
                        new CoordinatesResponse(venue.latitude(), venue.longitude())),
                summary.soldOut(),
                event.startAt(),
                summary.lowestPrice().cents(),
                nullToEmpty(event.description())
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
