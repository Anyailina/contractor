package org.annill.contractor.repository;

import java.util.List;
import java.util.Map;
import org.annill.contractor.entity.Contractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Репизоторий контрагента
 */

@Repository
public class ContractorRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    /**
    *  Для преобразования строки результата SQL запроса в объект
    */
    private final RowMapper<Contractor> contractorRowMapper = (rs, rowNum) -> {
        Contractor c = new Contractor();
        c.setId(rs.getString("id"));
        c.setParentId(rs.getString("parent_id"));
        c.setName(rs.getString("name"));
        c.setNameFull(rs.getString("name_full"));
        c.setInn(rs.getString("inn"));
        c.setOgrn(rs.getString("ogrn"));
        c.setCountryId(rs.getString("country"));
        c.setIndustryId(rs.getInt("industry"));
        c.setOrgFormId(rs.getInt("org_form"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    };

    public ContractorRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOrUpdate(Contractor c) {
        String sqlSelect = "SELECT count(*) FROM contractor WHERE id = :id";
        Integer count = jdbcTemplate.queryForObject(sqlSelect, Map.of("id", c.getId()), Integer.class);
        Map<String, Object> params = Map.of(
            "id", c.getId(),
            "parentId", c.getParentId(),
            "name", c.getName(),
            "nameFull", c.getNameFull(),
            "inn", c.getInn(),
            "ogrn", c.getOgrn(),
            "country", c.getCountryId(),
            "industry", c.getIndustryId(),
            "orgForm", c.getOrgFormId()
        );
        if (count != null && count > 0) {
            String sqlUpdate = """
                UPDATE contractor
                SET parent_id = :parentId,
                    name = :name,
                    name_full = :nameFull,
                    inn = :inn,
                    ogrn = :ogrn,
                    country = :country,
                    industry = :industry,
                    org_form = :orgForm,
                    modify_date = now()
                WHERE id = :id
                """;
            jdbcTemplate.update(sqlUpdate, params);
        } else {
            String sqlInsert = """
                INSERT INTO contractor
                (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, create_date, is_active)
                VALUES
                (:id, :parentId, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, now(), true)
                """;
            jdbcTemplate.update(sqlInsert, params);
        }
    }

    public Contractor findById(String id) {
        String sql = "SELECT * FROM contractor  WHERE is_active = true AND id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), contractorRowMapper);
    }

    public void logicalDelete(String id) {
        String sql = "UPDATE contractor SET is_active = false, modify_date = now() WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public List<Contractor> search(String contractorId, String parentId, String searchText, String country,
        Integer industry, String orgForm, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
            "SELECT c.* " +
                "FROM contractor c " +
                "LEFT JOIN country co ON c.country = co.id AND co.is_active = true " +
                "LEFT JOIN industry in ON c.country = in.id AND in.is_active = true " +
                "LEFT JOIN org_form o ON c.org_form = o.id AND o.is_active = true " +
                "WHERE c.is_active = true "
        );

        var params = new java.util.HashMap<String, Object>();

        if (contractorId != null) {
            sql.append("AND c.id = :contractorId ");
            params.put("contractorId", contractorId);
        }
        if (parentId != null) {
            sql.append("AND c.parent_id = :parentId ");
            params.put("parentId", parentId);
        }
        if (searchText != null) {
            sql.append(
                "AND (c.name ILIKE :searchText OR c.name_full ILIKE :searchText OR c.inn ILIKE :searchText OR c.ogrn ILIKE :searchText) ");
            params.put("searchText", "%" + searchText + "%");
        }

        if (country != null) {
            sql.append("AND co.name ILIKE :country ");
            params.put("country", "%" + country + "%");
        }
        if (industry != null) {
            sql.append("AND c.industry = :industry ");
            params.put("industry", industry);
        }
        if (orgForm != null) {
            sql.append("AND o.name ILIKE :orgForm ");
            params.put("orgForm", "%" + orgForm + "%");
        }

        sql.append("ORDER BY c.name LIMIT :limit OFFSET :offset");
        params.put("limit", limit);
        params.put("offset", offset);

        return jdbcTemplate.query(sql.toString(), params, contractorRowMapper);
    }

}
