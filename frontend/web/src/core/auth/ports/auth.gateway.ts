export type AuthUser = {
    id: string
    email: string
    fullname: string
}

export type OnAuthStateChangeCallback = (user?: AuthUser) => void

export type AuthGateway = {
    login: () => void
    logout: () => void
    onAuthStateChange: (callback: OnAuthStateChangeCallback) => () => void
}
