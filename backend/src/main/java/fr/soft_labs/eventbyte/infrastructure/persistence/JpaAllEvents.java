package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.ports.event.AllEvents;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class JpaAllEvents implements AllEvents {

    private final SpringDataEventRepository springData;

    public JpaAllEvents(SpringDataEventRepository springData) {
        this.springData = springData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return springData.findAll().stream()
                .map(JpaEventRepository::toDomain)
                .toList();
    }
}
