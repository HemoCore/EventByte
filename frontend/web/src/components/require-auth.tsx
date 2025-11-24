import { isAuthSelector } from "@/core/auth/auth.slice"
import { login } from "@/core/auth/usecases/login/login.usecase"
import { useAppDispatch } from "@/hooks/store-hooks"
import { cloneElement, FC, ReactElement, MouseEventHandler, MouseEvent } from "react"
import { useSelector } from "react-redux"

type Props = {
    children: ReactElement<{ onClick?: MouseEventHandler }>
}

export const RequireAuth: FC<Props> = ({ children }) => {
    const dispatch = useAppDispatch()
    const isAuthenticated = useSelector(isAuthSelector)

    if (isAuthenticated) {
        return children
    }

    return cloneElement(children, {
        onClick: (e: MouseEvent) => {
            e.preventDefault()
            e.stopPropagation()
            dispatch(login())
        }
    })
}
