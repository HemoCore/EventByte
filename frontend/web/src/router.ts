import { createRootRouteWithContext, createRouter as tanstackCreateRouter } from '@tanstack/react-router'
import { NotFoundPage } from './pages/not-found.page'
import { AppDispatch } from './core/create-store'
import { discoverRoutes } from './pages/discover/discover.route'
import { eventRoutes } from './pages/event/event.route'
import { searchRoutes } from './pages/search/search.route'
import { bookingRoutes } from './pages/booking/booking.route'
import { ticketsRoutes } from './pages/tickets/tickets.route'

type Context = {
    dispatch: AppDispatch,
}

export type Router = ReturnType<typeof createRouter>

export const rootRoute = createRootRouteWithContext<Context>()({
    notFoundComponent: NotFoundPage
})

export const createRouter = (context: Context) =>
    tanstackCreateRouter({
        routeTree: rootRoute.addChildren([
            discoverRoutes,
            eventRoutes,
            searchRoutes,
            bookingRoutes,
            ticketsRoutes
        ]),
        context,
        defaultViewTransition: true
    })

declare module '@tanstack/react-router' {
    interface Register {
        router: Router
    }
}
