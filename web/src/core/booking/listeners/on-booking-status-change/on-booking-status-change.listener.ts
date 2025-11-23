import { AppDispatch } from "@/core/create-store";
import { BookingGateway } from "../../ports/booking.gateway";
import { bookingChanged } from "../../booking.slice";

export const createOnBookingChangeListener = (bookingGateway: BookingGateway, dispatch: AppDispatch) => () => {
    return bookingGateway.subscribeToBookingChanges((booking) => {
        dispatch(bookingChanged(booking))
    })
}
