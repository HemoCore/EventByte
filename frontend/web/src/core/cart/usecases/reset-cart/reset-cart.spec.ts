import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, test } from "vitest";
import { cartItemEntityBuilder } from "../../entities/cart-item.entity";
import { resetCart } from "./reset-cart";

describe('Feature: Reset cart', () => {
    test('Cart tickets is empty', () => {
        const store = createTestStore({}, stateBuilder().withCartTickets([
            cartItemEntityBuilder().withId('1').withQuantity(1).build(),
            cartItemEntityBuilder().withId('2').withQuantity(2).build(),
        ]).build());

        store.dispatch(resetCart());

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([]).build())
    })

    test('Promo code is empty', () => {
        const store = createTestStore({}, stateBuilder().withCartPromoCode('code').build());

        store.dispatch(resetCart());

        expect(store.getState()).toEqual(stateBuilder().withCartPromoCode(undefined).build())
    })
})
