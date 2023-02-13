--liquibase formatted sql

--changeset obito:1
ALTER TABLE users_telegram_bot
    ADD COLUMN created_at TIMESTAMP;

ALTER TABLE users_telegram_bot
    ADD COLUMN modified_at TIMESTAMP;

--changeset obito:2
ALTER TABLE uploaded_files
    ADD COLUMN created_at TIMESTAMP;