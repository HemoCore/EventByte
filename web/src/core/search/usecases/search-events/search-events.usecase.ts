import { createAppAsyncThunk } from "@/core/create-app-async-thunks";
import { selectSearchLocation, selectSearchQuery, selectSearchRadius } from "../../search.slice";

export const searchEvents = createAppAsyncThunk(
    "search/searchEvents",
    async (_, { extra: { eventsGateway }, getState }) => {
        const state = getState();
        const location = selectSearchLocation(state);
        const radius = selectSearchRadius(state);
        const query = selectSearchQuery(state);

        return eventsGateway.searchEvents({
            lat: location.lat,
            lng: location.lng,
            radius,
            query,
        });
    }
);
