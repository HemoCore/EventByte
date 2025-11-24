import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const loadBookingById = createAppAsyncThunk('booking/loadBookingById', async (id: string, { extra: { bookingGateway, eventsGateway }, rejectWithValue }) => {
    const booking = await bookingGateway.getById(id)
    if (!booking) {
        return rejectWithValue('Booking not found')
    }

    const event = await eventsGateway.getEventById(booking.eventId)

    return {
        booking: {
            ...booking,
            bookedAt: booking.bookedAt.toISOString()
        },
        event
    }
})
