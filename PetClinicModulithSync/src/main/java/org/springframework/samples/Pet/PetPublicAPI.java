package org.springframework.samples.Pet;

import java.time.LocalDate;

public interface PetPublicAPI {

    void saveVisit(Integer id, LocalDate date, String description, Integer pet_id);

}
