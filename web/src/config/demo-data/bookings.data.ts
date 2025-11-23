import { Booking } from "@/core/booking/ports/booking.gateway";

export const demoBookings: Booking[] = [
    {
        id: "booking-pending-1",
        eventId: "1",
        bookedAt: new Date(),
        lines: [
            {
                id: "early-bird",
                name: "Early Bird",
                quantity: 1,
                price: 13,
            },
            {
                id: "regular",
                name: "Regular",
                quantity: 3,
                price: 20,
            },
        ],
        status: "pending",
        total: 74.2,
        fees: 1.2,
        clientSecret: "pi_3STmGtQRC8413tQ31GYgZl8E_secret_8Hb4ERO0fjgNDw4pJUXADHtf8",
    },
    {
        id: "booking-upcoming-1",
        eventId: "1",
        bookedAt: new Date("2025-11-15T10:00:00Z"),
        lines: [
            {
                id: "vip",
                name: "VIP Pass",
                quantity: 2,
                price: 45.99,
            },
        ],
        status: "approved",
        total: 93.48,
        fees: 1.5,
        clientSecret: "",
    },
    {
        id: "booking-upcoming-2",
        eventId: "2",
        bookedAt: new Date("2025-11-20T14:30:00Z"),
        lines: [
            {
                id: "general",
                name: "General Admission",
                quantity: 1,
                price: 18.2,
            },
        ],
        status: "approved",
        total: 19.2,
        fees: 1.0,
        clientSecret: "",
    },
    {
        id: "booking-past-1",
        eventId: "5",
        bookedAt: new Date("2024-05-15T09:00:00Z"),
        lines: [
            {
                id: "standard",
                name: "Standard",
                quantity: 2,
                price: 35.0,
            },
        ],
        status: "approved",
        total: 71.5,
        fees: 1.5,
        clientSecret: "",
    },
];

export const demoBookingNextId = "1234";
