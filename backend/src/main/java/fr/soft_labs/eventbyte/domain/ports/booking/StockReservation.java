package fr.soft_labs.eventbyte.domain.ports.booking;

import java.util.Map;

public interface StockReservation {

    /**
     * Reserve all quantities at once, or nothing.
     * @param quantityByTicketId     ticketId -> quantity asked
     * @param initialStockByTicketId ticketId -> quantity from the ticket (used the first time a ticket is booked)
     * @return true if everything was reserved, false if at least one ticket lacked stock
     */
    boolean reserve(Map<String, Integer> quantityByTicketId, Map<String, Integer> initialStockByTicketId);

    /**
     * Give reserved quantities back to the stock (expired booking).
     * @param quantityByTicketId ticketId -> quantity to give back
     */
    void release(Map<String, Integer> quantityByTicketId);
}
