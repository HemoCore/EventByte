package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.usecases.event.SearchEvents;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchEventsController {

    private final SearchEvents searchEvents;

    public SearchEventsController(SearchEvents searchEvents) {
        this.searchEvents = searchEvents;
    }

    private static final double METERS_PER_KM = 1000;

    // radius in meters: the frontend sends the value of its distance menu (5000 = 5 km)
    @GetMapping("/events/search")
    public List<EventResponse> search(@RequestParam double lat, @RequestParam double lng,
                                      @RequestParam double radius,
                                      @RequestParam(defaultValue = "") String query) {
        double radiusKm = radius / METERS_PER_KM;
        return searchEvents.execute(query, new Coordinates(lat, lng), radiusKm).stream()
                .map(EventResponse::from)
                .toList();
    }
}
