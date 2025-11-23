import { createRoute } from '@tanstack/react-router'
import { EventPage } from './event.page'
import { loadEventById } from '@/core/events/usecases/load-event-by-id/load-event-by-id.usecase'
import { rootRoute } from '@/router'
import { EventLayout } from './event.layout'
import { loadEventTickets } from '@/core/tickets/usecases/load-event-tickets/load-event-tickets.usecase'
import { resetCart } from '@/core/cart/usecases/reset-cart/reset-cart'

const eventLayoutRoute = createRoute({
    getParentRoute: () => rootRoute,
    id: 'event',
    component: EventLayout
})

const eventRoute = createRoute({
    getParentRoute: () => eventLayoutRoute,
    path: '/event/$id',
    loader(ctx) {
        const { id } = ctx.params
        ctx.context.dispatch(loadEventById({ id }))
        ctx.context.dispatch(loadEventTickets({ id }))
    },
    onLeave: (ctx) => {
        ctx.context.dispatch(resetCart())
    },
    component: EventPage,
})

export const eventRoutes = eventLayoutRoute.addChildren([
    eventRoute
])
