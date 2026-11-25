package fr.soft_labs.eventbyte.domain.models.booking;

public class InvalidBookingException extends IllegalArgumentException {
    public InvalidBookingException(String message) {
        super(message);
    }
}
