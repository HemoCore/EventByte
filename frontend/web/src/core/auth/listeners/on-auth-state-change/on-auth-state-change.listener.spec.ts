import { describe, expect, test } from "vitest";
import { FakeAuthGateway } from "../../adapters/fake-auth.gateway";
import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { authUserEntityBuilder } from "../../entities/auth-user.entity";

describe('Feature: On auth state change', () => {
    test('Example: John is authenticated', () => {
        const authGateway = new FakeAuthGateway()
        const store = createTestStore({ authGateway })
        authGateway.simulateAuthStateChange({ id: '1', email: '0xM0X@example.com', fullname: 'John Doe' })
        expect(store.getState()).toEqual(stateBuilder().withAuthChecked().withAuthUser(
            authUserEntityBuilder()
                .withId('1')
                .withEmail('0xM0X@example.com')
                .withFullname('John Doe')
                .build()
        ).withLikedEventsLoading(true).build())
    })

    test('Example: authentication has not already happened', () => {
        const store = createTestStore()
        expect(store.getState()).toEqual(stateBuilder()
            .build())
    })
})
