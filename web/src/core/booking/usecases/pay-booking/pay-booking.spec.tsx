import { createTestStore } from "@/core/create-store";
import { describe, expect, test, vitest } from "vitest";
import { payBooking } from "./pay-booking";
import { stateBuilder } from "@/core/state-builder";
import { bookingEntityBuilder } from "../../entities/booking.entity";
import { PaymentGateway } from "../../ports/payment.gateway";
import { FakePaymentGateway } from "../../adapters/fake-payment.gateway";

const spyPaymentGateway = (): PaymentGateway => {
    return {
        payBooking: vitest.fn()
    }
}

describe('Feature: Pay booking', () => {
    test('Payment is requested', async () => {
        const paymentGateway = spyPaymentGateway()
        const initialStateBuilder = stateBuilder().withBooking(bookingEntityBuilder().withClientSecret('client-secret').build())
        const store = createTestStore({ paymentGateway }, initialStateBuilder.build())


        await store.dispatch(payBooking({
            paymentContext: {
                card: 'card-number'
            }, bookingId: 'booking-1'
        }))

        expect(paymentGateway.payBooking).toHaveBeenCalledWith('client-secret', {
            card: 'card-number'
        })
    })

    test('isPaying when pending', async () => {
        const store = createTestStore()

        store.dispatch(payBooking({ paymentContext: {}, bookingId: 'booking-1' }))

        expect(store.getState()).toEqual(stateBuilder().withIsPaying(true).build())
    })

    test('is NOT paying when fullfilled', async () => {
        const store = createTestStore()

        await store.dispatch(payBooking({ paymentContext: {}, bookingId: 'booking-1' }))

        expect(store.getState()).toEqual(stateBuilder().withIsPaying(false).build())
    })

    test('is NOT paying when rejected', async () => {
        const paymentGateway = new FakePaymentGateway()
        paymentGateway.willFail = true
        const store = createTestStore({ paymentGateway })

        await store.dispatch(payBooking({ paymentContext: {}, bookingId: 'booking-1' }))

        expect(store.getState()).toEqual(stateBuilder().withIsPaying(false).build())
    })
})
