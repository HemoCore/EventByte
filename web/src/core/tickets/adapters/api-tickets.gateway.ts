import { AxiosInstance } from "axios"
import { z } from "zod"
import { Ticket, TicketsGateway } from "../ports/tickets.gateway"

const TicketSchema = z.object({
    id: z.string(),
    eventId: z.string(),
    name: z.string(),
    price: z.number(),
    quantity: z.number(),
})

const TicketsArraySchema = z.array(TicketSchema)

export class ApiTicketsGateway implements TicketsGateway {
    constructor(private readonly client: AxiosInstance) { }

    async getEventTickets(eventId: string): Promise<Ticket[]> {
        const response = await this.client.get(`/events/${eventId}/tickets`)
        return TicketsArraySchema.parse(response.data)
    }
}
