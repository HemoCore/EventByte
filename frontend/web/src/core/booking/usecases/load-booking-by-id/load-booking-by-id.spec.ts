import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { FakeBookingGateway } from "../../adapters/fake-booking.gateway";
import { loadBookingById } from "./load-booking-by-id";
import { stateBuilder } from "@/core/state-builder";
import { bookingEntityBuilder } from "../../entities/booking.entity";
import { bookingLineEntityBuilder } from "../../entities/booking-line.entity";
import { FakeEventsGateway } from "@/core/events/adapters/fake-events.gateway";
import { eventEntityBuilder } from "@/core/events/entities/event.entity";

describe('Feature: Load booking by id', () => {
    test('Booking and event are loaded', async () => {
        const bookingGateway = new FakeBookingGateway()
        const eventsGateway = new FakeEventsGateway()

        const event = eventEntityBuilder().withId('event-1').build()
        eventsGateway.events = [event]

        bookingGateway.addBooking({
            id: 'booking-1',
            eventId: 'event-1',
            bookedAt: new Date("2026-01-01"),
            lines: [{
                id: 'early-bird',
                name: 'Early Bird',
                quantity: 1,
                price: 13
            }],
            status: 'awaiting-payment',
            total: 13,
            fees: 0,
            clientSecret: 'client-secret'
        })
        const store = createTestStore({ bookingGateway, eventsGateway })

        await store.dispatch(loadBookingById('booking-1'))

        expect(store.getState().booking).toEqual(stateBuilder()
            .withBooking(bookingEntityBuilder()
                .withId('booking-1')
                .withEventId('event-1')
                .withBookedAt(new Date("2026-01-01"))
                .withStatus('awaiting-payment')
                .withTotal(13)
                .withFees(0)
                .withClientSecret('client-secret')
                .withLines([
                    bookingLineEntityBuilder()
                        .withId('early-bird')
                        .withName('Early Bird')
                        .withQuantity(1)
                        .withPrice(13)
                        .build()
                ])
                .build()
            )
            .build().booking)

        expect(store.getState().events.entities['event-1']).toBeDefined()
    })

    test('Booking is loading when pending', () => {
        const store = createTestStore()

        store.dispatch(loadBookingById('booking-1'))

        expect(store.getState()).toEqual(stateBuilder()
            .withBookingLoading({ id: 'booking-1', loading: true })
            .build())
    })
})
