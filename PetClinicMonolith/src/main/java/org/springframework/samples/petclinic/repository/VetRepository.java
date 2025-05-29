
package org.springframework.samples.petclinic.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.transaction.annotation.Transactional;

import com.tngtech.archunit.thirdparty.com.google.common.base.Optional;

import java.util.Collection;


public interface VetRepository extends Repository<Vet, Integer> {


	@Transactional(readOnly = true)
	@Cacheable("vets")
	Collection<Vet> findAll() throws DataAccessException;

	@Query("SELECT vet FROM Vet vet")
	@Transactional(readOnly = true)
	Page<Vet> findAll(Pageable pageable);

	
	void save(Vet vet);
	
	Vet findById(Integer id);


}
