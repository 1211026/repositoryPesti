package org.springframework.samples.Pet;

import java.time.LocalDate;

import org.springframework.samples.Pet.model.Pet;

public interface PetPublicAPI {

    void saveVisit(Integer id, LocalDate date, String description, Integer pet_id);

	Pet findById(int petId);

}
