import { createAction } from "@reduxjs/toolkit";

export const addToCart = createAction<string>('cart/add');
