package org.annill.contractor;

public class Query {
    public static final String COUNT_BY_ID_SQL =
        "SELECT count(*) FROM contractor WHERE id = :id";

    public static final String SELECT_BY_ID_SQL =
        "SELECT * FROM contractor WHERE is_active = true AND id = :id";

    public static final String LOGICAL_DELETE_SQL =
        "UPDATE contractor SET is_active = false, modify_date = now() WHERE id = :id";

    public static final String UPDATE_CONTRACTOR_SQL = """
        UPDATE contractor
        SET parent_id = :parent_id,
            name = :name,
            name_full = :name_full,
            inn = :inn,
            ogrn = :ogrn,
            country = :country,
            industry = :industry,
            org_form = :orgForm,
            modify_date = now(),
            is_active = true
        WHERE id = :id""";

    public static final String INSERT_CONTRACTOR_SQL = """
        INSERT INTO contractor
        (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, create_date, is_active)
        VALUES
        (:id, :parent_id, :name, :name_full, :inn, :ogrn, :country, :industry, :orgForm, now(), true)""";

    public static final String SEARCH_BASE_SQL = """
        SELECT c.*
        FROM contractor c
        LEFT JOIN country co ON c.country = co.id AND co.is_active = true
        LEFT JOIN industry in ON c.industry = in.id AND in.is_active = true
        LEFT JOIN org_form o ON c.org_form = o.id AND o.is_active = true
        WHERE c.is_active = true""";
}
