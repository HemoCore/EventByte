import { Skeleton } from "@/design-system/components/ui/skeleton"
import { useSelector } from "react-redux"
import { selectTicketsViewmodel } from "./select-tickets.viewmodel"
import { exhaustiveCheck } from "@/lib/exhaustive-check"
import { TicketCard } from "./ticket-card"
import { Empty, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "@/design-system/components/ui/empty"
import { Ticket } from "@/components/icons"

export const UpComingTickets = () => {
    const viewmodel = useSelector(selectTicketsViewmodel({ now: new Date(), status: 'upcoming' }))

    switch (viewmodel.type) {
        case 'loading':
            return <Loading />
        case 'empty':
            return <EmptyState />
        case 'loaded':
            return (
                <div className="grid gap-4">
                    {viewmodel.tickets.map((id) => (
                        <TicketCard key={id} id={id} />
                    ))}
                </div>
            )
        default:
            return exhaustiveCheck(viewmodel)
    }
}

const Loading = () => {
    return (
        <div className="grid gap-4">
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
        </div>
    )
}

const EmptyState = () => {
    return (
        <Empty>
            <EmptyHeader>
                <EmptyMedia variant="icon">
                    <Ticket />
                </EmptyMedia>
                <EmptyTitle>No upcoming tickets</EmptyTitle>
                <EmptyDescription>
                    Your upcoming event tickets will appear here
                </EmptyDescription>
            </EmptyHeader>
        </Empty>
    )
}
