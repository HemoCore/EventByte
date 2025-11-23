import { selectIsLoadingUserBookings, selectUserBookings } from "@/core/booking/booking.slice";
import { RootState } from "@/core/create-store";
import { createSelector } from "@reduxjs/toolkit";

type LoadingState = {
    type: 'loading'
}

type LoadedState = {
    type: 'loaded'
    hasPast: boolean
    hasUpcoming: boolean
    pastIds: string[]
    upcomingIds: string[]
}

export type TicketsViewmodel = LoadingState | LoadedState

export const selectTicketsViewmodel = (now: Date): (state: RootState) => TicketsViewmodel => createSelector(
    [
        selectIsLoadingUserBookings,
        selectUserBookings
    ], (isLoading, bookings) => {
        if (isLoading) {
            return {
                type: 'loading'
            }
        }

        const approvedBookings = bookings.filter(booking => booking.status === 'confirmed')
        const upcomingIds = approvedBookings.filter(booking => new Date(booking.bookedAt) > now).map(b => b.id)
        const pastIds = approvedBookings.filter(booking => new Date(booking.bookedAt) < now).map(b => b.id)

        return {
            type: 'loaded',
            upcomingIds,
            hasUpcoming: upcomingIds.length > 0,
            hasPast: pastIds.length > 0,
            pastIds
        }
    })
