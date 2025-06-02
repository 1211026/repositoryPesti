package org.springframework.samples.Visit.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.service.VisitRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public class VisitRepositoryImpl implements VisitRepository {

	private final JdbcClient jdbcClient;

	public VisitRepositoryImpl(@Qualifier("visitJdbcClient") JdbcClient jdbcClient){
		this.jdbcClient = jdbcClient;
	}

	@Override
	public void save(Visit visit) {
		jdbcClient.sql("INSERT INTO visits (visit_date, description, pet_id) VALUES (?, ?, ?)")
				.params(visit.getDate(), visit.getDescription(), visit.getPet_id())
				.update();
	}

	@Override
	public Set<Visit> findVisitByPetId(Integer id) {
		return jdbcClient.sql("SELECT * FROM visits WHERE pet_id = ?")
				.param(id)
				.query(Visit.class)
				.set();
	}

	@Override
	public List<Visit> findAll() {
		return jdbcClient.sql("SELECT * FROM visits")
				.query(Visit.class)
				.list();
	}

	@Override
	public Visit findById(Integer id){
		return jdbcClient.sql("SELECT * FROM visits WHERE id = ?")
				.param(id)
				.query(Visit.class)
				.optional()
				.orElse(null);
	}

}
