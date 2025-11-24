import { FC } from "react"
import { useSelector } from "react-redux"
import { Link } from "@tanstack/react-router"
import { createEventCardViewmodel } from "./event-card.viewmodel"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import { withLoading } from "@/lib/with-loading"

type Props = {
    id: string
}

const EventCard: FC<Props> = ({ id }: Props) => {
    const { cover, title, price, date, time, venue } = useSelector(createEventCardViewmodel({ id }))

    return (
        <Link to="/event/$id" params={{ id }} className="group cursor-pointer block">
            <div className="relative aspect-[16/9] overflow-hidden rounded-lg bg-muted mb-4" style={{ viewTransitionName: `event-cover-${id}` }}>
                <img
                    src={cover}
                    alt={title}
                    className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                />
            </div>

            <div className="space-y-2">
                <h3 className="font-bold text-xl leading-tight line-clamp-2 h-14" style={{ viewTransitionName: `event-title-${id}` }}>
                    {title}
                </h3>

                <p className="text-muted-foreground text-sm h-5" style={{ viewTransitionName: `event-venue-${id}` }}>
                    {venue}
                </p>

                <div className="flex items-center justify-between pt-2">
                    <div className="text-sm">
                        <span className="text-highlight font-medium" style={{ viewTransitionName: `event-date-${id}` }}>{date}</span>
                        <span className="text-muted-foreground" style={{ viewTransitionName: `event-time-${id}` }}> | {time}</span>
                    </div>
                    <div className="text-lg font-semibold" style={{ viewTransitionName: `event-price-${id}` }}>
                        {price}
                    </div>
                </div>
            </div>
        </Link>
    )
}

const Loading: FC = () => {
    return (
        <div className="group">
            <Skeleton className="aspect-[16/9] w-full rounded-lg mb-4" />

            <div className="space-y-2">
                <Skeleton className="h-6 w-3/4" />
                <Skeleton className="h-6 w-1/2" />

                <Skeleton className="h-4 w-2/3" />

                <div className="flex items-center justify-between pt-2">
                    <Skeleton className="h-4 w-1/3" />
                    <Skeleton className="h-6 w-16" />
                </div>
            </div>
        </div>
    )
}

export default withLoading(EventCard, Loading)
