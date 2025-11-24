import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, it } from "vitest";
import { selectInterestedButtonViewModel } from "./interested-button.viewmodel";
import { likeEvent } from "@/core/events/usecases/like-event/like-event.usecase";
import { unlikeEvent } from "@/core/events/usecases/unlike-event/unlike-event.usecase";

describe("Interested Button Viewmodel", () => {
    it('Is loading when loading likes', () => {
        const store = createTestStore({}, stateBuilder().withLikedEventsLoading(true).build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        expect(viewmodel).toEqual(expect.objectContaining({
            isLoading: true
        }))
    })

    it('Is NOT loading when NOT loading likes', () => {
        const store = createTestStore({}, stateBuilder().withLikedEventsLoading(false).build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        expect(viewmodel).toEqual(expect.objectContaining({
            isLoading: false
        }))
    })

    it('Is liked when event is liked by user', () => {
        const store = createTestStore({}, stateBuilder().withLiked('1').build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        expect(viewmodel).toEqual(expect.objectContaining({
            isLiked: true
        }))
    })

    it('Is NOT liked when event is NOT liked by user', () => {
        const store = createTestStore({}, stateBuilder().build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        expect(viewmodel).toEqual(expect.objectContaining({
            isLiked: false
        }))
    })

    it('toggle call like event when not liked', () => {
        const store = createTestStore({}, stateBuilder().build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        viewmodel.toggleLike();
        expect(store.getDispatchedUseCaseArgs(likeEvent)).toEqual('1');
    })

    it('toggle call unlike event when liked', () => {
        const store = createTestStore({}, stateBuilder().withLiked('1').build());
        const viewmodel = selectInterestedButtonViewModel({ eventId: '1', dispatch: store.dispatch })(store.getState());
        viewmodel.toggleLike();
        expect(store.getDispatchedUseCaseArgs(unlikeEvent)).toEqual('1');
    })
});
