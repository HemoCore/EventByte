package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.ports.booking.BookingRequestPublisher;

import java.util.ArrayList;
import java.util.List;

class RecordingPublisher implements BookingRequestPublisher {

    final List<String> published = new ArrayList<>();

    @Override
    public void publish(String bookingId) {
        published.add(bookingId);
    }
}
