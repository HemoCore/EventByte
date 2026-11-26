package fr.soft_labs.eventbyte.infrastructure.search;

import fr.soft_labs.eventbyte.domain.usecases.event.ReindexEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SearchIndexStartup implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexStartup.class);

    private final MeilisearchEventSearchIndex searchIndex;
    private final ReindexEvents reindexEvents;

    public SearchIndexStartup(MeilisearchEventSearchIndex searchIndex, ReindexEvents reindexEvents) {
        this.searchIndex = searchIndex;
        this.reindexEvents = reindexEvents;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            searchIndex.configure();
            log.info("{} events sent to the search index", reindexEvents.execute());
        } catch (RuntimeException e) {
            log.warn("Search index unavailable at startup: {}", e.getMessage());
        }
    }
}
