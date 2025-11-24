import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { FakeEventsGateway } from "../../adapters/fake-events.gateway";
import { stateBuilder } from "@/core/state-builder";
import { eventEntityBuilder } from "../../entities/event.entity";
import { loadLikedEvents } from "./load-liked-events.usecase";

describe('Feature: Load Liked Events', () => {
    test('Example: One event is liked', async () => {
        const eventsGateway = new FakeEventsGateway();
        const event = eventEntityBuilder().withId('1').build();
        eventsGateway.events = [event];
        eventsGateway.likedEvents = [event.id];
        const store = createTestStore({ eventsGateway });
        await store.dispatch(loadLikedEvents());
        expect(store.getState()).toEqual(stateBuilder().withEvents([
            event
        ]).withLiked('1').build());
    })

    test('Example: Multiple event is liked', async () => {
        const eventsGateway = new FakeEventsGateway();
        const events = [eventEntityBuilder().withId('1').build(), eventEntityBuilder().withId('2').build()];
        eventsGateway.events = events
        eventsGateway.likedEvents = events.map(event => event.id);
        const store = createTestStore({ eventsGateway });
        await store.dispatch(loadLikedEvents());
        expect(store.getState()).toEqual(stateBuilder().withEvents(
            events
        ).withLiked('1').withLiked('2').build());
    })

    test('Example: old cache is deleted', async () => {
        const initialState = stateBuilder().withLiked('2').build();
        const eventsGateway = new FakeEventsGateway();
        const event = eventEntityBuilder().withId('1').build();
        eventsGateway.events = [event];
        eventsGateway.likedEvents = [event.id];
        const store = createTestStore({ eventsGateway }, initialState);
        await store.dispatch(loadLikedEvents());
        expect(store.getState()).toEqual(stateBuilder().withEvents([
            event
        ]).withLiked('1').build());
    })

    test('Example: liked events are loading', async () => {
        const store = createTestStore();
        store.dispatch(loadLikedEvents());

        expect(store.getState()).toEqual(stateBuilder().withLikedEventsLoading(true).build());
    })

    test('Example: liked events are NOT loading on failure', async () => {
        const eventsGateway = new FakeEventsGateway();
        eventsGateway.willFail = true;
        const store = createTestStore({ eventsGateway });
        await store.dispatch(loadLikedEvents());

        expect(store.getState()).toEqual(stateBuilder().withLikedEventsLoading(false).build());
    })
});
