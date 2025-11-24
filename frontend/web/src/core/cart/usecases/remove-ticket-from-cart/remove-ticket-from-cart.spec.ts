import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, test } from "vitest";
import { cartItemEntityBuilder } from "../../entities/cart-item.entity";
import { removeFromCart } from "./remove-ticket-from-cart";

describe('Feature: Remove ticket from cart', () => {
    test('Ticket is removed from cart', () => {
        const store = createTestStore({}, stateBuilder().withCartTickets([
            cartItemEntityBuilder().withId('1').withQuantity(1).build(),
        ]).build());

        store.dispatch(removeFromCart("1"));

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([]).build())
    })

    test('Ticket quantity is decremented when ticket have more than one quantity', () => {
        const ticket = cartItemEntityBuilder().withId('1').withQuantity(2)
        const store = createTestStore({}, stateBuilder().withCartTickets([
            ticket.build(),
        ]).build());

        store.dispatch(removeFromCart("1"));

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([ticket.withQuantity(1).build()]).build())
    })

    test('Does nothing when ticket does not exist', () => {
        const store = createTestStore({}, stateBuilder().withCartTickets([]).build());

        store.dispatch(removeFromCart("1"));

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([]).build())
    })
})
