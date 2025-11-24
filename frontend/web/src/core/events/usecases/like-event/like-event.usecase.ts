import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const likeEvent = createAppAsyncThunk("events/like", async (eventId: string, { extra: { likesGateway } }) => {
    await likesGateway.likeEvent(eventId)
})
