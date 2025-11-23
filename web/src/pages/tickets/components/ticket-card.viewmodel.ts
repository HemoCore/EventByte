import { selectBooking, selectBookingEvent } from "@/core/booking/booking.slice"
import { createSelector } from "@reduxjs/toolkit"

type TicketCardViewmodel = {
    id: string
    event: {
        title: string
        cover: string
        date: string
        location: string
    }
    quantity: number
    isUpcoming: boolean
}

type Params = {
    id: string
    now: Date
}

export const selectTicketCardViewmodel = ({ now, id }: Params) =>
    createSelector([selectBooking(id), selectBookingEvent(id)], (booking, event): TicketCardViewmodel => {
        event = event!

        const quantity = booking.lines.map((line) => line.quantity).reduce((a, b) => a + b, 0)
        const isUpcoming = new Date(event.startAt) > now

        const dateFormater = new Intl.DateTimeFormat('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric',
        })

        return {
            id: id,
            event: {
                title: event.title,
                cover: event.cover,
                date: dateFormater.format(new Date(event.startAt)),
                location: `${event.venue.name}, ${event.venue.adresse}`,
            },
            quantity,
            isUpcoming,
        }
    })
