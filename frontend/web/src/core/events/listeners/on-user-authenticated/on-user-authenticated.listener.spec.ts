import { userAuthenticated } from "@/core/auth/auth.slice";
import { authUserEntityBuilder } from "@/core/auth/entities/auth-user.entity";
import { createTestStore, EMPTY_ARGS } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { loadLikedEvents } from "../../usecases/load-liked-events/load-liked-events.usecase";

describe('Feature: Load liked events on user authenticated', () => {
    test('Example: Load liked events', async () => {
        const store = createTestStore()
        store.dispatch(userAuthenticated(authUserEntityBuilder().withId('1').build()))
        expect(store.getDispatchedUseCaseArgs(loadLikedEvents)).toEqual(EMPTY_ARGS)
    })

    test('Example: User is not auth liked events is NOT loaded', async () => {
        const store = createTestStore()
        store.dispatch(userAuthenticated())
        expect(store.getDispatchedUseCaseArgs(loadLikedEvents)).toEqual(undefined)
    })
})
