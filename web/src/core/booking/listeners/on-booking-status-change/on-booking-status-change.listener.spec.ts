import { describe, it, expect } from "vitest"
import { createTestStore } from "@/core/create-store"
import { FakeBookingGateway } from "../../adapters/fake-booking.gateway"
import { bookingEntityBuilder } from "../../entities/booking.entity"
import { stateBuilder } from "@/core/state-builder"
import { createOnBookingChangeListener } from "./on-booking-status-change.listener"

describe('onBookingChange listener', () => {
    it('should update booking when gateway notifies change', async () => {
        const bookingGateway = new FakeBookingGateway()
        const booking = bookingEntityBuilder()
            .withId('booking-1')
            .withEventId('event-1')
            .withStatus('awaiting-payment')
            .build()

        bookingGateway.addBooking({
            ...booking,
            bookedAt: new Date(booking.bookedAt),
            lines: []
        })

        const store = createTestStore({ bookingGateway }, stateBuilder().withBooking(booking).build())

        createOnBookingChangeListener(bookingGateway, store.dispatch)()

        const updatedBooking = {
            ...booking,
            bookedAt: new Date(booking.bookedAt),
            status: 'confirmed' as const,
            lines: []
        }

        bookingGateway.simulateBookingChange(updatedBooking)

        expect(store.getState())
            .toEqual(stateBuilder()
                .withBooking(
                    bookingEntityBuilder(booking).withStatus('confirmed').build()
                ).build())
    })

})
