import { GoogleMap } from "@/components/GoogleMap"
import { withLoading } from "@/lib/with-loading"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import { Home, MapPin } from "@/components/icons"
import { FC } from "react"

type Props = {
    venue: {
        name: string
        address: string
        coordinates: {
            latitude: number
            longitude: number
        }
    }
}

const EventLocation: FC<Props> = ({ venue }) => {
    return (
        <section className="space-y-4">
            <h2 className="text-2xl font-bold">Location</h2>
            <div className="space-y-6">
                <div className="max-w-xl pb-6 space-y-4">
                    <div className="flex items-center gap-3">
                        <Home className="h-6 w-6" />
                        <span className="border-b py-4 w-full text-xl">
                            {venue.name}
                        </span>
                    </div>
                    <div className="flex items-center gap-3">
                        <MapPin className="h-6 w-6" />
                        <span className="text-xl">{venue.address}</span>
                    </div>
                </div>
                <div className="w-full h-[400px] rounded-lg overflow-hidden relative">
                    <GoogleMap latitude={venue.coordinates.latitude} longitude={venue.coordinates.longitude} />
                </div>
            </div>
        </section>
    )
}

const Loading: FC = () => {
    return (
        <section className="space-y-4">
            <Skeleton className="h-10 w-48" />
            <div className="space-y-6">
                <div className="max-w-2xl pb-6 space-y-4">
                    <Skeleton className="h-7 w-64" />
                    <Skeleton className="h-7 w-96" />
                </div>
                <Skeleton className="w-full h-[400px] rounded-lg" />
            </div>
        </section>
    )
}

export default withLoading(EventLocation, Loading)
