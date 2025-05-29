package org.springframework.samples.Owner;

import java.time.LocalDate;

public interface OwnerPublicAPI {

    void savePet(Integer id, String name, String type, Integer owner_id, LocalDate birthdate);

    void saveVisit(Integer id, LocalDate date, String description, Integer pet_id);

}
