CREATE TABLE restricted_zones (
    id BIGSERIAL PRIMARY KEY,
    region VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
);