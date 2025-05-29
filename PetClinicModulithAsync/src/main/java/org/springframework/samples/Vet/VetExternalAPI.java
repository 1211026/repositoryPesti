package org.springframework.samples.Vet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Vet.model.Vet;

import java.util.Collection;

public interface VetExternalAPI {

	Page<Vet> findAll(Pageable pageable);

	Collection<Vet> findAll();

}
