package org.springframework.samples.Vet.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Vet.model.Vet;

import java.util.Collection;
import java.util.Optional;

public interface VetRepository {

    Collection<Vet> findAll() throws DataAccessException;

    Page<Vet> findAll(Pageable pageable) throws DataAccessException;

    Optional<Vet> findById(Integer id);

}

