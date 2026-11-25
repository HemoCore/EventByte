CREATE TABLE bookings (
    id        VARCHAR(64)  PRIMARY KEY,
    user_id   VARCHAR(255) NOT NULL,
    event_id  VARCHAR(64)  NOT NULL REFERENCES events (id),
    booked_at TIMESTAMPTZ  NOT NULL,
    status    VARCHAR(32)  NOT NULL
);

CREATE INDEX idx_bookings_user_id ON bookings (user_id);

CREATE TABLE booking_lines (
    booking_id       VARCHAR(64)  NOT NULL REFERENCES bookings (id) ON DELETE CASCADE,
    ticket_id        VARCHAR(64)  NOT NULL,
    name             VARCHAR(255) NOT NULL,
    quantity         INTEGER      NOT NULL CHECK (quantity > 0),
    unit_price_cents BIGINT       NOT NULL CHECK (unit_price_cents >= 0),
    PRIMARY KEY (booking_id, ticket_id)
);
