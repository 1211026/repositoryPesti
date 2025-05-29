package org.springframework.samples.Pet.service;

import org.springframework.samples.Pet.model.PetType;

import java.util.List;

public interface PetTypeRepository {

    List<PetType> findPetTypes();

    PetType findById(Integer id);
}
