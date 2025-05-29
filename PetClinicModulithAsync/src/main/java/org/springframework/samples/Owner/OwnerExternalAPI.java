package org.springframework.samples.Owner;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;

import java.util.List;
import java.util.Optional;

public interface OwnerExternalAPI {

	Owner findById(Integer id);

	Integer save(Owner owner);

	Page<Owner> findByLastName(String lastname, Pageable pageable);

	List<OwnerPet> findPetByOwner(Integer owner_id);

	Optional<Owner> findByName(String firstName, String lastName);

}
