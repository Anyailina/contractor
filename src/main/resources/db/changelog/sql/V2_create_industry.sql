--liquibase formatted sql
--changeset annill:create_table_industry


CREATE TABLE IF NOT EXISTS industry
(
    id        serial PRIMARY KEY NOT NULL,
    name      text               NOT NULL,
    is_active boolean            NOT NULL DEFAULT TRUE
);