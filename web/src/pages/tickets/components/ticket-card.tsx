import { FC } from "react"
import { selectTicketCardViewmodel } from "./ticket-card.viewmodel"
import { useSelector } from "react-redux"
import { Link } from "@tanstack/react-router"
import { Card, CardContent } from "@/design-system/components/ui/card"
import { Badge } from "@/design-system/components/ui/badge"

type Props = {
    id: string
}

export const TicketCard: FC<Props> = ({ id }) => {
    const { event, quantity, isUpcoming } = useSelector(selectTicketCardViewmodel({
        now: new Date(),
        id,
    }))

    return (
        <Link to="/tickets/$id" params={{ id: id }}>
            <Card className="overflow-hidden transition-all hover:shadow-lg cursor-pointer">
                <CardContent className="p-0">
                    <div className="flex gap-3 p-3">
                        <img
                            src={event.cover}
                            alt={event.title}
                            className="w-24 h-24 object-cover rounded aspect-square"
                        />
                        <div className="flex-1 flex flex-col justify-between py-0.5">
                            <div className="space-y-1.5">
                                <div className="flex items-start justify-between gap-2">
                                    <h3 className="font-semibold text-base line-clamp-2">
                                        {event.title}
                                    </h3>
                                    {isUpcoming && (
                                        <Badge variant="default" className="shrink-0">
                                            Upcoming
                                        </Badge>
                                    )}
                                </div>
                                <div className="space-y-0.5 text-sm text-muted-foreground">
                                    <p>{event.date}</p>
                                    <p>{event.location}</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-3 text-sm">
                                <span className="font-medium">
                                    {quantity} {quantity === 1 ? "ticket" : "tickets"}
                                </span>
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </Link>
    )
}
