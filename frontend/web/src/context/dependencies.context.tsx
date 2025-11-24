import { Stripe } from "@stripe/stripe-js"
import { createContext, FC, PropsWithChildren, useContext } from "react"

export type Dependencies = {
    stripe: Stripe
}

export const DependenciesContext = createContext<Dependencies | undefined>(undefined)

type Props = {
    dependencies: Dependencies
}

export const DependenciesProvider: FC<PropsWithChildren<Props>> = ({ children, dependencies }) => {
    return (
        <DependenciesContext.Provider value={
            dependencies
        }>
            {children}
        </DependenciesContext.Provider >
    )
}

export const useDependencies = () => {
    const dependencies = useContext(DependenciesContext)
    if (!dependencies) {
        throw new Error("useDependencies must be used within a DependenciesProvider")
    }
    return dependencies
}
