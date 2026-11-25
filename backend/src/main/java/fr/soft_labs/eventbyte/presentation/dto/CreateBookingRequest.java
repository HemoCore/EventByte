package fr.soft_labs.eventbyte.presentation.dto;

import java.util.List;

// README: POST /bookings body = { items: Array<{ id: string, quantity: number }> }
public record CreateBookingRequest(List<Item> items) {

    public record Item(String id, int quantity) {
    }
}
