package org.springframework.samples.Owner;

import java.time.LocalDate;

import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.model.OwnerPet.Visit;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

public interface OwnerPublicAPI {
    void savePet(Integer id, String name, String type, Integer owner_id, LocalDate birthdate);
    void saveVisit(Integer id, LocalDate date, String description, Integer pet_id);
    Owner findById(int ownerId);
    void savePet(Pet pet);
    void saveOwner(Owner owner);
    Pet findPetById(Integer petId);
    PetType findPetTypeByName(String typeName);
    void deletePet(Integer petId);
    int countPetsByOwnerId(Integer ownerId);
}