import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { likeEvent } from "./like-event.usecase";
import { stateBuilder } from "@/core/state-builder";
import { FakeLikesGateway } from "../../adapters/fake-likes.gateway";

describe('Like event', () => {
    test('Event is liked optimisticaly', async () => {
        const store = createTestStore()
        store.dispatch(likeEvent('1'))
        expect(store.getState()).toEqual(stateBuilder().withLiked('1').build())
    })

    test('Event is liked on remote', async () => {
        const likesGateway = new FakeLikesGateway()
        const store = createTestStore({ likesGateway })
        await store.dispatch(likeEvent('1'))
        expect(likesGateway.likedEvents).toEqual(['1'])
    })

    test('Remove like optimistic on remote error', async () => {
        const likesGateway = new FakeLikesGateway()
        likesGateway.willLikeFail = true
        const store = createTestStore({ likesGateway })
        await store.dispatch(likeEvent('1'))
        expect(store.getState()).toEqual(stateBuilder().build())
    })
})
