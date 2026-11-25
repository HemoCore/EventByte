-- Likes des utilisateurs sur les événements
CREATE TABLE likes (
                       user_id    VARCHAR(255) NOT NULL,
                       event_id   VARCHAR(64)  NOT NULL REFERENCES events (id) ON DELETE CASCADE,
                       created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       PRIMARY KEY (user_id, event_id)
);

CREATE INDEX idx_likes_event_id ON likes (event_id);