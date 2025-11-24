import { createAction, createSlice } from "@reduxjs/toolkit";
import { AuthUserEntity } from "./entities/auth-user.entity";
import { RootState } from "../create-store";

export const userAuthenticated = createAction<AuthUserEntity | undefined>(
    'auth/user-authenticated',
)

type AuthState = {
    isAuthChecked: boolean
    user?: AuthUserEntity
};

const initialState: AuthState = {
    isAuthChecked: false
};

export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(userAuthenticated, (state, action) => {
            state.isAuthChecked = true;
            state.user = action.payload;
        })
    }
});

export const authSelector = (state: RootState) => state.auth;
export const isCheckingAuthSelector = (state: RootState) => !authSelector(state).isAuthChecked;
export const isAuthCheckedSelector = (state: RootState) => authSelector(state).isAuthChecked;
export const isAuthSelector = (state: RootState) => isAuthCheckedSelector(state) && !!authSelector(state).user;
