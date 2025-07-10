--liquibase formatted sql
--changeset annill:create_table_contractor_country


CREATE TABLE IF NOT EXISTS country
(
    id        text PRIMARY KEY NOT NULL,
    name      text             NOT NULL,
    is_active boolean          NOT NULL DEFAULT TRUE
);
