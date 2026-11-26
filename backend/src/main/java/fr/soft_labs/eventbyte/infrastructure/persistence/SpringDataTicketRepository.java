package fr.soft_labs.eventbyte.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

interface SpringDataTicketRepository extends JpaRepository<TicketEntity, String> {

    // "join fetch" loads the event in the same query, so reading ticket.getEvent() costs nothing extra
    @Query("SELECT t FROM TicketEntity t JOIN FETCH t.event WHERE t.id IN :ids")
    List<TicketEntity> findAllWithEventByIdIn(@Param("ids") Collection<String> ids);
}
