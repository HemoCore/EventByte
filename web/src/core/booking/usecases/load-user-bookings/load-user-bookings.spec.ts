import { describe, it, expect } from "vitest"
import { loadUserBookings } from "./load-user-bookings"
import { createTestStore } from "@/core/create-store"
import { bookingEntityBuilder } from "../../entities/booking.entity"
import { FakeBookingGateway } from "../../adapters/fake-booking.gateway"
import { FakeEventsGateway } from "@/core/events/adapters/fake-events.gateway"
import { eventEntityBuilder } from "@/core/events/entities/event.entity"
import { stateBuilder } from "@/core/state-builder"

describe('loadUserBookings', () => {
    it('should load user bookings and their events', async () => {
        const bookingGateway = new FakeBookingGateway()
        const eventsGateway = new FakeEventsGateway()

        const event1 = eventEntityBuilder().withId('event-1').build()
        const event2 = eventEntityBuilder().withId('event-2').build()

        eventsGateway.events = [event1, event2]

        const booking1 = bookingEntityBuilder()
            .withId('booking-1')
            .withEventId('event-1')
            .withStatus('confirmed')
            .build()

        const booking2 = bookingEntityBuilder()
            .withId('booking-2')
            .withEventId('event-2')
            .withStatus('awaiting-payment')
            .build()

        bookingGateway.addBooking({
            ...booking1,
            bookedAt: new Date(booking1.bookedAt),
            lines: []
        })

        bookingGateway.addBooking({
            ...booking2,
            bookedAt: new Date(booking2.bookedAt),
            lines: []
        })

        const store = createTestStore({ bookingGateway, eventsGateway })

        await store.dispatch(loadUserBookings())

        expect(store.getState()).toEqual(stateBuilder().withBooking(booking1).withBooking(booking2).withEvents([event1, event2]).build())
    })

    it('should return empty arrays when user has no bookings', async () => {
        const store = createTestStore()

        await store.dispatch(loadUserBookings())

        expect(store.getState()).toEqual(stateBuilder().build())
    })
})
