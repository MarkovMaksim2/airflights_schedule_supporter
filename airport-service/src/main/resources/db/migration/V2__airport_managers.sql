CREATE TABLE airport_managers (
    id BIGSERIAL PRIMARY KEY,
    airport_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL UNIQUE
);
