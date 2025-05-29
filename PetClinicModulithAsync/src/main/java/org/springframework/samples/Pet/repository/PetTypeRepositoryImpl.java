package org.springframework.samples.Pet.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetTypeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PetTypeRepositoryImpl implements PetTypeRepository {

	private final JdbcClient jdbcClient;

	public PetTypeRepositoryImpl(@Qualifier("petJdbcClient") JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public List<PetType> findPetTypes() {
		return jdbcClient.sql("SELECT * FROM types ORDER BY name")
				.query(PetType.class)
				.list();
	}

	@Override
	public PetType findById(Integer id) {
		return jdbcClient.sql("SELECT * FROM types WHERE id = ?")
				.param(id)
				.query(PetType.class)
				.single();
	}
}
