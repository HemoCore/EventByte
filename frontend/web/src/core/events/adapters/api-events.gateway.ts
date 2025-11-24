import { AxiosInstance } from "axios"
import { z } from "zod"
import {
    DiscoverParams,
    Event,
    EventsGateway,
    SearchParams,
} from "../ports/events.gateway"

const VenueSchema = z.object({
    name: z.string(),
    adresse: z.string(),
    coordinates: z.object({
        latitude: z.number(),
        longitude: z.number(),
    }),
})

const EventSchema = z.object({
    id: z.string(),
    title: z.string(),
    cover: z.string(),
    likedCount: z.number(),
    venue: VenueSchema,
    isSoldOut: z.boolean(),
    startAt: z.string(),
    lowestPrice: z.number(),
    description: z.string(),
})

const EventsArraySchema = z.array(EventSchema)

export class ApiEventsGateway implements EventsGateway {
    constructor(private readonly client: AxiosInstance) {}

    async getDiscoverEvents(params: DiscoverParams): Promise<Event[]> {
        const response = await this.client.get("/events/discover", {
            params: {
                lat: params.lat,
                lng: params.lng,
            },
        })
        return EventsArraySchema.parse(response.data)
    }

    async getLikedEvents(): Promise<Event[]> {
        const response = await this.client.get("/events/liked")
        return EventsArraySchema.parse(response.data)
    }

    async getEventById(id: string): Promise<Event> {
        const response = await this.client.get(`/events/${id}`)
        return EventSchema.parse(response.data)
    }

    async getEventsByIds(ids: string[]): Promise<Event[]> {
        const response = await this.client.get("/events", {
            params: {
                ids: ids.join(","),
            },
        })
        return EventsArraySchema.parse(response.data)
    }

    async searchEvents(params: SearchParams): Promise<Event[]> {
        const response = await this.client.get("/events/search", {
            params: {
                lat: params.lat,
                lng: params.lng,
                radius: params.radius,
                query: params.query,
            },
        })
        return EventsArraySchema.parse(response.data)
    }
}
