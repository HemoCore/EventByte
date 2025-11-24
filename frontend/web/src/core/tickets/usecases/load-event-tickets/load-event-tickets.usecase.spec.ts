import { describe, expect, it } from "vitest"
import { createTestStore } from "@/core/create-store"
import { FakeTicketsGateway } from "@/core/tickets/adapters/fake-tickets.gateway"
import { ticketEntityBuilder } from "@/core/tickets/entities/ticket.entity"
import { loadEventTickets } from "./load-event-tickets.usecase"
import { stateBuilder } from "@/core/state-builder"

describe('Feature: Load event tickets', () => {
    it('should load tickets for an event', async () => {
        const tickets = [
            ticketEntityBuilder().withId('early-bird').withName('Early Bird').withPrice(13.99).withQuantity(3).build(),
            ticketEntityBuilder().withId('regular').withName('Regular').withPrice(15.99).withQuantity(5).build(),
            ticketEntityBuilder().withId('last-chance').withName('Last Chance').withPrice(20.99).withQuantity(2).build(),
        ]

        const ticketsGateway = new FakeTicketsGateway()
        ticketsGateway.setTickets(tickets)

        const store = createTestStore({ ticketsGateway })

        await store.dispatch(loadEventTickets({ id: '1' }))

        expect(store.getState().tickets).toEqual(stateBuilder().withTickets(tickets).build().tickets)
    })

    it('is in loading state while loading tickets', async () => {
        const ticketsGateway = new FakeTicketsGateway(100)
        ticketsGateway.setTickets([])

        const store = createTestStore({ ticketsGateway })

        const promise = store.dispatch(loadEventTickets({ id: '1' }))

        expect(store.getState().tickets).toEqual(stateBuilder().withLoadingTicketsForEvent('1').build().tickets)

        await promise

    })

    it('is NOT in loading state while error loading tickets', async () => {
        const ticketsGateway = new FakeTicketsGateway()
        ticketsGateway.willFail = true

        const store = createTestStore({ ticketsGateway })

        await store.dispatch(loadEventTickets({ id: '1' }))

        expect(store.getState().tickets).toEqual(stateBuilder().build().tickets)
    })
})
