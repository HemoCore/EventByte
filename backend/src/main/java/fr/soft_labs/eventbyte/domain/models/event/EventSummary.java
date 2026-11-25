package fr.soft_labs.eventbyte.domain.models.event;

/**
 * Un événement tel qu'on le montre au public : l'événement lui-même,
 * plus les informations calculées (nombre de likes, complet ou non).
 */
public record EventSummary(Event event, long likedCount, boolean soldOut) {

    public EventSummary {
        if (event == null) {
            throw new IllegalArgumentException("L'événement est obligatoire");
        }
        if (likedCount < 0) {
            throw new IllegalArgumentException("Le nombre de likes ne peut pas être négatif : " + likedCount);
        }
    }

    /** Prix le plus bas parmi les tickets, 0 si l'événement n'a aucun ticket. */
    public Money lowestPrice() {
        return event.lowestPrice().orElse(Money.ofCents(0));
    }
}
