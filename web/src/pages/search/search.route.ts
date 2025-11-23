import { createRoute } from "@tanstack/react-router"
import { SearchPage } from "./search.page"
import { rootRoute } from "@/router"
import { SearchLayout } from "./search.layout"

const layoutRoute = createRoute({
    getParentRoute: () => rootRoute,
    id: 'search',
    component: SearchLayout,
})

const searchRoute = createRoute({
    getParentRoute: () => layoutRoute,
    path: '/search',
    component: SearchPage,
})

export const searchRoutes = layoutRoute.addChildren([searchRoute])

