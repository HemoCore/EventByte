import { createAppAsyncThunk } from "@/core/create-app-async-thunks"

export const login = createAppAsyncThunk("auth/login", async (_, { extra: { authGateway } }) => {
    authGateway.login();
})
