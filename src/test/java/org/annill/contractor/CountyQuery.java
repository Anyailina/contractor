package org.annill.contractor;

public class CountyQuery {


    public static final String FIND_BY_ID_QUERY = "SELECT * FROM country WHERE is_active = true AND id = :id";

    public static final String COUNT_BY_ID_QUERY = "SELECT count(*) FROM country WHERE is_active = true AND id = :id";

    public static final String UPDATE_QUERY = "UPDATE country SET name = :name WHERE id = :id";

    public static final String INSERT_QUERY = "INSERT INTO country (id, name, is_active) VALUES (:id, :name, true)";

    public static final String LOGICAL_DELETE_QUERY = "UPDATE country SET is_active = false WHERE id = :id";

}
