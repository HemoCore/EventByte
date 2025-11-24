import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

type LoadEventByIdParams = {
    id: string
}

export const loadEventById = createAppAsyncThunk(
    "events/load-event-by-id",
    async (params: LoadEventByIdParams, { extra: { eventsGateway } }) => {
        return eventsGateway.getEventById(params.id)
    }
)
