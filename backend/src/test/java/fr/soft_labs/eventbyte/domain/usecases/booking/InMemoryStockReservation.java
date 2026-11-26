package fr.soft_labs.eventbyte.domain.usecases.booking;

import fr.soft_labs.eventbyte.domain.ports.booking.StockReservation;

import java.util.HashMap;
import java.util.Map;

/** All-or-nothing in-memory stock, mimicking what the Redis adapter will do. */
class InMemoryStockReservation implements StockReservation {

    final Map<String, Integer> stock = new HashMap<>();
    int calls = 0;

    @Override
    public boolean reserve(Map<String, Integer> quantityByTicketId, Map<String, Integer> initialStockByTicketId) {
        calls++;
        initialStockByTicketId.forEach(stock::putIfAbsent);
        for (var entry : quantityByTicketId.entrySet()) {
            if (stock.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        quantityByTicketId.forEach((id, qty) -> stock.merge(id, -qty, Integer::sum));
        return true;
    }

    @Override
    public void release(Map<String, Integer> quantityByTicketId) {
        // like release-stock.lua: only tickets that have a counter get their stock back
        quantityByTicketId.forEach((id, qty) -> stock.computeIfPresent(id, (k, current) -> current + qty));
    }
}
