import { Outlet, useParams } from "@tanstack/react-router"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { FC } from "react"
import { useSelector } from "react-redux"
import { selectEventById } from "@/core/events/events.slice"
import { Main } from "@/components/main"

const BackgroundImage: FC = () => {
    const { id } = useParams({ from: '/event/event/$id' })
    const event = useSelector(selectEventById(id))

    if (!event) {
        return null
    }

    return (
        <div className="absolute inset-x-0 top-0 -z-1 h-[56.25vw] max-h-[480px] blur-3xl">
            <img src={event.cover} alt="" className="h-full w-full object-cover" />
        </div>
    )
}
export const EventLayout = () => {
    return (
        <div className="min-h-screen flex flex-col">
            <BackgroundImage />
            <Header />
            <Main>
                <Outlet />
            </Main>
            <Footer />
        </div>
    )
}
