package org.springframework.samples.Owner.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.stereotype.Repository;
import org.springframework.samples.Owner.model.OwnerPet;

import java.util.List;
import java.util.Set;

@Repository
public class OwnerPetRepositoryImpl implements OwnerPetRepository {

	private final JdbcClient jdbcClient;

	public OwnerPetRepositoryImpl(@Qualifier("ownerJdbcClient") JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public List<OwnerPet> findPetByOwnerId(Integer id) {
		return jdbcClient.sql("SELECT * FROM owner_pets WHERE owner_id = ?")
				.param(id)
				.query(OwnerPet.class)
				.list();
	}

	@Override
	public OwnerPet findById(Integer id) {
		return jdbcClient.sql("SELECT * FROM owner_pets WHERE id = ?")
				.param(id)
				.query(OwnerPet.class)
				.single();
	}

	@Override
	public void save(boolean isNew, OwnerPet pet) {
		if (isNew) {
			jdbcClient.sql("INSERT INTO owner_pets (name, birth_date, owner_id, type_name) VALUES (?, ?, ?, ?)")
					.params(pet.getName(), pet.getBirthDate(), pet.getOwner_id(), pet.getType_name())
					.update();
		} else {
			jdbcClient.sql("UPDATE owner_pets SET name = ?, birth_date = ?, owner_id = ?, type_name = ? WHERE id = ?")
					.params(pet.getName(), pet.getBirthDate(), pet.getOwner_id(), pet.getType_name(), pet.getId())
					.update();
		}
	}


	@Override
	public void saveVisit(OwnerPet.Visit visit) {
		if (visit.id() == null) {
			jdbcClient.sql("INSERT INTO owner_visits (pet_id, visit_date, description) VALUES (?, ?, ?)")
					.params(visit.pet_id(), visit.visit_date(), visit.description())
					.update();
		} else {
			jdbcClient.sql("UPDATE owner_visits SET pet_id = ?, visit_date = ?, description = ? WHERE id = ?")
					.params(visit.pet_id(), visit.visit_date(), visit.description(), visit.id())
					.update();
		}
	}

	@Override
	public Set<OwnerPet.Visit> findVisitByPetId(Integer id) {
		return jdbcClient.sql("SELECT * FROM owner_visits WHERE pet_id = ?")
				.param(id)
				.query(OwnerPet.Visit.class)
				.set();
	}
}
