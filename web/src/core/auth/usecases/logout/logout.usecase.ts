import { createAppAsyncThunk } from "@/core/create-app-async-thunks"

export const logout = createAppAsyncThunk("auth/logout", async (_, { extra: { authGateway } }) => {
    authGateway.logout();
})
