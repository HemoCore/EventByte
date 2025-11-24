export type PaymentGateway = {
    payBooking(clientSecret: string, paymentContext: unknown): Promise<void>
}
