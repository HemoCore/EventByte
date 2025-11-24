import { describe, expect, it } from "vitest";
import { createDiscoverPageViewmodel } from "./discover.viewmodel";
import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";

describe('DiscoverPageViewmodel', () => {
    it('Is in empty state when no events found', () => {
        const store = createTestStore()
        const viewmodel = createDiscoverPageViewmodel()(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({
            type: 'empty'
        }))
    })

    it('Is in with-events state when events found', () => {
        const store = createTestStore({}, stateBuilder().withDiscoveredEvents(["1", "2", "3"]).build());
        const viewmodel = createDiscoverPageViewmodel()(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({
            type: 'with-events'
        }))
    })

    it('Is in loading state when loading', () => {
        const store = createTestStore({}, stateBuilder().withDiscoverLoading(true).build());
        const viewmodel = createDiscoverPageViewmodel()(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({
            type: 'loading'
        }))
    })

    it('Events id is given when in with-events state', () => {
        const store = createTestStore({}, stateBuilder().withDiscoveredEvents(["1", "2", "3"]).build());
        const viewmodel = createDiscoverPageViewmodel()(store.getState())

        expect(viewmodel).toEqual(expect.objectContaining({
            events: ["1", "2", "3"]
        }))
    })
})
