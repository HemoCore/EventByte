import { createEntityAdapter } from "@reduxjs/toolkit"

export type CartItemEntity = {
    id: string
    quantity: number
}

export const cartItemEntityAdapter = createEntityAdapter<CartItemEntity>()

export const cartItemEntityBuilder = (item: CartItemEntity = {
    id: '1',
    quantity: 1
}) => ({
    withId: (id: string) => cartItemEntityBuilder({ ...item, id }),
    withQuantity: (quantity: number) => cartItemEntityBuilder({ ...item, quantity }),
    build: () => item
})
