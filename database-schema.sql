-- Spring Boot creates/updates this table automatically through JPA (ddl-auto=update).
-- This SQL is included for reference; run it only if you want to create the database manually.
CREATE DATABASE IF NOT EXISTS diagnostic_lab;
USE diagnostic_lab;

CREATE TABLE IF NOT EXISTS lab_state (
    id BIGINT NOT NULL PRIMARY KEY,
    payload LONGTEXT NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);
