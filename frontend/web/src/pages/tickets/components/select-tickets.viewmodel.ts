import { selectIsLoadingUserBookings, selectUserBookings } from "@/core/booking/booking.slice";
import { RootState } from "@/core/create-store";
import { eventsSelectors } from "@/core/events/events.slice";
import { createSelector } from "@reduxjs/toolkit";

type LoadingState = {
    type: 'loading'
}

type LoadedState = {
    type: 'loaded',
    tickets: string[]
}

type EmptyState = {
    type: 'empty'
}
type TicketsViewmodel = LoadingState | LoadedState | EmptyState

export type SelectTicketsViewmodelParams = {
    now: Date
    status: 'upcoming' | 'past'
}

export const selectTicketsViewmodel =
    ({ now, status }: SelectTicketsViewmodelParams) => createSelector(
        [selectIsLoadingUserBookings, selectUserBookings, (state: RootState) => state],
        (isLoading, bookings, state): TicketsViewmodel => {
            if (isLoading) {
                return {
                    type: 'loading'
                }
            }

            const confirmedBookings = bookings.filter(booking => booking.status === 'confirmed')

            const isEmpty = confirmedBookings.length === 0

            if (isEmpty) {
                return {
                    type: 'empty'
                }
            }

            const tickets = []

            for (const booking of confirmedBookings) {
                const event = eventsSelectors.selectById(state, booking.eventId)

                if (!event) {
                    continue
                }

                const isUpcoming = new Date(event.startAt) > now

                if (status === 'upcoming' && isUpcoming) {
                    tickets.push(booking.id)
                } else if (status === 'past' && !isUpcoming) {
                    tickets.push(booking.id)
                }
            }

            return {
                type: 'loaded',
                tickets
            }
        }) 
