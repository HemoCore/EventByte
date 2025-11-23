import { combineReducers } from "@reduxjs/toolkit";
import { authSlice } from "./auth/auth.slice";
import { eventsSlice } from "./events/events.slice";
import { ticketsSlice } from "./tickets/tickets.slice";
import { cartSlice } from "./cart/cart.slice";
import { bookingSlice } from "./booking/booking.slice";
import { searchSlice } from "./search/search.slice";

export const rootReducer = combineReducers({
    [authSlice.name]: authSlice.reducer,
    [eventsSlice.name]: eventsSlice.reducer,
    [ticketsSlice.name]: ticketsSlice.reducer,
    [cartSlice.name]: cartSlice.reducer,
    [bookingSlice.name]: bookingSlice.reducer,
    [searchSlice.name]: searchSlice.reducer
});
