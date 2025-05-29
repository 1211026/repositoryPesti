package org.springframework.samples.Pet.service;

import org.springframework.samples.Pet.model.Pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    List<Pet> findPetByOwnerId(Integer id);

    Optional<Pet> findPetByName(String name);

    Pet findById(Integer id);

    void save(Pet pet, boolean isNew);

    void save(Pet.Visit petVisit);

}
