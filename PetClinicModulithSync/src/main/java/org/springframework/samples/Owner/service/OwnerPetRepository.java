package org.springframework.samples.Owner.service;

import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Visit.model.Visit;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OwnerPetRepository {

    List<OwnerPet> findPetByOwnerId(Integer id);

    Optional<OwnerPet> findById(Integer id);

    void save(OwnerPet pet);

    void save(Visit visit);

    Set<org.springframework.samples.Owner.model.OwnerPet.Visit> findVisitByPetId(Integer id);
    
 // Adicione este método para corresponder ao que está sendo chamado em OwnerManagement
    void save(boolean isNew, OwnerPet pet);
    
    // Métodos adicionais
    void deletePet(Integer petId);
    
    int countPetsByOwnerId(Integer ownerId);
    
    List<OwnerPet> findAll();

	void save(org.springframework.samples.Owner.model.OwnerPet.Visit visit);
    
  
}