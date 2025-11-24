import { RootState } from "@/core/create-store";
import { selectDiscoverEvents, selectIsDiscoverLoading } from "@/core/events/events.slice";
import { createSelector } from "@reduxjs/toolkit";

type EmptyState = {
    type: 'empty'
}

type WithEventsState = {
    type: 'with-events',
    events: string[]
}

type LoadingState = {
    type: 'loading'
}

type DiscoverPageViewmodel = EmptyState | WithEventsState | LoadingState

export const createDiscoverPageViewmodel = (): (state: RootState) => DiscoverPageViewmodel => createSelector([selectDiscoverEvents, selectIsDiscoverLoading], (eventIds, isLoading) => {
    if (isLoading) {
        return {
            type: 'loading'
        }
    }

    const isEmpty = eventIds.length === 0
    if (isEmpty) {
        return {
            type: 'empty'
        }
    }

    return {
        type: 'with-events',
        events: eventIds
    }
}) 
