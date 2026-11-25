package fr.soft_labs.eventbyte.domain.models.event;

public record EventSummary(Event event, long likedCount, boolean soldOut) {

    public EventSummary {
        if (event == null) {
            throw new IllegalArgumentException("L'événement est obligatoire");
        }
        if (likedCount < 0) {
            throw new IllegalArgumentException("Le nombre de likes ne peut pas être négatif : " + likedCount);
        }
    }

    public Money lowestPrice() {
        return event.lowestPrice().orElse(Money.ofCents(0));
    }
}
