-- Événements reçus de Strapi
CREATE TABLE events (
                        id              VARCHAR(64)      PRIMARY KEY,
                        title           VARCHAR(255)     NOT NULL,
                        description     TEXT,
                        cover           TEXT,
                        start_at        TIMESTAMPTZ,
                        venue_name      VARCHAR(255)     NOT NULL,
                        venue_address   VARCHAR(500)     NOT NULL,
                        venue_latitude  DOUBLE PRECISION NOT NULL,
                        venue_longitude DOUBLE PRECISION NOT NULL
);

-- Types de tickets d'un événement
CREATE TABLE tickets (
                         id          VARCHAR(64)  PRIMARY KEY,
                         event_id    VARCHAR(64)  NOT NULL REFERENCES events (id) ON DELETE CASCADE,
                         name        VARCHAR(255) NOT NULL,
                         price_cents INTEGER      NOT NULL CHECK (price_cents >= 0),
                         quantity    INTEGER      NOT NULL CHECK (quantity >= 0)
);

CREATE INDEX idx_tickets_event_id ON tickets (event_id);