package org.annill.contractor.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.annill.contractor.ContractorSearch;
import org.annill.contractor.converter.ContractorConverter;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий контрагента
 *
 * @author anailina
 */
@Repository
@AllArgsConstructor
public class ContractorRepository {

    private static final String COUNT_BY_ID_SQL =
        "SELECT count(*) FROM contractor WHERE id = :id";

    private static final String SELECT_BY_ID_SQL =
        "SELECT * FROM contractor WHERE is_active = true AND id = :id";

    private static final String LOGICAL_DELETE_SQL =
        "UPDATE contractor SET is_active = false, modify_date = now() WHERE id = :id";

    private static final String UPDATE_CONTRACTOR_SQL = """
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

    private static final String INSERT_CONTRACTOR_SQL = """
        INSERT INTO contractor
        (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, create_date, is_active)
        VALUES
        (:id, :parent_id, :name, :name_full, :inn, :ogrn, :country, :industry, :orgForm, now(), true)""";

    private static final String SEARCH_BASE_SQL = """
        SELECT c.*
        FROM contractor c
        LEFT JOIN country co ON c.country = co.id AND co.is_active = true
        LEFT JOIN industry in ON c.industry = in.id AND in.is_active = true
        LEFT JOIN org_form o ON c.org_form = o.id AND o.is_active = true
        WHERE c.is_active = true""";

    private final ContractorConverter contractorConverter;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Contractor> contractorRowMapper = (rs, rowNum) ->
        Contractor.builder()
            .id(rs.getString("id"))
            .parentId(rs.getString("parent_id"))
            .name(rs.getString("name"))
            .nameFull(rs.getString("name_full"))
            .inn(rs.getString("inn"))
            .ogrn(rs.getString("ogrn"))
            .country(rs.getString("country"))
            .industry(rs.getInt("industry"))
            .orgForm(rs.getInt("org_form"))
            .isActive(rs.getBoolean("is_active"))
            .build();

    public void saveOrUpdate(ContractorDto contractorDto) {
        if (contractorDto == null || contractorDto.getId() == null) {
            throw new EntityNotFoundException();
        }

        Integer count = jdbcTemplate.queryForObject(COUNT_BY_ID_SQL,
            Map.of("id", contractorDto.getId()), Integer.class);

        Map<String, Object> params = new HashMap<>();
        params.put("id", contractorDto.getId());
        params.put("parent_id", contractorDto.getParentId());
        params.put("name", contractorDto.getName());
        params.put("name_full", contractorDto.getNameFull());
        params.put("inn", contractorDto.getInn());
        params.put("ogrn", contractorDto.getOgrn());
        params.put("country", contractorDto.getCountry());
        params.put("industry", contractorDto.getIndustry());
        params.put("orgForm", contractorDto.getOrgForm());

        if (count != null && count > 0) {
            jdbcTemplate.update(UPDATE_CONTRACTOR_SQL, params);
        } else {
            jdbcTemplate.update(INSERT_CONTRACTOR_SQL, params);
        }
    }

    public ContractorDto findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID контрагента не может быть пустым");
        }
        Contractor contractor = jdbcTemplate.queryForObject(
            SELECT_BY_ID_SQL,
            Map.of("id", id),
            contractorRowMapper
        );
        return Optional.ofNullable(contractor)
            .map(contractorConverter::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Контрагент с ID " + id + " не найден"));
    }

    public void logicalDelete(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID контрагента не может быть пустым");
        }
        jdbcTemplate.update(LOGICAL_DELETE_SQL, Map.of("id", id));
    }

    public List<ContractorDto> search(ContractorSearch contractorSearch) {
        if (contractorSearch == null) {
            return List.of();
        }

        StringBuilder sql = new StringBuilder(SEARCH_BASE_SQL);
        Map<String, Object> params = new HashMap<>();

        if (contractorSearch.getId() != null) {
            sql.append(" AND c.id = :contractorId");
            params.put("contractorId", contractorSearch.getId());
        }
        if (contractorSearch.getParentId() != null) {
            sql.append(" AND c.parent_id = :parentId");
            params.put("parentId", contractorSearch.getParentId());
        }
        if (contractorSearch.getSearchFilter() != null && !contractorSearch.getSearchFilter().isBlank()) {
            sql.append(
                " AND (c.name ILIKE :searchText OR c.name_full ILIKE :searchText OR c.inn ILIKE :searchText OR c.ogrn ILIKE :searchText)");
            params.put("searchText", "%" + contractorSearch.getSearchFilter() + "%");
        }
        if (contractorSearch.getCountry() != null && !contractorSearch.getCountry().isBlank()) {
            sql.append(" AND co.name ILIKE :country");
            params.put("country", "%" + contractorSearch.getCountry() + "%");
        }
        if (contractorSearch.getIndustry() != null) {
            sql.append(" AND c.industry = :industry");
            params.put("industry", contractorSearch.getIndustry());
        }
        if (contractorSearch.getOrgForm() != null && !contractorSearch.getOrgForm().isBlank()) {
            sql.append(" AND o.name ILIKE :orgForm");
            params.put("orgForm", "%" + contractorSearch.getOrgForm() + "%");
        }

        sql.append(" ORDER BY c.name LIMIT :limit OFFSET :offset");
        params.put("limit", contractorSearch.getLimit());
        params.put("offset", contractorSearch.getOffset());

        return jdbcTemplate.query(sql.toString(), params, contractorRowMapper)
            .stream()
            .map(contractorConverter::toDto)
            .collect(Collectors.toList());
    }

}
