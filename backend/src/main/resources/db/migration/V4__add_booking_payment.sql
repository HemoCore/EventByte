-- US-16: the Stripe payment of a booking (filled when the booking becomes awaiting-payment)
ALTER TABLE bookings
    ADD COLUMN payment_intent_id VARCHAR(255),
    ADD COLUMN client_secret     VARCHAR(255);

-- one payment = one booking; US-17 finds the booking from the payment id sent by Stripe
CREATE UNIQUE INDEX idx_bookings_payment_intent_id ON bookings (payment_intent_id);
