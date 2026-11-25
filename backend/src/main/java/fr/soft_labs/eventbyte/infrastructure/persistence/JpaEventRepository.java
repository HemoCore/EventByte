package fr.soft_labs.eventbyte.infrastructure.persistence;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import fr.soft_labs.eventbyte.domain.models.event.Money;
import fr.soft_labs.eventbyte.domain.models.event.Ticket;
import fr.soft_labs.eventbyte.domain.models.event.Venue;
import fr.soft_labs.eventbyte.domain.ports.event.EventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// 1. La classe : elle implémente le port du domaine, et Spring la crée automatiquement
@Component
public class JpaEventRepository implements EventRepository {

    // L'outil de Spring Data qui parle vraiment à la base
    private final SpringDataEventRepository springData;

    public JpaEventRepository(SpringDataEventRepository springData) {
        this.springData = springData;
    }

    // 2. save : crée l'événement, ou met à jour celui qui existe (pas de doublon)
    @Override
    @Transactional
    public void save(Event event) {
        // On cherche la ligne existante ; si elle n'existe pas, on en prépare une nouvelle
        EventEntity entity = springData.findById(event.id())
                .orElseGet(() -> new EventEntity(event.id()));

        // On recopie les informations du domaine dans l'entité
        entity.setTitle(event.title());
        entity.setDescription(event.description());
        entity.setCover(event.cover());
        entity.setStartAt(event.startAt());
        entity.setVenueName(event.venue().name());
        entity.setVenueAddress(event.venue().address());
        entity.setVenueLatitude(event.venue().latitude());
        entity.setVenueLongitude(event.venue().longitude());
        syncTickets(entity, event);

        springData.save(entity);
    }

    // 3. syncTickets : met à jour les tickets un par un
    private void syncTickets(EventEntity entity, Event event) {
        // Les tickets déjà en base, rangés par identifiant
        Map<String, TicketEntity> existing = entity.getTickets().stream()
                .collect(Collectors.toMap(TicketEntity::getId, Function.identity()));
        // Les identifiants des tickets reçus dans le message
        Set<String> receivedIds = event.tickets().stream()
                .map(Ticket::id)
                .collect(Collectors.toSet());

        // a) On retire les tickets qui ne sont plus dans le message
        entity.getTickets().removeIf(ticket -> !receivedIds.contains(ticket.getId()));

        for (Ticket ticket : event.tickets()) {
            TicketEntity ticketEntity = existing.get(ticket.id());
            // b) Si le ticket est nouveau, on le crée et on le rattache à l'événement
            if (ticketEntity == null) {
                ticketEntity = new TicketEntity(ticket.id());
                ticketEntity.setEvent(entity);
                entity.getTickets().add(ticketEntity);
            }
            // c) Dans tous les cas, on met à jour ses informations
            ticketEntity.setName(ticket.name());
            ticketEntity.setPriceCents(Math.toIntExact(ticket.price().cents()));
            ticketEntity.setQuantity(ticket.quantity());
        }
    }

    // 4. findById : lit un événement et le convertit en objet du domaine
    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findById(String id) {
        return springData.findById(id).map(JpaEventRepository::toDomain);
    }

    // deleteById : supprime l'événement et ses tickets (sans erreur s'il n'existe pas)
    @Override
    @Transactional
    public void deleteById(String id) {
        springData.deleteById(id);
    }

    // 5. toDomain : convertit une entité de la base en objet du domaine
    static Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCover(),
                entity.getStartAt(),
                new Venue(entity.getVenueName(), entity.getVenueAddress(),
                        entity.getVenueLatitude(), entity.getVenueLongitude()),
                entity.getTickets().stream()
                        .map(t -> new Ticket(t.getId(), t.getName(),
                                Money.ofCents(t.getPriceCents()), t.getQuantity()))
                        .toList()
        );
    }
}