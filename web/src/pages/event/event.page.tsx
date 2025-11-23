import { useParams, notFound } from "@tanstack/react-router"
import { useSelector } from "react-redux"
import { createEventPageViewmodel, EventPageViewmodel } from "./event.viewmodel"
import { FC } from "react"
import EventBreadcrumb from "./components/event-breadcrumb"
import EventInfos from "./components/event-infos"
import EventDescription from "./components/event-description"
import EventLocation from "./components/event-location"
import EventBredCrumb from "./components/event-breadcrumb"
import { exhaustiveCheck } from "@/lib/exhaustive-check"
import EventTickets from "./components/event-tickets"

export const EventPage = () => {
    const { id } = useParams({
        from: '/event/event/$id'
    })

    const viewmodel = useSelector(
        createEventPageViewmodel({ id })
    )

    switch (viewmodel.type) {
        case 'loading':
            return <Loading />
        case 'loaded':
            return <Loaded viewmodel={viewmodel} />
        case 'not-found':
            throw notFound()
        default:
            exhaustiveCheck(viewmodel)
    }
}

type Props = {
    viewmodel: Extract<EventPageViewmodel, { type: 'loaded' }>
}

const Loaded: FC<Props> = ({ viewmodel: { event } }) => {
    return (
        <div className="py-8 space-y-12">
            <EventBreadcrumb title={event.title} />
            <EventInfos event={event} />
            <EventTickets eventId={event.id} />
            <EventDescription description={event.description} />
            <EventLocation venue={event.venue} />
        </div>
    )
}

const Loading = () => {
    return (
        <div className="py-8 space-y-12">
            <EventBredCrumb.Loading />
            <EventInfos.Loading />
            <EventTickets.Loading />
            <EventDescription.Loading />
            <EventLocation.Loading />
        </div>
    )
}
