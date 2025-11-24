import { createTestStore } from "@/core/create-store";
import { describe, expect, test } from "vitest";
import { FakeAuthGateway } from "../../adapters/fake-auth.gateway";
import { login } from "./login.usecase";

describe('Feature: Login', () => {
    test('Login is requested', async () => {
        const authGateway = new FakeAuthGateway()
        const store = createTestStore({ authGateway })
        await store.dispatch(login())
        expect(authGateway.loginRequested).toBeTruthy()
    })
})
