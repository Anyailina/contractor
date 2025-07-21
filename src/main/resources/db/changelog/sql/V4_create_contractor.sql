--liquibase formatted sql
--changeset annill:create_table_deal_type


CREATE TABLE IF NOT EXISTS contractor
(
    id             varchar(12) PRIMARY KEY NOT NULL,
    parent_id      varchar(12),
    name           text                    NOT NULL,
    name_full      text,
    inn            text,
    ogrn           text,
    country        text,
    industry       int4,
    org_form       int4,
    create_date    timestamp               NOT NULL DEFAULT now(),
    modify_date    timestamp,
    create_user_id text,
    modify_user_id text,
    is_active      boolean                 NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_contractor_parent
        FOREIGN KEY (parent_id) REFERENCES contractor (id),
    CONSTRAINT fk_contractor_country
        FOREIGN KEY (country) REFERENCES country (id),
    CONSTRAINT fk_contractor_industry
        FOREIGN KEY (industry) REFERENCES industry (id),
    CONSTRAINT fk_contractor_org_form
        FOREIGN KEY (org_form) REFERENCES org_form (id)
);