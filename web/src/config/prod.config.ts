import { KeycloakAuthGateway } from "@/core/auth/adapters/keycloak-auth.gateway";
import { ApiBookingGateway } from "@/core/booking/adapters/api-booking.gateway";
import { StripePaymentGateway } from "@/core/booking/adapters/stripe-payment.gateway";
import { ApiEventsGateway } from "@/core/events/adapters/api-events.gateway";
import { ApiLikesGateway } from "@/core/events/adapters/api-likes.gateway";
import { ApiTicketsGateway } from "@/core/tickets/adapters/api-tickets.gateway";
import { loadStripe } from "@stripe/stripe-js";
import Keycloak from "keycloak-js";
import { ConfigFactory } from "./config-registry";
import { createApiClient } from "@/core/shared/adapters/api";

export const config: ConfigFactory = async (env) => {
    const keycloak = new Keycloak({
        url: env.keycloak.url,
        realm: env.keycloak.realm,
        clientId: env.keycloak.clientId,
    });

    const authGateway = new KeycloakAuthGateway(keycloak);

    const apiClient = createApiClient({
        apiUrl: env.apiUrl,
        getToken: () => keycloak.token,
    })

    const eventsGateway = new ApiEventsGateway(apiClient);
    const likesGateway = new ApiLikesGateway(apiClient);
    const ticketsGateway = new ApiTicketsGateway(apiClient);
    const bookingGateway = new ApiBookingGateway(
        apiClient,
        () => keycloak.token,
        env.apiUrl
    );

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
