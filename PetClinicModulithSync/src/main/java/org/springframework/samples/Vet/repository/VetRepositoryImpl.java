package org.springframework.samples.Vet.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.service.VetRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional
public class VetRepositoryImpl implements VetRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public VetRepositoryImpl(@Qualifier("vetDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<Vet> findAll() throws DataAccessException {
		return jdbcTemplate.query("SELECT * FROM vets", new VetRowMapper());
	}
	@Override
	public Optional<Vet> findById(Integer id) throws DataAccessException {
		String selectVetQuery = "SELECT * FROM vets WHERE id = ?";
		Vet vet = jdbcTemplate.queryForObject(selectVetQuery, new Object[]{id}, new VetRowMapper());

		if (vet != null) {
			vet.setSpecialties(findSpecialtiesByVetId(vet.getId()));
		}

		return Optional.ofNullable(vet);
	}
	@Override
	public Page<Vet> findAll(Pageable pageable) throws DataAccessException {
		String countQuery = "SELECT COUNT(*) FROM vets";
		int total = jdbcTemplate.queryForObject(countQuery, Integer.class);

		String selectVetsQuery = "SELECT * FROM vets LIMIT ? OFFSET ?";
		List<Vet> vets = jdbcTemplate.query(selectVetsQuery, new Object[]{pageable.getPageSize(), pageable.getOffset()}, new VetRowMapper());

		for (Vet vet : vets) {
			vet.setSpecialties(findSpecialtiesByVetId(vet.getId()));
		}

		return new PageImpl<>(vets, pageable, total);
	}

	private Set<Specialty> findSpecialtiesByVetId(Integer vetId) {
		String selectSpecialtiesQuery = "SELECT s.id, s.name FROM specialties s JOIN vet_specialties vs ON s.id = vs.specialty_id WHERE vs.vet_id = ?";
		return new HashSet<>(jdbcTemplate.query(selectSpecialtiesQuery, new Object[]{vetId}, new SpecialtyRowMapper()));
	}

	private static class VetRowMapper implements RowMapper<Vet> {
		@Override
		public Vet mapRow(ResultSet rs, int rowNum) throws SQLException {
			Vet vet = new Vet();
			vet.setId(rs.getInt("id"));
			vet.setFirstName(rs.getString("first_name"));
			vet.setLastName(rs.getString("last_name"));
			// Mapear outras propriedades, se necessário
			return vet;
		}
	}

	private static class SpecialtyRowMapper implements RowMapper<Specialty> {
		@Override
		public Specialty mapRow(ResultSet rs, int rowNum) throws SQLException {
			Specialty specialty = new Specialty();
			specialty.setId(rs.getInt("id"));
			specialty.setName(rs.getString("name"));
			return specialty;
		}
	}
}
