package org.springframework.samples.Owner.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.stereotype.Repository;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public class OwnerPetRepositoryImpl implements OwnerPetRepository {

	private final JdbcClient jdbcClient;
	
	private final JdbcTemplate jdbcTemplate;

	public OwnerPetRepositoryImpl(@Qualifier("ownerJdbcClient") JdbcClient jdbcClient,
	                              @Qualifier("ownerJdbcTemplate") JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
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
	    System.out.println("OwnerPetRepositoryImpl.save: Iniciando salvamento de pet: id=" + pet.getId() + 
	                      ", name=" + pet.getName() + 
	                      ", owner_id=" + pet.getOwner_id() + 
	                      ", isNew=" + isNew);
	    try {
	        if (isNew) {
	            // Implementação original para inserir um novo pet
	            KeyHolder keyHolder = new GeneratedKeyHolder();
	            
	            jdbcTemplate.update(
	                connection -> {
	                    PreparedStatement ps = connection.prepareStatement(
	                        "INSERT INTO owner_pets (name, birth_date, owner_id, type_name) VALUES (?, ?, ?, ?)",
	                        Statement.RETURN_GENERATED_KEYS
	                    );
	                    ps.setString(1, pet.getName());
	                    
	                    // Converter birthDate para java.sql.Date se necessário
	                    if (pet.getBirthDate() instanceof LocalDate) {
	                        ps.setDate(2, java.sql.Date.valueOf((LocalDate) pet.getBirthDate()));
	                    } else {
	                        ps.setObject(2, pet.getBirthDate());
	                    }
	                    
	                    ps.setInt(3, pet.getOwner_id());
	                    ps.setString(4, pet.getType_name());
	                    
	                    return ps;
	                },
	                keyHolder
	            );
	            
	            // Obter o ID gerado e atribuí-lo ao pet
	            Number key = keyHolder.getKey();
	            if (key != null) {
	                pet.setId(key.intValue());
	                System.out.println("OwnerPet salvo com ID: " + pet.getId());
	            } else {
	                System.err.println("Não foi possível obter o ID gerado para o OwnerPet");
	            }
	        } else {
	            // Implementação original para atualizar um pet existente
	            jdbcTemplate.update(
	                "UPDATE owner_pets SET name = ?, birth_date = ?, owner_id = ?, type_name = ? WHERE id = ?",
	                pet.getName(),
	                pet.getBirthDate() instanceof LocalDate ? 
	                    java.sql.Date.valueOf((LocalDate) pet.getBirthDate()) : pet.getBirthDate(),
	                pet.getOwner_id(),
	                pet.getType_name(),
	                pet.getId()
	            );
	            System.out.println("OwnerPet atualizado com ID: " + pet.getId());
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao salvar OwnerPet no repositório: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
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
