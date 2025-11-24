import { userAuthenticated } from "@/core/auth/auth.slice";
import { AppDispatch, Dependencies, RootState } from "@/core/create-store";
import { isAnyOf, ListenerMiddlewareInstance } from "@reduxjs/toolkit";
import { loadLikedEvents } from "../../usecases/load-liked-events/load-liked-events.usecase";
import { bookingChanged } from "@/core/booking/booking.slice";

export const onUserAuthenticated = (listener: ListenerMiddlewareInstance<RootState, AppDispatch, Dependencies>,) => {
    listener.startListening({
        matcher: isAnyOf(userAuthenticated),
        effect: (action: ReturnType<typeof userAuthenticated>, { dispatch, extra }) => {
            if (!action.payload) {
                return
            }
            dispatch(loadLikedEvents())

            extra.bookingGateway.subscribeToBookingChanges((booking) => {
                dispatch(bookingChanged(booking))
            })
        }
    })

    return listener.middleware
}
