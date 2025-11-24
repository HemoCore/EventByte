import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { unlikeEvent } from "./unlike-event.usecase";
import { stateBuilder } from "@/core/state-builder";
import { FakeLikesGateway } from "../../adapters/fake-likes.gateway";

describe('Like event', () => {
    test('Event is liked optimisticaly', async () => {
        const initialState = stateBuilder().withLiked('1').build()
        const store = createTestStore({}, initialState)
        store.dispatch(unlikeEvent('1'))
        expect(store.getState()).toEqual(stateBuilder().build())
    })

    test('Event is unliked on remote', async () => {
        const likesGateway = new FakeLikesGateway()
        const store = createTestStore({ likesGateway })
        await store.dispatch(unlikeEvent('1'))
        expect(likesGateway.likedEvents).toEqual([])
    })

    test('Remove unlike optimistic on remote error', async () => {
        const initialState = stateBuilder().withLiked('1').build()
        const likesGateway = new FakeLikesGateway()
        likesGateway.willUnlikeFail = true
        const store = createTestStore({ likesGateway }, initialState)
        await store.dispatch(unlikeEvent('1'))
        expect(store.getState()).toEqual(initialState)
    })
})
