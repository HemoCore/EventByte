package fr.soft_labs.eventbyte.domain.models.event;

public record Venue(String name, String address, double latitude, double longitude) {

    public Venue {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du lieu est obligatoire");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("L'adresse du lieu est obligatoire");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude invalide : " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude invalide : " + longitude);
        }
    }
}