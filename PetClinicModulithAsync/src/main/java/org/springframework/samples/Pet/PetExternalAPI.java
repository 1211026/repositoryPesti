package org.springframework.samples.Pet;

import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import java.util.Collection;
import java.util.Optional;

public interface PetExternalAPI {

	Collection<PetType> findPetTypes();

	Pet getPetById(Integer petId);

	Optional<Pet> getPetByName(String name, boolean isNew);

	void save(Pet pet);

}
