import { Provider } from "react-redux"
import { RouterProvider } from "@tanstack/react-router"
import { AppStore } from "./core/create-store"
import { Router } from "./router"
import { DependenciesProvider } from "./context/dependencies.context"
import { FC } from "react"
import { Stripe } from "@stripe/stripe-js"

type Props = {
    store: AppStore,
    stripe: Stripe,
    router: Router
}

export const App: FC<Props> = ({ store, stripe, router }) => {
    return (
        <Provider store={store}>
            <DependenciesProvider dependencies={{ stripe }}>
                <RouterProvider router={router} />
            </DependenciesProvider>
        </Provider>
    )
}
