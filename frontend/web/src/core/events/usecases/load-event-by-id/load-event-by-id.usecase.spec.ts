import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, test } from "vitest";
import { loadEventById } from "./load-event-by-id.usecase";
import { FakeEventsGateway } from "../../adapters/fake-events.gateway";
import { eventEntityBuilder, venueEntityBuilder } from "../../entities/event.entity";

const place = venueEntityBuilder()
    .withName('Bourse')
    .withAdresse('Place de la Bourse, 75007 Paris')
    .withCoordinates({ latitude: 48.8566, longitude: 2.3522 })
    .build()

const event1 = eventEntityBuilder()
    .withId('1')
    .withTitle('Event 1')
    .withCover("https://cover.com")
    .withVenue(place)
    .withDescription('Description')
    .build()

describe('Load event by id', () => {
    test('Event is loaded', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.events = [{
            id: '1',
            title: 'Event 1',
            cover: "https://cover.com",
            likedCount: 0,
            venue: {
                name: 'Bourse',
                adresse: 'Place de la Bourse, 75007 Paris',
                coordinates: { latitude: 48.8566, longitude: 2.3522 },
            },
            isSoldOut: false,
            lowestPrice: 0,
            startAt: new Date("2026-01-01").toISOString(),
            description: 'Description'
        }]
        const store = createTestStore({ eventsGateway })
        await store.dispatch(loadEventById({ id: '1' }))
        expect(store.getState()).toEqual(stateBuilder().withEvents([event1]).build())
    })

    test('Cached event is NOT deleted', async () => {
        const eventsGateway = new FakeEventsGateway()
        const event2 = eventEntityBuilder()
            .withId('2')
            .withTitle('Event 2')
            .withCover("https://cover2.com")
            .withVenue(place)
            .build()

        eventsGateway.events = [{
            id: '2',
            title: 'Event 2',
            cover: "https://cover2.com",
            likedCount: 0,
            venue: {
                name: 'Bourse',
                adresse: 'Place de la Bourse, 75007 Paris',
                coordinates: { latitude: 48.8566, longitude: 2.3522 },
            },
            isSoldOut: false,
            lowestPrice: 0,
            startAt: new Date("2026-01-01").toISOString(),
            description: 'Description'
        }]
        const store = createTestStore({ eventsGateway }, stateBuilder().withEvents([event1]).build())
        await store.dispatch(loadEventById({ id: '2' }))
        expect(store.getState()).toEqual(stateBuilder().withEvents([event1, event2]).build())
    })

    test('Event is loading when pending', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.events = [{
            id: '1',
            title: 'Event 1',
            cover: "https://cover.com",
            likedCount: 0,
            venue: {
                name: 'Bourse',
                adresse: 'Place de la Bourse, 75007 Paris',
                coordinates: { latitude: 48.8566, longitude: 2.3522 },
            },
            isSoldOut: false,
            lowestPrice: 0,
            startAt: new Date("2026-01-01").toISOString(),
            description: 'Description'
        }]
        const store = createTestStore({ eventsGateway })

        const promise = store.dispatch(loadEventById({ id: '1' }))

        expect(store.getState()).toEqual(stateBuilder().withEventLoading('1').build())

        await promise
    })

    test('Event is NOT loading when error', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.getEventById = async () => {
            throw new Error('Network error')
        }
        const store = createTestStore({ eventsGateway })

        await store.dispatch(loadEventById({ id: '1' }))

        expect(store.getState().events.loadingEvents['1']).toBeUndefined()
    })
})
