import { EventsGateway, Event, DiscoverParams, SearchParams } from "../ports/events.gateway";

export class FakeEventsGateway implements EventsGateway {
    events: Event[] = []
    likedEvents: string[] = []
    willFail: boolean = false

    constructor(private delay: number = 0) { }

    async getDiscoverEvents(params: DiscoverParams): Promise<Event[]> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        return this.events.filter(event => event.venue.coordinates.latitude === params.lat && event.venue.coordinates.longitude === params.lng)
    }

    async searchEvents(params: SearchParams): Promise<Event[]> {
        await new Promise(resolve => setTimeout(resolve, this.delay))

        // Filtrer par distance (simplifié pour la démo)
        const filteredByDistance = this.events.filter(event => {
            const distance = this.calculateDistance(
                params.lat,
                params.lng,
                event.venue.coordinates.latitude,
                event.venue.coordinates.longitude
            )
            return distance <= params.radius
        })

        // Filtrer par texte de recherche
        if (!params.query.trim()) {
            return filteredByDistance
        }

        const query = params.query.toLowerCase()
        return filteredByDistance.filter(event =>
            event.title.toLowerCase().includes(query) ||
            event.description.toLowerCase().includes(query) ||
            event.place.name.toLowerCase().includes(query) ||
            event.place.adresse.toLowerCase().includes(query)
        )
    }

    private calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
        // Formule de Haversine simplifiée pour calculer la distance en km
        const R = 6371 // Rayon de la Terre en km
        const dLat = this.toRadians(lat2 - lat1)
        const dLon = this.toRadians(lon2 - lon1)
        const a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(this.toRadians(lat1)) *
            Math.cos(this.toRadians(lat2)) *
            Math.sin(dLon / 2) *
            Math.sin(dLon / 2)
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    private toRadians(degrees: number): number {
        return degrees * (Math.PI / 180)
    }

    async getLikedEvents(): Promise<Event[]> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        if (this.willFail) {
            throw new Error()
        }
        return this.likedEvents.map(id => this.events.find(event => event.id === id)!)
    }

    async getEventById(id: string): Promise<Event> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        const event = this.events.find(event => event.id === id)
        if (!event) {
            throw new Error('Event not found')
        }
        return event
    }

    async getEventsByIds(ids: string[]): Promise<Event[]> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        return this.events.filter(event => ids.includes(event.id))
    }
}
