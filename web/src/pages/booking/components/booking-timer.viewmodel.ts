import { selectBooking, selectIsBookingLoading } from "@/core/booking/booking.slice";
import { RootState } from "@/core/create-store";
import { Duration } from "@/core/shared/duration";
import { createSelector } from "@reduxjs/toolkit";

type LoadingState = {
    type: 'loading'
}
type LoadedState = {
    type: 'loaded'
    timeLeft: string
}

type Viewmodel = LoadingState | LoadedState

const EXPIRATION_DURATION = Duration.fromMinutes(10)

type Params = {
    now: Date,
    bookingId: string
}

export const selectBookingTimerViewmodel = ({ now, bookingId }: Params): (state: RootState) => Viewmodel => createSelector(
    [
        selectBooking(bookingId),
        selectIsBookingLoading(bookingId)
    ], (booking, isLoading) => {
        if (isLoading || !booking) {
            return {
                type: 'loading'
            }
        }

        const expirationDate = Duration.fromDate(new Date(booking.bookedAt)).add(EXPIRATION_DURATION)
        const remaining = expirationDate.sub(Duration.fromDate(now))

        const minutes = Math.max(0, Math.floor(remaining.minutes))
        const seconds = Math.max(0, Math.floor(remaining.seconds) % 60)

        const format = (num: number) => num.toString().padStart(2, '0')

        return {
            type: 'loaded',
            timeLeft: `${format(minutes)}:${format(seconds)}`
        }
    })
