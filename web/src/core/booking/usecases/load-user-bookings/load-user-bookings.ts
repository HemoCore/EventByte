import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const loadUserBookings = createAppAsyncThunk('booking/loadUserBookings', async (_, { extra: { bookingGateway, eventsGateway } }) => {
    const bookings = await bookingGateway.getUserBookings()
    const eventIds = [...new Set(bookings.map(b => b.eventId))]
    const events = eventIds.length > 0 ? await eventsGateway.getEventsByIds(eventIds) : []

    return {
        bookings: bookings.map(booking => ({
            ...booking,
            bookedAt: booking.bookedAt.toISOString()
        })),
        events
    }
})
