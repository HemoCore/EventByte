import { Stripe, StripeElements } from "@stripe/stripe-js";
import { PaymentGateway } from "../ports/payment.gateway";

export class StripePaymentGateway implements PaymentGateway {
    constructor(private readonly stripe: Stripe) { }

    async payBooking(_: string, paymentContext: StripeElements): Promise<void> {
        const result = await this.stripe.confirmPayment({
            elements: paymentContext,
            redirect: 'if_required'
        });
        if (result.error) {
            throw new Error(result.error.message);
        }
    }
}
