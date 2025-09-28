CREATE TABLE airlines (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE airports (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL
);


CREATE TABLE passengers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    airline_id BIGINT NOT NULL REFERENCES airlines(id),
    departure_airport_id BIGINT NOT NULL REFERENCES airports(id),
    arrival_airport_id BIGINT NOT NULL REFERENCES airports(id),
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL
);


CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    passenger_id BIGINT NOT NULL REFERENCES passengers(id),
    flight_id BIGINT NOT NULL REFERENCES flights(id),
    booking_time TIMESTAMP NOT NULL
);


CREATE TABLE restricted_zones (
    id BIGSERIAL PRIMARY KEY,
    region VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
);