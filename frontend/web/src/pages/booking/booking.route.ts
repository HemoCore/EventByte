import { createRoute } from "@tanstack/react-router";
import { BookingPage } from "./booking.page";
import { rootRoute } from "@/router";
import { BookingLayout } from "./booking.layout";
import { loadBookingById } from "@/core/booking/usecases/load-booking-by-id/load-booking-by-id";

const bookingLayoutRoute = createRoute({
    getParentRoute: () => rootRoute,
    id: "booking",
    component: BookingLayout
})

const bookingRoute = createRoute({
    getParentRoute: () => bookingLayoutRoute,
    path: "/booking/$id",
    loader(ctx) {
        const { id } = ctx.params
        ctx.context.dispatch(loadBookingById(id))
    },
    component: BookingPage
});


export const bookingRoutes = bookingLayoutRoute.addChildren([
    bookingRoute,
])
