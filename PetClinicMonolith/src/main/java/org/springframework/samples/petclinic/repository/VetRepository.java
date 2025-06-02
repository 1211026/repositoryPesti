
package org.springframework.samples.petclinic.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


public interface VetRepository extends Repository<Vet, Integer> {


	@Transactional(readOnly = true)
	@Cacheable("vets")
	Collection<Vet> findAll() throws DataAccessException;

	@Transactional(readOnly = true)
	@Cacheable("vets")
	Page<Vet> findAll(Pageable pageable) throws DataAccessException;

}
