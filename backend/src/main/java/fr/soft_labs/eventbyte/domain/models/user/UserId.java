package fr.soft_labs.eventbyte.domain.models.user;

public record UserId(String value) {

    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}