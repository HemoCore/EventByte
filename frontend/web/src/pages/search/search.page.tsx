import EventCard from "@/components/event-card/event-card";
import { SearchX } from "@/components/icons";
import {
    Empty,
    EmptyDescription,
    EmptyHeader,
    EmptyMedia,
    EmptyTitle,
} from "@/design-system/components/ui/empty";
import { useSelector } from "react-redux";
import { LocationFilter } from "./components/location-filter";
import { selectSearchViewmodel } from "./search.viewmodel";
import { useAppDispatch } from "@/hooks/store-hooks";

export const SearchPage = () => {
    const dispatch = useAppDispatch();
    const viewmodel = useSelector(selectSearchViewmodel(dispatch));

    return (
        <div className="py-8">
            <div className="space-y-8">
                <div className="space-y-4">
                    <h1 className="text-3xl font-bold tracking-tight">Search Events</h1>
                    <LocationFilter
                        location={viewmodel.location}
                        radius={viewmodel.radius}
                        onLocationChange={viewmodel.setLocation}
                        onDistanceChange={viewmodel.setRadius}
                    />
                </div>

                {viewmodel.type === "loading" && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {Array.from({ length: 8 }).map((_, index) => (
                            <EventCard.Loading key={index} />
                        ))}
                    </div>
                )}

                {viewmodel.type === "empty" && (
                    <Empty>
                        <EmptyHeader>
                            <EmptyMedia variant="icon">
                                <SearchX />
                            </EmptyMedia>
                            <EmptyTitle>No events found</EmptyTitle>
                            <EmptyDescription>
                                Try adjusting your search criteria
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
        </div>
    );
};
