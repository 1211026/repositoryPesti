package org.springframework.samples.Owner.service;

import org.springframework.samples.Owner.model.OwnerPet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OwnerPetRepository {

    List<OwnerPet> findPetByOwnerId(Integer id);

    Optional<OwnerPet> findById(Integer id);

    void save(OwnerPet pet);

    void save(OwnerPet.Visit visit);

    Set<OwnerPet.Visit> findVisitByPetId(Integer id);
}
