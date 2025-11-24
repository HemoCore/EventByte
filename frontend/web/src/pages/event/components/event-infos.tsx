import { withLoading } from "@/lib/with-loading";
import { Button } from "@/design-system/components/ui/button";
import { Skeleton } from "@/design-system/components/ui/skeleton";
import { FC } from "react";
import { InterestedButton } from "./interested-button/interested-button";
import { Calendar, Home, MapPin } from "@/components/icons";

type Props = {
    event: {
        id: string
        title: string
        date: string
        time: string
        cover: string
        venue: {
            name: string
            address: string
        }
        lowestPrice: string
        likedCount: number
    }
}

const EventInfos: FC<Props> = ({ event }) => {
    return (
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-6 order-2 lg:order-1">
                <div>
                    <h1 className="text-5xl font-bold uppercase mb-2" style={{ viewTransitionName: `event-title-${event.id}` }}>{event.title}</h1>
                </div>

                <div className="text-lg max-w-xl">
                    <div className="flex items-center gap-3">
                        <Calendar className="h-5 w-5" />
                        <span className="border-b py-4 w-full">
                            <span className="text-highlight font-medium">{event.date}</span>{' '}
                            <span className="text-muted-foreground">
                                from{' '}
                                <span className="text-foreground">{event.time}</span>
                            </span>
                        </span>
                    </div>

                    <div className="flex items-center gap-3">
                        <Home className="h-5 w-5" />
                        <div className="border-b w-full py-4">
                            <span>{event.venue.name}</span>
                        </div>
                    </div>

                    <div className="flex items-center gap-3">
                        <MapPin className="h-5 w-5" />
                        <span className="py-4 w-full">{event.venue.address}</span>
                    </div>
                </div>

                <div className="flex gap-4 pt-4">
                    <Button size="lg" className="text-base uppercase" asChild>
                        <a href="#tickets">
                            Tickets now at {event.lowestPrice}
                        </a>
                    </Button>
                    <InterestedButton eventId={event.id} />
                </div>

                <p className="text-sm">
                    {event.likedCount} are interested
                </p>
            </div>

            <div className="order-1 lg:order-2">
                <div className="aspect-video rounded-lg overflow-hidden md:shadow-3xl" style={{ viewTransitionName: `event-cover-${event.id}` }}>
                    <img
                        src={event.cover}
                        alt={event.title}
                        className="w-full h-full object-cover"
                    />
                </div>
            </div>
        </section>
    )
}

const Loading: FC = () => {
    return (
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-6">
                <div>
                    <Skeleton className="h-14 w-3/4 mb-2" />
                    <Skeleton className="h-4 w-24" />
                </div>
                <div className="space-y-4">
                    <Skeleton className="h-6 w-full" />
                    <Skeleton className="h-6 w-2/3" />
                    <Skeleton className="h-6 w-3/4" />
                </div>
                <div className="flex gap-4 pt-4">
                    <Skeleton className="h-11 w-48" />
                    <Skeleton className="h-11 w-36" />
                </div>
                <Skeleton className="h-4 w-32" />
            </div>
            <Skeleton className="aspect-[16/9] w-full rounded-lg" />
        </section>
    )
}

export default withLoading(EventInfos, Loading)
