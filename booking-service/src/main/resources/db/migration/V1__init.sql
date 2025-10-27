CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    booking_time TIMESTAMP NOT NULL
);