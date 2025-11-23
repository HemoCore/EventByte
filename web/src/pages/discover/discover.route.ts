import { createRoute } from '@tanstack/react-router'
import { DiscoverPage } from './discover.page'
import { rootRoute } from '@/router'
import { Layout } from './discover.layout'

const layoutRoute = createRoute({
    getParentRoute: () => rootRoute,
    id: 'layout',
    component: Layout,
})

export const discoverRoute = createRoute({
    getParentRoute: () => layoutRoute,
    path: '/',
    component: DiscoverPage,
})

export const discoverRoutes = layoutRoute.addChildren([discoverRoute])
