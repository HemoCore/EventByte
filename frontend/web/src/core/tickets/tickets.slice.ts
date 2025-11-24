import { createSelector, createSlice } from "@reduxjs/toolkit"
import { RootState } from "../create-store"
import { ticketsEntityAdapter } from "./entities/ticket.entity"
import { loadEventTickets } from "./usecases/load-event-tickets/load-event-tickets.usecase"

type TicketsState = {
    loading: Record<string, boolean>
} & ReturnType<typeof ticketsEntityAdapter.getInitialState>

const initialState: TicketsState = ticketsEntityAdapter.getInitialState({
    loading: {}
})


export const ticketsSlice = createSlice({
    name: "tickets",
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(loadEventTickets.pending, (state, action) => {
                state.loading[action.meta.arg.id] = true
            })
            .addCase(loadEventTickets.fulfilled, (state, action) => {
                const { id: eventId } = action.meta.arg
                ticketsEntityAdapter.upsertMany(state, action.payload)
                delete state.loading[eventId]
            })
            .addCase(loadEventTickets.rejected, (state, action) => {
                delete state.loading[action.meta.arg.id]
            })
    }
})

export const ticketsSelectors = ticketsEntityAdapter.getSelectors<RootState>((state) => state.tickets);

export const selectEventTickets = (eventId: string) => createSelector(
    (state: RootState) => ticketsSelectors.selectAll(state),
    (tickets) => tickets.filter(ticket => ticket.eventId === eventId)
)

export const selectAllTickets = (state: RootState) => ticketsSelectors.selectAll(state)

export const selectIsLoadingEventTickets = (eventId: string) => (state: RootState) =>
    !!state.tickets.loading[eventId]

export const selectEventTicketsIds = (eventId: string) => createSelector(
    (state: RootState) => ticketsSelectors.selectAll(state),
    (tickets) => tickets.filter(ticket => ticket.eventId === eventId).map(ticket => ticket.id)
)

export const selectTicketById = (ticketId: string) => createSelector(
    (state: RootState) => ticketsSelectors.selectAll(state),
    (tickets) => tickets.find(ticket => ticket.id === ticketId)
)
