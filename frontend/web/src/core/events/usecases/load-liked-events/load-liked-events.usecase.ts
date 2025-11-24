import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const loadLikedEvents = createAppAsyncThunk('events/load-liked', async (_, { extra: { eventsGateway } }) => {
    return eventsGateway.getLikedEvents();
})
