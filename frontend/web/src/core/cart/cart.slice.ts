import { createSelector, createSlice } from "@reduxjs/toolkit";
import { cartItemEntityAdapter } from "./entities/cart-item.entity";
import { resetCart } from "./usecases/reset-cart/reset-cart";
import { addToCart } from "./usecases/add-ticket-to-cart/add-ticket-to-cart";
import { removeFromCart } from "./usecases/remove-ticket-from-cart/remove-ticket-from-cart";
import { RootState } from "../create-store";

type CartState = {
    promocode?: string
} &
    ReturnType<typeof cartItemEntityAdapter.getInitialState>

const initialState: CartState = cartItemEntityAdapter.getInitialState()

export const cartSlice = createSlice({
    name: 'cart',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(resetCart, (state) => {
            cartItemEntityAdapter.removeAll(state)
            state.promocode = undefined
        }).addCase(addToCart, (state, action) => {
            const item = cartItemEntitySelectors.selectById(state, action.payload)

            cartItemEntityAdapter.upsertOne(state, {
                id: action.payload,
                quantity: item ? item.quantity + 1 : 1
            })
        }).addCase(removeFromCart, (state, action) => {
            const item = cartItemEntitySelectors.selectById(state, action.payload)
            if (!item) {
                return
            }

            if (item.quantity === 1) {
                cartItemEntityAdapter.removeOne(state, action.payload)
                return
            }

            cartItemEntityAdapter.updateOne(state, {
                id: action.payload,
                changes: {
                    quantity: item.quantity - 1
                }
            })
        })
    }
})

export const cartItemEntitySelectors = cartItemEntityAdapter.getSelectors()

export const selectCartItemQuantity = (id: string) => createSelector(
    (state: RootState) => cartItemEntitySelectors.selectById(state.cart, id),
    (item) => item ? item.quantity : 0
)

export const selectCartIsEmpty = (state: RootState) => cartItemEntitySelectors.selectAll(state.cart).length === 0

export const selectCartItems = (state: RootState) => cartItemEntitySelectors.selectAll(state.cart)
