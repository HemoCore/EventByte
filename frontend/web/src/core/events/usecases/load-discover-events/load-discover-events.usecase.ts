import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

type LoadDiscoverEventsParams = {
    lat: number
    lng: number
}

export const loadDiscoverEvents = createAppAsyncThunk(
    "events/load-discover",
    async (params: LoadDiscoverEventsParams, { extra: { eventsGateway } }) => {
        return eventsGateway.getDiscoverEvents(params)
    }
)
