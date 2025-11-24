import { createAppAsyncThunk } from "@/core/create-app-async-thunks"

type LoadEventTicketsParams = {
    id: string
}

export const loadEventTickets = createAppAsyncThunk(
    "tickets/load-event-tickets",
    async (params: LoadEventTicketsParams, { extra: { ticketsGateway } }) => {
        return ticketsGateway.getEventTickets(params.id)
    }
)
