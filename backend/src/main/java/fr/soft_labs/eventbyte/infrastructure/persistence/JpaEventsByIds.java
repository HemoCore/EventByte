package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.EventsByIds;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
public class JpaEventsByIds implements EventsByIds {

    private final SpringDataEventRepository springData;

    public JpaEventsByIds(SpringDataEventRepository springData) {
        this.springData = springData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllByIds(Collection<String> ids) {
        return springData.findAllById(ids).stream()
                .map(JpaEventRepository::toDomain)
                .toList();
    }
}
