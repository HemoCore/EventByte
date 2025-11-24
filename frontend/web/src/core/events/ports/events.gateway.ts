export type Venue = {
    name: string
    adresse: string
    coordinates: { latitude: number, longitude: number }
}

export type Event = {
    id: string
    title: string
    cover: string
    likedCount: number
    venue: Venue
    isSoldOut: boolean
    startAt: string
    lowestPrice: number
    description: string
}

export type DiscoverParams = {
    lat: number
    lng: number
}

export type SearchParams = {
    lat: number
    lng: number
    radius: number // in km
    query: string
}

export type EventsGateway = {
    getDiscoverEvents: (params: DiscoverParams) => Promise<Event[]>
    getLikedEvents: () => Promise<Event[]>
    getEventById: (id: string) => Promise<Event>
    getEventsByIds: (ids: string[]) => Promise<Event[]>
    searchEvents: (params: SearchParams) => Promise<Event[]>
}
