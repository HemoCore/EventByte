import { CitySelector, cities, type City } from "@/components/city-selector";
import { Select } from "@/design-system/components/ui/select";

const DISTANCE_OPTIONS = [1000, 2000, 5000, 10_000, 20_000, 50_000, 100_000] as const;

type DistanceKm = (typeof DISTANCE_OPTIONS)[number];

type LocationFilterProps = {
    location: City;
    radius: number;
    onLocationChange?: (city: City) => void;
    onDistanceChange?: (distance: DistanceKm) => void;
};

export const LocationFilter = ({
    location,
    radius,
    onLocationChange,
    onDistanceChange,
}: LocationFilterProps) => {
    const handleCityChange = (city: City) => {
        onLocationChange?.(city);
    };

    const handleDistanceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const distance = Number(e.target.value) as DistanceKm;
        onDistanceChange?.(distance);
    };

    return (
        <div className="flex items-center gap-6 flex-wrap">
            <div className="flex items-center gap-2">
                <span className="text-sm text-muted-foreground">Location:</span>
                <CitySelector
                    selectedCity={location}
                    onCityChange={handleCityChange}
                />
            </div>

            <div className="flex items-center gap-2">
                <span className="text-sm text-muted-foreground">Within:</span>
                <Select
                    value={radius}
                    onChange={handleDistanceChange}
                >
                    {DISTANCE_OPTIONS.map((distance) => (
                        <option key={distance} value={distance}>
                            {distance / 1000} km
                        </option>
                    ))}
                </Select>
            </div>
        </div>
    );
};
