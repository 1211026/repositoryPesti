package org.springframework.samples.Owner.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Pet.model.PetType;

import java.util.Optional;

public interface OwnerRepository {

    Owner findById(Integer id);

    Page<Owner> findByLastName(String lastName, Pageable pageable);

    Owner save(Owner owner);

    Optional<Owner> findByName(String firstName, String lastName);

	Page<Owner> findAll(Pageable pageable);

	
}