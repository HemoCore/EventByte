import { createAppAsyncThunk } from "@/core/create-app-async-thunks";
import { selectCartItems } from "@/core/cart/cart.slice";

export const bookCart = createAppAsyncThunk('booking/bookCart', async (_, { extra: { bookingGateway }, getState }) => {
    const state = getState()
    const cartItems = selectCartItems(state)

    const bookParams = cartItems.map(item => ({
        id: item.id,
        quantity: item.quantity
    }))

    return bookingGateway.book(bookParams)
})
