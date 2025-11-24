import { AxiosInstance } from "axios"
import { z } from "zod"
import { EventSource } from "eventsource"
import {
    Booking,
    BookingGateway,
    BookParams,
    OnBookingChangeCallback,
} from "../ports/booking.gateway"

const BookingLineSchema = z.object({
    id: z.string(),
    name: z.string(),
    quantity: z.number(),
    price: z.number(),
})

const BookingStatusSchema = z.enum(['requested', 'reserved', 'rejected', 'awaiting-payment', 'expired', 'confirmed'])

const ApiBookingResponseSchema = z.object({
    id: z.string(),
    eventId: z.string(),
    bookedAt: z.string(),
    lines: z.array(BookingLineSchema),
    status: BookingStatusSchema,
    total: z.number(),
    fees: z.number(),
    clientSecret: z.string().optional(),
})

const BookingIdSchema = z.object({
    id: z.string(),
})

const BookingsArraySchema = z.array(ApiBookingResponseSchema)

type ApiBookingResponse = z.infer<typeof ApiBookingResponseSchema>

export class ApiBookingGateway implements BookingGateway {
    constructor(
        private readonly client: AxiosInstance,
        private readonly getToken: () => string | undefined,
        private readonly apiUrl: string
    ) { }

    async book(params: BookParams): Promise<string> {
        const response = await this.client.post("/bookings", {
            items: params,
        })
        const validated = BookingIdSchema.parse(response.data)
        return validated.id
    }

    async getById(id: string): Promise<Booking | undefined> {
        try {
            const response = await this.client.get(`/bookings/${id}`)
            const validated = ApiBookingResponseSchema.parse(response.data)
            return this.mapBooking(validated)
        } catch (error) {
            if (
                error &&
                typeof error === "object" &&
                "response" in error &&
                (error as { response?: { status?: number } }).response?.status === 404
            ) {
                return undefined
            }
            throw error
        }
    }

    async getUserBookings(): Promise<Booking[]> {
        const response = await this.client.get("/bookings/user")
        const validated = BookingsArraySchema.parse(response.data)
        return validated.map(this.mapBooking)
    }

    subscribeToBookingChanges(callback: OnBookingChangeCallback): () => void {
        const token = this.getToken()

        const eventSource = new EventSource(`${this.apiUrl}/bookings/stream`, {
            fetch: (input, init) =>
                fetch(input, {
                    ...init,
                    headers: {
                        ...init.headers,
                        Authorization: `Bearer ${token}`,
                    },
                }),
        })

        eventSource.addEventListener('booking_updated', (event) => {
            try {
                const data = JSON.parse(event.data)
                const validated = ApiBookingResponseSchema.parse(data)
                const booking = this.mapBooking(validated)
                callback(booking)
            } catch (error) {
                console.error('Error parsing SSE booking data:', error)
            }
        })

        eventSource.addEventListener('error', (error) => {
            console.error('SSE connection error:', error)
            eventSource.close()
        })

        return () => {
            eventSource.close()
        }
    }

    private mapBooking(data: ApiBookingResponse): Booking {
        return {
            ...data,
            bookedAt: new Date(data.bookedAt),
        }
    }
}
