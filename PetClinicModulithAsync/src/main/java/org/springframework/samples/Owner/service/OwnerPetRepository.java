package org.springframework.samples.Owner.service;

import org.springframework.samples.Owner.model.OwnerPet;

import java.util.List;
import java.util.Set;

public interface OwnerPetRepository {

    List<OwnerPet> findPetByOwnerId(Integer id);

    OwnerPet findById(Integer id);

    void save(boolean isNew, OwnerPet pet);

    void saveVisit(OwnerPet.Visit visit);

    Set<OwnerPet.Visit> findVisitByPetId(Integer id);
}
