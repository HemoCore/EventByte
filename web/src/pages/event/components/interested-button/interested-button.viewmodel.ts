import { AppDispatch, RootState } from "@/core/create-store"
import { selectIsEventLiked, selectIsLikedEventsLoading } from "@/core/events/events.slice"
import { likeEvent } from "@/core/events/usecases/like-event/like-event.usecase"
import { unlikeEvent } from "@/core/events/usecases/unlike-event/unlike-event.usecase"
import { createSelector } from "@reduxjs/toolkit"

type InterestedButtonViewModel = {
    isLoading: boolean
    isLiked: boolean
    toggleLike: () => void
}

type Params = {
    eventId: string,
    dispatch: AppDispatch
}

export const selectInterestedButtonViewModel = ({ eventId, dispatch }: Params): (state: RootState) => InterestedButtonViewModel => createSelector([selectIsLikedEventsLoading, selectIsEventLiked(eventId)], (isLoading, isLiked) => {
    return {
        isLoading,
        isLiked,
        toggleLike: () => {
            if (isLiked) {
                return dispatch(unlikeEvent(eventId));
            }
            dispatch(likeEvent(eventId));
        }
    }
})
