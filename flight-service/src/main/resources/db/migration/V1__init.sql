CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    airline_id BIGINT NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    seats INTEGER NOT NULL DEFAULT 100
);