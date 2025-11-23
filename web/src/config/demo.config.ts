import { AsyncStorageAuthGateway } from "@/core/auth/adapters/async-storage-auth.gateway";
import { FakeAuthGateway } from "@/core/auth/adapters/fake-auth.gateway";
import { FakeBookingGateway } from "@/core/booking/adapters/fake-booking.gateway";
import { StripePaymentGateway } from "@/core/booking/adapters/stripe-payment.gateway";
import { FakeEventsGateway } from "@/core/events/adapters/fake-events.gateway";
import { FakeLikesGateway } from "@/core/events/adapters/fake-likes.gateway";
import { FakeTicketsGateway } from "@/core/tickets/adapters/fake-tickets.gateway";
import { loadStripe } from "@stripe/stripe-js";
import type { ConfigFactory } from "./config-registry";
import { demoBookingNextId, demoBookings } from "./demo-data/bookings.data";
import { demoEvents, demoLikedEvents } from "./demo-data/events.data";
import { demoTickets } from "./demo-data/tickets.data";
import { demoUser } from "./demo-data/user.data";
import type { EnvConfig } from "../lib/env";

export const config: ConfigFactory = async (env: EnvConfig) => {
    const fakeAuthGateway = new FakeAuthGateway();
    fakeAuthGateway.logUserWith = demoUser;
    const authGateway = new AsyncStorageAuthGateway(fakeAuthGateway);

    const eventsGateway = new FakeEventsGateway(300);
    eventsGateway.likedEvents = demoLikedEvents;
    eventsGateway.events = demoEvents;

    const likesGateway = new FakeLikesGateway(300);

    const ticketsGateway = new FakeTicketsGateway(600);
    ticketsGateway.setTickets(demoTickets);

    const bookingGateway = new FakeBookingGateway(300, ticketsGateway, env.stripe.clientSecret);
    bookingGateway.nextId = demoBookingNextId;
    for (const booking of demoBookings) {
        bookingGateway.addBooking(booking);
    }

    (window as any).bookingGateway = bookingGateway

    const stripe = await loadStripe(env.stripe.publicKey);

    if (!stripe) {
        throw new Error("Failed to load Stripe");
    }

    const paymentGateway = new StripePaymentGateway(stripe);

    return {
        dependencies: {
            authGateway,
            eventsGateway,
            likesGateway,
            ticketsGateway,
            bookingGateway,
            paymentGateway,
        },
        stripe,
    };
}
