package fr.soft_labs.eventbyte.presentation.controllers;

import fr.soft_labs.eventbyte.domain.usecases.event.GetEventsByIds;
import fr.soft_labs.eventbyte.presentation.dto.EventResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetEventsByIdsController {

    private final GetEventsByIds getEventsByIds;

    public GetEventsByIdsController(GetEventsByIds getEventsByIds) {
        this.getEventsByIds = getEventsByIds;
    }

    @GetMapping("/events")
    public List<EventResponse> getByIds(@RequestParam(required = false, defaultValue = "") List<String> ids) {
        return getEventsByIds.execute(ids).stream()
                .map(EventResponse::from)
                .toList();
    }
}
