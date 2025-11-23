import { createAppAsyncThunk } from "@/core/create-app-async-thunks";

export const loadEventsByIds = createAppAsyncThunk(
	'events/loadByIds',
	async (ids: string[], { extra: { eventsGateway } }) => {
		return await eventsGateway.getEventsByIds(ids)
	}
)
