package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetEventsByIds {

    private final EventsByIds eventsByIds;
    private final EventSummaryAssembler assembler;

    public GetEventsByIds(EventsByIds eventsByIds, EventSummaryAssembler assembler) {
        this.eventsByIds = eventsByIds;
        this.assembler = assembler;
    }

    public List<EventSummary> execute(List<String> ids) {
        Set<String> requested = ids.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (requested.isEmpty()) {
            return List.of();
        }

        Map<String, Event> found = eventsByIds.findAllByIds(requested).stream()
                .collect(Collectors.toMap(Event::id, Function.identity(), (a, b) -> a));

        List<Event> inRequestedOrder = requested.stream()
                .map(found::get)
                .filter(Objects::nonNull)
                .toList();
        return assembler.assemble(inRequestedOrder);
    }
}
