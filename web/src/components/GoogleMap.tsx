import { Skeleton } from "@/design-system/components/ui/skeleton";
import { FC, useState } from "react";

type Props = {
    latitude: number
    longitude: number
}

export const GoogleMap: FC<Props> = ({ latitude, longitude }) => {
    const [isMapLoaded, setIsMapLoaded] = useState(false)

    const onMapLoad = () => {
        setIsMapLoaded(true)
    }

    return (
        <>
            {!isMapLoaded && <Skeleton className="absolute inset-0 w-full h-full" />}
            <iframe
                width="100%"
                height="100%"
                allowFullScreen
                onLoad={onMapLoad}
                src={`https://maps.google.com/maps?q=${latitude},${longitude}&z=15&output=embed`}
            />
        </>
    )
}
