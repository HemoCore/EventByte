export type Ticket = {
    id: string
    eventId: string
    name: string
    price: number
    quantity: number
}

export interface TicketsGateway {
    getEventTickets(eventId: string): Promise<Ticket[]>
}
