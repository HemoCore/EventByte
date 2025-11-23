import { createSelector, createSlice } from "@reduxjs/toolkit";
import { eventsAdapter } from "./entities/event.entity";
import { RootState } from "../create-store";
import { loadDiscoverEvents } from "./usecases/load-discover-events/load-discover-events.usecase";
import { likeEvent } from "./usecases/like-event/like-event.usecase";
import { unlikeEvent } from "./usecases/unlike-event/unlike-event.usecase";
import { loadLikedEvents } from "./usecases/load-liked-events/load-liked-events.usecase";
import { loadEventById } from "./usecases/load-event-by-id/load-event-by-id.usecase";
import { loadEventsByIds } from "./usecases/load-events-by-ids/load-events-by-ids.usecase";
import { loadUserBookings } from "../booking/usecases/load-user-bookings/load-user-bookings";
import { loadBookingById } from "../booking/usecases/load-booking-by-id/load-booking-by-id";
import { searchEvents } from "../search/usecases/search-events/search-events.usecase";

const initialState = eventsAdapter.getInitialState<{
    liked: Record<string, boolean>,
    discovered: string[],
    loadingDiscover: boolean,
    loadingEvents: Record<string, boolean>,
    loadingLikedEvents: boolean
}>({
    liked: {},
    discovered: [],
    loadingDiscover: false,
    loadingEvents: {},
    loadingLikedEvents: false
});

export type EventsState = ReturnType<typeof eventsAdapter.getInitialState>;

export const eventsSlice = createSlice({
    name: 'events',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(loadDiscoverEvents.pending, (state) => {
            state.loadingDiscover = true
        }).addCase(loadDiscoverEvents.fulfilled, (state, action) => {
            eventsAdapter.setMany(state, action.payload);
            state.discovered = action.payload.map(event => event.id)
            state.loadingDiscover = false
        }).addCase(loadDiscoverEvents.rejected, (state) => {
            state.loadingDiscover = false
        }).addCase(loadLikedEvents.fulfilled, (state, action) => {
            state.loadingLikedEvents = false
            eventsAdapter.setMany(state, action.payload);
            state.liked = action.payload.reduce((acc, event) => {
                acc[event.id] = true;
                return acc
            }, {} as Record<string, boolean>)
        }).addCase(likeEvent.pending, (state, action) => {
            state.liked[action.meta.arg] = true
        }).addCase(likeEvent.rejected, (state, action) => {
            delete state.liked[action.meta.arg]
        }).addCase(unlikeEvent.pending, (state, action) => {
            delete state.liked[action.meta.arg]
        }).addCase(unlikeEvent.rejected, (state, action) => {
            state.liked[action.meta.arg] = true
        }).addCase(loadEventById.pending, (state, action) => {
            state.loadingEvents[action.meta.arg.id] = true
        }).addCase(loadEventById.fulfilled, (state, action) => {
            eventsAdapter.setOne(state, action.payload)
            delete state.loadingEvents[action.meta.arg.id]
        }).addCase(loadEventById.rejected, (state, action) => {
            delete state.loadingEvents[action.meta.arg.id]
        }).addCase(loadEventsByIds.fulfilled, (state, action) => {
            eventsAdapter.setMany(state, action.payload)
        }).addCase(loadUserBookings.fulfilled, (state, action) => {
            eventsAdapter.setMany(state, action.payload.events)
        }).addCase(loadBookingById.fulfilled, (state, action) => {
            eventsAdapter.setOne(state, action.payload.event)
        }).addCase(searchEvents.fulfilled, (state, action) => {
            eventsAdapter.setMany(state, action.payload)
        }).addCase(loadLikedEvents.pending, (state) => {
            state.loadingLikedEvents = true
        }).addCase(loadLikedEvents.rejected, (state) => {
            state.loadingLikedEvents = false
        })
    }
})

export const eventsSelectors = eventsAdapter.getSelectors<RootState>((state) => state.events);

export const selectLikedEvents = createSelector(
    (state: RootState) => state.events.liked,
    (liked) => Object.keys(liked)
);

export const selectIsEventLiked = (eventId: string) => createSelector(
    (state: RootState) => state.events.liked,
    (liked) => !!liked[eventId]
);

export const selectIsLikedEventsLoading = (state: RootState) => state.events.loadingLikedEvents;

export const selectEventById = (eventId: string) => (state: RootState) => eventsSelectors.selectById(state, eventId);

export const selectDiscoverEvents = (state: RootState) => state.events.discovered;

export const selectLoadingDiscover = (state: RootState) => state.events.loadingDiscover;

export const selectIsDiscoverLoading = (state: RootState) => state.events.loadingDiscover;

export const selectIsEventLoading = (eventId: string) => (state: RootState) => !!state.events.loadingEvents[eventId];
