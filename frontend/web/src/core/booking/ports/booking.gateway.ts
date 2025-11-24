import { BookingStatus } from "../entities/booking.entity";

export type BookingLine = {
    id: string;
    name: string;
    quantity: number;
    price: number
}


export type Booking = {
    id: string;
    eventId: string;
    bookedAt: Date;
    lines: BookingLine[]
    status: BookingStatus;
    total: number
    fees: number
    clientSecret?: string
}

export type OnBookingChangeCallback = (booking: Booking) => void

export type BookParams = {
    id: string
    quantity: number
}[]

export type BookingGateway = {
    book(params: BookParams): Promise<string>
    getById(id: string): Promise<Booking | undefined>
    getUserBookings(): Promise<Booking[]>
    subscribeToBookingChanges(callback: OnBookingChangeCallback): () => void
}
