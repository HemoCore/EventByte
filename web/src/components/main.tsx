import clsx from "clsx"
import { FC, HtmlHTMLAttributes } from "react"

export const Main: FC<HtmlHTMLAttributes<HTMLDivElement>> = ({ children, className, ...props }) => {
    return (
        <main {...props} className={clsx("container mx-auto px-4 flex-1", className)} >
            {children}
        </main >
    )
}
