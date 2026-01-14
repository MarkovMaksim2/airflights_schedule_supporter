CREATE SCHEMA IF NOT EXISTS notification;

CREATE TABLE IF NOT EXISTS notification.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    event_id VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(64) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    retries INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL
);
