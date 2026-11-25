package fr.soft_labs.eventbyte.domain.ports.booking;

public interface BookingRequestPublisher {

    void publish(String bookingId);
}