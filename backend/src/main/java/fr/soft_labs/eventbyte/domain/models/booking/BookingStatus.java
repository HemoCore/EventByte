package fr.soft_labs.eventbyte.domain.models.booking;

public enum BookingStatus {
    REQUESTED("requested"),
    RESERVED("reserved"),
    REJECTED("rejected"),
    AWAITING_PAYMENT("awaiting-payment"),
    EXPIRED("expired"),
    CONFIRMED("confirmed");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
