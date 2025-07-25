package org.springframework.samples.Pet.service;

import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.Pet.Visit;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    List<Pet> findPetByOwnerId(Integer id);

    Optional<Pet> findPetByName(String name);

    Pet findById(Integer id);


    void save(Pet.Visit petVisit);
    
    List<Pet> findAll();
    
    
    Pet findById(int petId);

	List<Visit> findVisitByPetId(Integer id);

	void save(Pet pet);

	void save(Pet pet, boolean isNew);

}