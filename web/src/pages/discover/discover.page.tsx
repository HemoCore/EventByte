import { useEffect, useState } from "react"
import { CitySelector, cities, type City } from "@/components/city-selector"
import EventCard from "@/components/event-card/event-card"
import { useAppDispatch } from "@/hooks/store-hooks";
import { loadDiscoverEvents } from "@/core/events/usecases/load-discover-events/load-discover-events.usecase";
import { useSelector } from "react-redux";
import { SearchX } from "@/components/icons";
import { Empty, EmptyHeader, EmptyMedia, EmptyTitle, EmptyDescription } from "@/design-system/components/ui/empty";
import { createDiscoverPageViewmodel } from "./discover.viewmodel";

export const DiscoverPage = () => {
    const dispatch = useAppDispatch()
    const [selectedCity, setSelectedCity] = useState<City>(cities[0])

    const viewmodel = useSelector(createDiscoverPageViewmodel())

    useEffect(() => {
        dispatch(loadDiscoverEvents({ lat: selectedCity.lat, lng: selectedCity.lng }))
    }, [selectedCity])

    return (
        <div className="py-8">
            <div className="space-y-8">
                <h1 className="text-3xl font-bold tracking-tight flex items-center">
                    Discover events in <CitySelector selectedCity={selectedCity} onCityChange={setSelectedCity} />
                </h1>
                {viewmodel.type === 'loading' && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {Array.from({ length: 8 }).map((_, index) => (
                            <EventCard.Loading key={index} />
                        ))}
                    </div>
                )}

                {viewmodel.type === 'empty' && (
                    <Empty>
                        <EmptyHeader>
                            <EmptyMedia variant="icon">
                                <SearchX />
                            </EmptyMedia>
                            <EmptyTitle>No events found</EmptyTitle>
                            <EmptyDescription>
                                There are no events in {selectedCity.name} at the moment
                            </EmptyDescription>
                        </EmptyHeader>
                    </Empty>
                )}

                {viewmodel.type === "with-events" && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {viewmodel.events.map((id) => (
                            <EventCard key={id} id={id} />
                        ))}
                    </div>
                )}
            </div>
        </div >
    );
};
