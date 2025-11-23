import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, test } from "vitest";
import { cartItemEntityBuilder } from "../../entities/cart-item.entity";
import { addToCart } from "./add-ticket-to-cart";

describe('Feature: Add ticket to cart', () => {
    test('Ticket is added to cart', () => {
        const store = createTestStore();

        store.dispatch(addToCart("1"));

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([cartItemEntityBuilder().withId('1').withQuantity(1).build()]).build())
    })

    test('Ticket quantity is incremented when ticket already exists', () => {
        const ticket = cartItemEntityBuilder().withId('1').withQuantity(1)
        const store = createTestStore({}, stateBuilder().withCartTickets([ticket.build()]).build());

        store.dispatch(addToCart("1"));

        expect(store.getState()).toEqual(stateBuilder().withCartTickets([ticket.withQuantity(2).build()]).build())
    })
})
