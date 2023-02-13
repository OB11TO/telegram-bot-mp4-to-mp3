--liquibase formatted sql

--changeset obito:1
ALTER TABLE uploaded_files
    ADD COLUMN quality_video INTEGER;