import { PaymentGateway } from "../ports/payment.gateway";

export class FakePaymentGateway implements PaymentGateway {
    willFail = false

    constructor(private readonly delay = 0) { }

    async payBooking(clientSecret: string, paymentContext: unknown): Promise<void> {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        if (this.willFail) {
            throw new Error("Payment failed")
        }
    }
}
