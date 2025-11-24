import { Ticket, TicketsGateway } from "../ports/tickets.gateway"

export class FakeTicketsGateway implements TicketsGateway {
    ticketsByEventId: Map<string, Ticket[]> = new Map()
    ticketsById: Map<string, Ticket> = new Map()
    willFail = false

    constructor(private readonly delay = 0) { }

    async getEventTickets(eventId: string): Promise<Ticket[]> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        if (this.willFail) {
            throw new Error('Failed to load tickets')
        }
        return this.ticketsByEventId.get(eventId) || []
    }

    setTickets(tickets: Ticket[]) {
        for (const ticket of tickets) {
            this.ticketsByEventId.set(ticket.eventId, [...this.ticketsByEventId.get(ticket.eventId) || [], ticket])
            this.ticketsById.set(ticket.id, ticket)
        }
    }

    getTicketById(ticketId: string): Ticket | undefined {
        return this.ticketsById.get(ticketId)
    }
}
