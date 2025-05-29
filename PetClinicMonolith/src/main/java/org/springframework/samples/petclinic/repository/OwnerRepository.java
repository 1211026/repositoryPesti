
package org.springframework.samples.petclinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface OwnerRepository extends Repository<Owner, Integer> {


	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();


	@Query("SELECT DISTINCT owner FROM Owner owner left join  owner.pets WHERE owner.lastName LIKE :lastName% ")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	@Transactional(readOnly = true)
	@EntityGraph(attributePaths = "pets")
	Owner findById(int id);



	void save(Owner owner);

	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	Page<Owner> findAll(Pageable pageable);
	
	@Query("SELECT o FROM Owner o JOIN o.pets p WHERE p.id = :petId")
	Owner findOwnerByPetId(@Param("petId") Integer petId);





	@Query("SELECT pet FROM Pet pet WHERE pet.id =:id")
	@Transactional(readOnly = true)
	Pet findPetById(@Param("id") Integer id);

	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	List<Owner> findAllOwners();
  // Método para obter todos os donos


	@Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
	List<Owner> findByLastNameRaw(@Param("lastName") String lastName);



	
	
	

}
