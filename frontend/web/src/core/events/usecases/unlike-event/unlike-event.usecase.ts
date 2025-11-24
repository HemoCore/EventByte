import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const unlikeEvent = createAppAsyncThunk("events/unlike", async (eventId: string, { extra: { likesGateway } }) => {
    await likesGateway.unlikeEvent(eventId)
})
