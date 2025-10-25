CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    airline_id BIGINT NOT NULL REFERENCES airlines(id),
    departure_airport_id BIGINT NOT NULL REFERENCES airports(id),
    arrival_airport_id BIGINT NOT NULL REFERENCES airports(id),
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    seats INTEGER NOT NULL DEFAULT 100
);