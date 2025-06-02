package org.springframework.samples.Pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.Owner.OwnerPublicAPI;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.PetPublicAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetService implements PetExternalAPI, PetPublicAPI {

    private final PetRepository petRepository;
    private final PetTypeRepository petTypeRepository;
    private final OwnerPublicAPI ownerPublicAPI;
    @Override
    public Collection<PetType> findPetTypes() {
        return petTypeRepository.findPetTypes();
    }

    @Override
    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId);
    }

    @Override
    public Optional<Pet> getPetByName(String name, boolean isNew) {
        return petRepository.findPetByName(name);
    }

    @Override
    public void save(Pet pet) {
        boolean isNew = (pet.getId() == null);
        petRepository.save(pet, isNew);
        ownerPublicAPI.savePet(pet.getId(), pet.getName(), pet.getType().getName(), pet.getOwner_id(), pet.getBirthDate());
    }

    @Override
    public void saveVisit(Integer id, LocalDate date, String description, Integer pet_id) {
        petRepository.save(new Pet.Visit(id,description,date,pet_id));
    }
}