package fr.soft_labs.eventbyte.domain.models.event;

public record Ticket(String id, String name, Money price, int quantity) {

    public Ticket {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant du ticket est obligatoire");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du ticket est obligatoire");
        }
        if (price == null) {
            throw new IllegalArgumentException("Le prix du ticket est obligatoire");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative : " + quantity);
        }
    }
}
