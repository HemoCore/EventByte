import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { FakeAuthGateway } from "../../adapters/fake-auth.gateway";
import { logout } from "./logout.usecase";

describe('Feature: Logout', () => {
    test('Logout is requested', async () => {
        const authGateway = new FakeAuthGateway()
        const store = createTestStore({ authGateway })
        await store.dispatch(logout())
        expect(authGateway.logoutRequested).toBeTruthy()
    })
})
