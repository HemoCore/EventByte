import { createRoute } from "@tanstack/react-router"
import { rootRoute } from "@/router"
import { TicketsLayout } from "./tickets.layout"
import { TicketsPage } from "./tickets.page"
import { TicketDetailsPage } from "./ticket-details.page"
import { loadUserBookings } from "@/core/booking/usecases/load-user-bookings/load-user-bookings"
import { loadBookingById } from "@/core/booking/usecases/load-booking-by-id/load-booking-by-id"

const ticketsLayoutRoute = createRoute({
    getParentRoute: () => rootRoute,
    id: "tickets",
    component: TicketsLayout,
})

const ticketsListRoute = createRoute({
    getParentRoute: () => ticketsLayoutRoute,
    path: "/tickets",
    loader(ctx) {
        ctx.context.dispatch(loadUserBookings())
    },
    component: TicketsPage,
})

const ticketDetailsRoute = createRoute({
    getParentRoute: () => ticketsLayoutRoute,
    path: "/tickets/$id",
    loader: ({ params, context }) => {
        context.dispatch(loadBookingById(params.id))
    },
    component: TicketDetailsPage,
})

export const ticketsRoutes = ticketsLayoutRoute.addChildren([
    ticketsListRoute,
    ticketDetailsRoute,
])
