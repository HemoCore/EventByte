import { Action, ActionCreatorWithPayload, AsyncThunk, configureStore, createListenerMiddleware, isAsyncThunkAction, Middleware, ThunkDispatch } from "@reduxjs/toolkit";
import { rootReducer } from "./root-reducer";
import { AuthGateway } from "./auth/ports/auth.gateway";
import { FakeAuthGateway } from "./auth/adapters/fake-auth.gateway";
import { EventsGateway } from "./events/ports/events.gateway";
import { FakeEventsGateway } from "./events/adapters/fake-events.gateway";
import { LikesGateway } from "./events/ports/likes.gateway";
import { FakeLikesGateway } from "./events/adapters/fake-likes.gateway";
import { TicketsGateway } from "./tickets/ports/tickets.gateway";
import { FakeTicketsGateway } from "./tickets/adapters/fake-tickets.gateway";
import { createOnAuthStateChangeListener } from "./auth/listeners/on-auth-state-change/on-auth-state-change.listener";
import { onUserAuthenticated } from "./events/listeners/on-user-authenticated/on-user-authenticated.listener";
import { BookingGateway } from "./booking/ports/booking.gateway";
import { FakeBookingGateway } from "./booking/adapters/fake-booking.gateway";
import { PaymentGateway } from "./booking/ports/payment.gateway";
import { FakePaymentGateway } from "./booking/adapters/fake-payment.gateway";

export const EMPTY_ARGS = 'EMPTY_ARGS' as const;

export type Dependencies = {
    authGateway: AuthGateway,
    eventsGateway: EventsGateway,
    likesGateway: LikesGateway,
    ticketsGateway: TicketsGateway,
    bookingGateway: BookingGateway,
    paymentGateway: PaymentGateway
};

const createAppListenerMiddleware = (dependencies: Dependencies) => createListenerMiddleware<RootState, ThunkDispatch<RootState, Dependencies, Action>, Dependencies>({
    extra: dependencies
});

export const createStore = (dependencies: Dependencies, preloadedState: Partial<RootState> = {}, middleware: Middleware[] = [],) => {

    const store = configureStore({
        reducer: rootReducer,
        preloadedState,
        middleware: (getDefaultMiddleware) => getDefaultMiddleware({
            thunk: {
                extraArgument: dependencies,
            },
        }).concat(middleware).concat([
            onUserAuthenticated(createAppListenerMiddleware(dependencies))
        ]),
    })

    createOnAuthStateChangeListener(dependencies.authGateway, store.dispatch)()

    return store;
}

export const createTestStore = ({
    authGateway = new FakeAuthGateway(),
    eventsGateway = new FakeEventsGateway(),
    likesGateway = new FakeLikesGateway(),
    ticketsGateway = new FakeTicketsGateway(),
    bookingGateway = new FakeBookingGateway(),
    paymentGateway = new FakePaymentGateway(),
}: Partial<Dependencies> = {},
    preloadedState: Partial<RootState> = {}) => {

    const actions: Action<any>[] = [];

    const logActionsMiddleware: Middleware = () => (next) => (action) => {
        actions.push(action as Action<any>);
        return next(action);
    };

    const getAction = () => actions;

    const getDispatchedUseCaseArgs = (useCase: AsyncThunk<any, any, any>) => {
        const pendingUseCaseAction = getAction().find(
            (a) => a.type == useCase.pending.toString(),
        );

        if (!pendingUseCaseAction) return;

        if (!isAsyncThunkAction(pendingUseCaseAction)) return;

        return pendingUseCaseAction.meta.arg ?? EMPTY_ARGS;
    };

    const getDispatchedAction = (action: ActionCreatorWithPayload<any>) => {
        const useCaseAction: any = getAction().find(
            (a) => a.type == action.toString(),
        )

        return useCaseAction?.payload ?? EMPTY_ARGS;
    };

    const store = createStore({
        authGateway,
        eventsGateway,
        likesGateway,
        ticketsGateway,
        bookingGateway,
        paymentGateway
    }, preloadedState, [logActionsMiddleware]);

    return {
        ...store,
        getAction,
        getDispatchedUseCaseArgs,
        getDispatchedAction
    };
}

export type AppStore = ReturnType<typeof createStore>
export type AppDispatch = ThunkDispatch<RootState, Dependencies, Action<any>>;
export type RootState = ReturnType<typeof rootReducer>;
