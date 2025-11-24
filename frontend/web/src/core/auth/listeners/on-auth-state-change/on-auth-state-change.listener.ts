import { AppDispatch } from "@/core/create-store";
import { AuthGateway } from "../../ports/auth.gateway";
import { userAuthenticated } from "../../auth.slice";

export const createOnAuthStateChangeListener = (authGateway: AuthGateway, dispatch: AppDispatch) => () => {
    return authGateway.onAuthStateChange((user) => {
        if (!user) return dispatch(userAuthenticated(undefined))

        dispatch(userAuthenticated({
            id: user.id,
            email: user.email,
            fullname: user.fullname
        }))
    })
}
