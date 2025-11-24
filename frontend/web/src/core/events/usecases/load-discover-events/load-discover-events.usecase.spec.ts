import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, test } from "vitest";
import { loadDiscoverEvents } from "./load-discover-events.usecase";
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
    .soldOut()
    .withDescription('Description 1')
    .build()

const event2 = eventEntityBuilder()
    .withId('2')
    .withTitle('Event 2')
    .withCover("https://cover2.com")
    .withVenue(place)
    .withDescription('Description 2')
    .build()

describe('Load discover events', () => {
    test('Events is loaded', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.events = [
            {
                id: '1',
                title: 'Event 1',
                cover: "https://cover.com",
                likedCount: 0,
                venue: {
                    name: 'Bourse',
                    adresse: 'Place de la Bourse, 75007 Paris',
                    coordinates: { latitude: 48.8566, longitude: 2.3522 },
                },
                isSoldOut: true,
                lowestPrice: 0,
                startAt: new Date("2026-01-01").toISOString(),
                description: 'Description 1'
            },
            {
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
                description: 'Description 2'
            }
        ]
        const store = createTestStore({ eventsGateway })
        await store.dispatch(loadDiscoverEvents({ lat: 48.8566, lng: 2.3522 }))
        expect(store.getState()).toEqual(stateBuilder().withEvents([
            event1,
            event2
        ]).withDiscoveredEvents(['1', '2']).build())
    })

    test('Cached events are NOT deleted', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.events = [
            {
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
                description: 'Description 2'
            }
        ]
        const store = createTestStore({ eventsGateway }, stateBuilder().withEvents([
            event1
        ]).build())
        await store.dispatch(loadDiscoverEvents({ lat: 48.8566, lng: 2.3522 }))
        expect(store.getState()).toEqual(stateBuilder().withEvents([
            event1,
            event2
        ]).withDiscoveredEvents(['2']).build())
    })

    test('should set loadingDiscover to true when pending', async () => {
        const eventsGateway = new FakeEventsGateway()
        const store = createTestStore({ eventsGateway })

        const promise = store.dispatch(loadDiscoverEvents({ lat: 48.8566, lng: 2.3522 }))

        expect(store.getState()).toEqual(stateBuilder().withDiscoverLoading(true).build())

        await promise
    })

    test('should set loadingDiscover to false when fulfilled', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.events = []
        const store = createTestStore({ eventsGateway })

        await store.dispatch(loadDiscoverEvents({ lat: 48.8566, lng: 2.3522 }))

        expect(store.getState()).toEqual(stateBuilder().withDiscoverLoading(false).build())
    })

    test('should set loadingDiscover to false when rejected', async () => {
        const eventsGateway = new FakeEventsGateway()
        eventsGateway.getDiscoverEvents = async () => {
            throw new Error('Network error')
        }
        const store = createTestStore({ eventsGateway })

        await store.dispatch(loadDiscoverEvents({ lat: 48.8566, lng: 2.3522 }))

        expect(store.getState()).toEqual(stateBuilder().withDiscoverLoading(false).build())
    })
})
