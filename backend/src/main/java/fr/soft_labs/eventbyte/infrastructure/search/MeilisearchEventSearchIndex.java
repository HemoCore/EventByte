package fr.soft_labs.eventbyte.infrastructure.search;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import fr.soft_labs.eventbyte.domain.models.event.Coordinates;
import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class MeilisearchEventSearchIndex implements EventSearchIndex {

    static final String INDEX = "events";

    private final Client client;
    private final JsonMapper jsonMapper;

    public MeilisearchEventSearchIndex(Client client, JsonMapper jsonMapper) {
        this.client = client;
        this.jsonMapper = jsonMapper;
    }

    public void configure() {
        Index index = client.index(INDEX);
        index.updateSearchableAttributesSettings(
                new String[]{"title", "venueName", "description", "venueAddress"});
        index.updateFilterableAttributesSettings(new String[]{"_geo", "startAt"});
    }

    @Override
    public void index(Collection<Event> events) {
        List<Map<String, Object>> documents = events.stream()
                .map(MeilisearchEventSearchIndex::toDocument)
                .toList();
        client.index(INDEX).addDocuments(jsonMapper.writeValueAsString(documents), "id");
    }

    @Override
    public void remove(String eventId) {
        client.index(INDEX).deleteDocument(eventId);
    }

    @Override
    public List<String> search(String query, Coordinates center, double radiusKm, Instant from, int limit) {
        SearchRequest request = SearchRequest.builder()
                .q(query)
                .filter(new String[]{filter(center, radiusKm, from)})
                .limit(limit)
                .attributesToRetrieve(new String[]{"id"})
                .build();
        return client.index(INDEX).search(request).getHits().stream()
                .map(hit -> String.valueOf(hit.get("id")))
                .toList();
    }

    static String filter(Coordinates center, double radiusKm, Instant from) {
        return String.format(Locale.ROOT, "_geoRadius(%.6f, %.6f, %d) AND startAt >= %d",
                center.latitude(), center.longitude(), Math.round(radiusKm * 1000), from.getEpochSecond());
    }

    static Map<String, Object> toDocument(Event event) {
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("id", event.id());
        document.put("title", event.title());
        document.put("description", event.description() == null ? "" : event.description());
        document.put("venueName", event.venue().name());
        document.put("venueAddress", event.venue().address());
        document.put("_geo", Map.of("lat", event.venue().latitude(), "lng", event.venue().longitude()));
        document.put("startAt", event.startAt() == null ? 0L : event.startAt().getEpochSecond());
        return document;
    }
}
