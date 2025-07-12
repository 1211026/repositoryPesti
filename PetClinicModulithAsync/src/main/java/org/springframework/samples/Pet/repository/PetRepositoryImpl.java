package org.springframework.samples.Pet.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class PetRepositoryImpl implements PetRepository {

	private final JdbcClient jdbcClient;
	
	private final JdbcTemplate jdbcTemplate;

	public PetRepositoryImpl(@Qualifier("petJdbcClient") JdbcClient jdbcClient,
	                         @Qualifier("petJdbcTemplate")JdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
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
	    System.out.println("PetRepositoryImpl.save: Iniciando salvamento de pet: name=" + pet.getName() + 
	                      ", owner_id=" + pet.getOwner_id() + 
	                      ", isNew=" + isNew);
	    try {
	        if (isNew) {
	            // Implementação original para inserir um novo pet
	            KeyHolder keyHolder = new GeneratedKeyHolder();
	            
	            // Usar JdbcTemplate em vez de JdbcClient para poder usar KeyHolder
	            jdbcTemplate.update(
	                connection -> {
	                    PreparedStatement ps = connection.prepareStatement(
	                        "INSERT INTO pets (name, owner_id, type_id, birth_date) VALUES (?, ?, ?, ?)",
	                        Statement.RETURN_GENERATED_KEYS
	                    );
	                    ps.setString(1, pet.getName());
	                    ps.setInt(2, pet.getOwner_id());
	                    ps.setInt(3, pet.getType().getId());
	                    
	                    // Converter birthDate para java.sql.Date se necessário
	                    if (pet.getBirthDate() instanceof LocalDate) {
	                        ps.setDate(4, java.sql.Date.valueOf((LocalDate) pet.getBirthDate()));
	                    } else {
	                        ps.setObject(4, pet.getBirthDate());
	                    }
	                    
	                    return ps;
	                },
	                keyHolder
	            );
	            
	            // Obter o ID gerado e atribuí-lo ao pet
	            Number key = keyHolder.getKey();
	            if (key != null) {
	                pet.setId(key.intValue());
	                System.out.println("Pet salvo com ID: " + pet.getId());
	            } else {
	                System.err.println("Não foi possível obter o ID gerado para o pet");
	            }
	        } else {
	            // Implementação original para atualizar um pet existente
	            jdbcTemplate.update(
	                "UPDATE pets SET name = ?, owner_id = ?, type_id = ?, birth_date = ? WHERE id = ?",
	                pet.getName(), 
	                pet.getOwner_id(), 
	                pet.getType().getId(), 
	                pet.getBirthDate() instanceof LocalDate ? 
	                    java.sql.Date.valueOf((LocalDate) pet.getBirthDate()) : 
	                    pet.getBirthDate(),
	                pet.getId()
	            );
	            System.out.println("Pet atualizado com ID: " + pet.getId());
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao salvar pet no repositório: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }
	}

	@Override
	public void save(Pet.Visit petVisit) {
		jdbcClient.sql("INSERT INTO pet_visit (pet_id, visit_date, description) VALUES (?, ?, ?)")
				.params(petVisit.pet_id(), petVisit.visit_date(), petVisit.description())
				.update();

	}
	
	@Override
	public Pet findById(int petId) {
	    return findById(Integer.valueOf(petId));
	}
}
