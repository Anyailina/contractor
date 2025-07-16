package org.annill.contractor;

import java.util.HashMap;
import java.util.Map;
import org.annill.contractor.entity.Contractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContractorTestRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ContractorTestRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Contractor contractor) {
        String sql = """
            INSERT INTO contractor 
            (id, name, name_full, inn, ogrn, country, industry, org_form, create_date, is_active)
            VALUES 
            (:id, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, now(), true)
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", contractor.getId());
        params.put("name", contractor.getName());
        params.put("nameFull", contractor.getNameFull());
        params.put("inn", contractor.getInn());
        params.put("ogrn", contractor.getOgrn());
        params.put("country", contractor.getCountry());
        params.put("industry", contractor.getIndustry());
        params.put("orgForm", contractor.getOrgForm());

        jdbcTemplate.update(sql, params);
    }
    public void delete(String id) {
        String sql = """
            DELETE from contractor where id = :id
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        jdbcTemplate.update(sql, params);
    }

}
