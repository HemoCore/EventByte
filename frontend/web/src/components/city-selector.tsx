import { useState } from "react"
import { Check, ChevronsUpDown } from "@/components/icons"
import { Button } from "@/design-system/components/ui/button"
import frFlag from "@/assets/fr-flag.svg"
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from "@/design-system/components/ui/command"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/design-system/components/ui/popover"
import { cn } from "@/design-system/lib/utils"

export type City = {
    name: string
    lat: number
    lng: number
}

export const cities: City[] = [
    { name: "Paris", lat: 48.8585, lng: 2.3475 },
    { name: "Marseille", lat: 43.2965, lng: 5.3698 },
    { name: "Lyon", lat: 45.7640, lng: 4.8357 },
    { name: "Toulouse", lat: 43.6047, lng: 1.4442 },
    { name: "Nice", lat: 43.7102, lng: 7.2620 },
    { name: "Nantes", lat: 47.2184, lng: -1.5536 },
    { name: "Montpellier", lat: 43.6108, lng: 3.8767 },
    { name: "Strasbourg", lat: 48.5734, lng: 7.7521 },
    { name: "Bordeaux", lat: 44.8378, lng: -0.5792 },
    { name: "Lille", lat: 50.6292, lng: 3.0573 },
    { name: "Rennes", lat: 48.1173, lng: -1.6778 },
    { name: "Reims", lat: 49.2583, lng: 4.0317 },
    { name: "Le Havre", lat: 49.4944, lng: 0.1079 },
    { name: "Saint-Étienne", lat: 45.4397, lng: 4.3872 },
    { name: "Toulon", lat: 43.1242, lng: 5.9280 },
    { name: "Grenoble", lat: 45.1885, lng: 5.7245 },
    { name: "Dijon", lat: 47.3220, lng: 5.0415 },
    { name: "Angers", lat: 47.4784, lng: -0.5632 },
    { name: "Nîmes", lat: 43.8367, lng: 4.3601 },
    { name: "Aix-en-Provence", lat: 43.5297, lng: 5.4474 },
]

type CitySelectorProps = {
    selectedCity: City
    onCityChange: (city: City) => void
}

export const CitySelector = ({ selectedCity, onCityChange }: CitySelectorProps) => {
    const [open, setOpen] = useState(false)

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="ghost"
                    role="combobox"
                    aria-expanded={open}
                    className="justify-between p-0 h-auto font-bold text-3xl hover:bg-transparent gap-2"
                >
                    <span className="flex items-center gap-3">
                        <span className="inline-flex items-center justify-center w-6 h-6 rounded-full overflow-hidden bg-muted/50">
                            <img
                                src={frFlag}
                                alt="France"
                                className="w-full h-full object-cover"
                            />
                        </span>
                        {selectedCity.name}
                    </span>
                    <ChevronsUpDown className="ml-2 h-5 w-5 shrink-0 opacity-50" />
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[200px] p-0">
                <Command>
                    <CommandInput placeholder="Search city..." />
                    <CommandList>
                        <CommandEmpty>No city found.</CommandEmpty>
                        <CommandGroup>
                            {cities.map((city) => (
                                <CommandItem
                                    key={city.name}
                                    value={city.name}
                                    onSelect={() => {
                                        onCityChange(city)
                                        setOpen(false)
                                    }}
                                >
                                    <Check
                                        className={cn(
                                            "mr-2 h-4 w-4",
                                            selectedCity.name === city.name ? "opacity-100" : "opacity-0"
                                        )}
                                    />
                                    {city.name}
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    )
}
