CREATE SCHEMA IF NOT EXISTS file;

CREATE TABLE IF NOT EXISTS file.files (
    file_id VARCHAR(64) PRIMARY KEY,
    owner_id VARCHAR(255) NOT NULL,
    bucket VARCHAR(255) NOT NULL,
    object_key VARCHAR(1024) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
