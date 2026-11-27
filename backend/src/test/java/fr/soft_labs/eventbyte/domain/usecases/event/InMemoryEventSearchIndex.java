package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class InMemoryEventSearchIndex implements EventSearchIndex {

    private final Map<String, Event> documents = new LinkedHashMap<>();
    private final List<String> queries = new ArrayList<>();
    private final List<Double> radiiKm = new ArrayList<>();
    private List<String> fixedResults;
    private int indexCalls;

    public static InMemoryEventSearchIndex returning(String... ids) {
        InMemoryEventSearchIndex index = new InMemoryEventSearchIndex();
        index.fixedResults = List.of(ids);
        return index;
    }

    @Override
    public void index(Collection<Event> events) {
        indexCalls++;
        events.forEach(event -> documents.put(event.id(), event));
    }

    @Override
    public void remove(String eventId) {
        documents.remove(eventId);
    }

    @Override
    public List<String> search(String query, Coordinates center, double radiusKm, Instant from, int limit) {
        queries.add(query);
        radiiKm.add(radiusKm);
        if (fixedResults != null) {
            return fixedResults;
        }
        String text = query.toLowerCase(Locale.ROOT);
        return documents.values().stream()
                .filter(e -> e.startAt() != null && !e.startAt().isBefore(from))
                .filter(e -> center.distanceKmTo(
                        new Coordinates(e.venue().latitude(), e.venue().longitude())) <= radiusKm)
                .filter(e -> text.isEmpty() || Stream.of(e.title(), e.description(), e.venue().name(), e.venue().address())
                        .anyMatch(field -> field != null && field.toLowerCase(Locale.ROOT).contains(text)))
                .limit(limit)
                .map(Event::id)
                .toList();
    }

    public boolean contains(String eventId) {
        return documents.containsKey(eventId);
    }

    public Event document(String eventId) {
        return documents.get(eventId);
    }

    public int size() {
        return documents.size();
    }

    public List<String> queries() {
        return queries;
    }

    public List<Double> radiiKm() {
        return radiiKm;
    }

    public int indexCalls() {
        return indexCalls;
    }
}
