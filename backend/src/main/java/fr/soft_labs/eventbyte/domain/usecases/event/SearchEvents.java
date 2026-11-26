package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.EventSummary;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;
import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchEvents {

    public static final int MAX_RESULTS = 50;

    private final EventSearchIndex searchIndex;
    private final EventsByIds eventsByIds;
    private final EventSummaryAssembler assembler;
    private final Clock clock;

    public SearchEvents(EventSearchIndex searchIndex, EventsByIds eventsByIds,
                        EventSummaryAssembler assembler, Clock clock) {
        this.searchIndex = searchIndex;
        this.eventsByIds = eventsByIds;
        this.assembler = assembler;
        this.clock = clock;
    }

    public List<EventSummary> execute(String query, Coordinates center, double radiusKm) {
        if (!(radiusKm > 0) || Double.isInfinite(radiusKm)) {
            throw new IllegalArgumentException("Rayon invalide : " + radiusKm);
        }
        String text = query == null ? "" : query.trim();

        List<String> ids = searchIndex.search(text, center, radiusKm, clock.instant(), MAX_RESULTS);
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<String, Event> found = eventsByIds.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Event::id, Function.identity(), (a, b) -> a));
        List<Event> byRelevance = ids.stream()
                .map(found::get)
                .filter(Objects::nonNull)
                .toList();
        return assembler.assemble(byRelevance);
    }
}
