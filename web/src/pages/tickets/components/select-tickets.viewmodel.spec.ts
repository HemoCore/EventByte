import { describe, expect, it } from "vitest";
import { selectTicketsViewmodel, SelectTicketsViewmodelParams } from "./select-tickets.viewmodel";
import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { bookingEntityBuilder } from "@/core/booking/entities/booking.entity";
import { eventEntityBuilder } from "@/core/events/entities/event.entity";

const event1 = eventEntityBuilder().withId('1').startAt(new Date('2025-01-05')).build()
const event2 = eventEntityBuilder().withId('2').startAt(new Date('2025-01-02')).build()
const event3 = eventEntityBuilder().withId('3').startAt(new Date('2025-01-10')).build()

const booking1 = bookingEntityBuilder().withId('1').withEventId('1').withStatus('confirmed').build()
const booking2 = bookingEntityBuilder().withId('2').withEventId('2').withStatus('confirmed').build()
const booking3 = bookingEntityBuilder().withId('3').withEventId('3').withStatus('confirmed').build()
const pendingBooking = bookingEntityBuilder().withId('4').withEventId('1').withStatus('awaiting-payment').build()

const testSelectTicketsViewmodel = (
    { now = new Date(), status = 'upcoming' }:
        Partial<SelectTicketsViewmodelParams> = {}
) => selectTicketsViewmodel({ now, status })

describe('selectTicketsViewmodel', () => {
    it('is in loading state when bookings is loading', () => {
        const store = createTestStore({}, stateBuilder().withUserBookingsLoading(true).build())
        const viewmodel = testSelectTicketsViewmodel()(store.getState())

        expect(viewmodel.type).toBe('loading')
    })

    it('is in loaded state when bookings is NOT loading', () => {
        const store = createTestStore({}, stateBuilder().withUserBookingsLoading(false).withBooking(booking1).build())
        const viewmodel = testSelectTicketsViewmodel()(store.getState())

        expect(viewmodel.type).toBe('loaded')
    })

    it('is in empty state when there are no tickets', () => {
        const store = createTestStore({}, stateBuilder().withUserBookingsLoading(false).build())
        const viewmodel = testSelectTicketsViewmodel()(store.getState())

        expect(viewmodel.type).toBe('empty')
    })

    it('is in empty state when there are tickets on pending bookings', () => {
        const store = createTestStore({}, stateBuilder().withUserBookingsLoading(false).withBooking(pendingBooking).build())
        const viewmodel = testSelectTicketsViewmodel()(store.getState())

        expect(viewmodel.type).toBe('empty')
    })

    it('retrieve all upcoming tickets ids', () => {
        const now = new Date('2025-01-03')
        const store = createTestStore({}, stateBuilder()
            .withEvents([
                event1,
                event2,
                event3
            ])
            .withBooking(
                booking1
            )
            .withBooking(booking2)
            .withBooking(booking3)
            .build()
        )

        const viewmodel = testSelectTicketsViewmodel({ status: 'upcoming', now })(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({ tickets: ['1', '3'] }))
    })

    it('retrieve all upcoming tickets ids', () => {
        const now = new Date('2025-01-03')
        const store = createTestStore({}, stateBuilder()
            .withEvents([
                event1,
                event2,
                event3
            ])
            .withBooking(
                booking1
            )
            .withBooking(booking2)
            .withBooking(booking3)
            .build()
        )

        const viewmodel = testSelectTicketsViewmodel({ status: 'past', now })(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({ tickets: ['2'] }))
    })

    it('pending bookings is not included', () => {
        const now = new Date('2025-01-03')
        const store = createTestStore({}, stateBuilder()
            .withEvents([
                event1,
                event2
            ])
            .withBooking(
                bookingEntityBuilder().withId('1').withEventId('1').withStatus('awaiting-payment').build()
            )
            .withBooking(booking2)
            .build()
        )

        const viewmodel = testSelectTicketsViewmodel({ status: 'past', now })(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({ tickets: ['2'] }))
    })
})
