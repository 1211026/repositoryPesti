package org.springframework.samples.Pet.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PetRepositoryImpl implements PetRepository {

	private final JdbcClient jdbcClient;

	public PetRepositoryImpl(@Qualifier("petJdbcClient") JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public List<Pet> findPetByOwnerId(Integer id) {
		return jdbcClient.sql("SELECT * FROM pets WHERE owner_id = ?")
				.param(id)
				.query(Pet.class)
				.list();
	}

	@Override
	public Optional<Pet> findPetByName(String name) {
		return jdbcClient.sql("SELECT * FROM pets WHERE name = ?")
				.param(name)
				.query(Pet.class)
				.optional();
	}

	@Override
	public Pet findById(Integer id) {
		return jdbcClient.sql("SELECT * FROM pets WHERE id = ?")
				.param(id)
				.query(Pet.class)
				.single();
	}

	@Override
	public void save(Pet pet, boolean isNew) {
		if (isNew) {
			jdbcClient.sql("INSERT INTO pets (name, owner_id, type_id,birth_date ) VALUES (?, ?, ?, ?)")
					.params(pet.getName(), pet.getOwner_id(), pet.getType().getId(), pet.getBirthDate())
					.update();
		} else {
			jdbcClient.sql("UPDATE pets SET name = ?, owner_id = ?, type_id = ?, birth_date = ? WHERE id = ?")
					.params(pet.getName(), pet.getOwner_id(), pet.getType().getId(), pet.getBirthDate(), pet.getId())
					.update();
		}
	}

	@Override
	public void save(Pet.Visit petVisit) {
		jdbcClient.sql("INSERT INTO pet_visit (pet_id, visit_date, description) VALUES (?, ?, ?)")
				.params(petVisit.pet_id(), petVisit.visit_date(), petVisit.description())
				.update();

	}
}
