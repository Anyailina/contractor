--liquibase formatted sql
--changeset annill:create_table_org_form


CREATE TABLE IF NOT EXISTS org_form
(
    id        serial PRIMARY KEY NOT NULL,
    name      text               NOT NULL,
    is_active boolean            NOT NULL DEFAULT TRUE
);