package fr.soft_labs.eventbyte.domain.usecases.event;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.AllEvents;
import fr.soft_labs.eventbyte.domain.ports.event.EventSearchIndex;

import java.util.List;

public class ReindexEvents {

    private final AllEvents allEvents;
    private final EventSearchIndex searchIndex;

    public ReindexEvents(AllEvents allEvents, EventSearchIndex searchIndex) {
        this.allEvents = allEvents;
        this.searchIndex = searchIndex;
    }

    public int execute() {
        List<Event> events = allEvents.findAll();
        if (!events.isEmpty()) {
            searchIndex.index(events);
        }
        return events.size();
    }
}
