import { createAction } from "@reduxjs/toolkit";

export const removeFromCart = createAction<string>('cart/remove');
