package org.springframework.samples.Owner.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.notifications.SavePetEvent;
import org.springframework.samples.notifications.AddVisitPet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OwnerManagement implements OwnerExternalAPI {

	private final OwnerRepository repository;

	private final OwnerPetRepository petRepository;

	
	@Autowired
	public OwnerManagement(OwnerRepository repository, OwnerPetRepository petRepository) {
		this.repository = repository;
		this.petRepository = petRepository;
	}
	
	@Override
	public Owner findById(Integer id) {
		Owner owner = repository.findById(id);
		List<OwnerPet> pets = petRepository.findPetByOwnerId(id);
		for (OwnerPet pet : pets) {
			Set<OwnerPet.Visit> visits = petRepository.findVisitByPetId(pet.getId());
			pet.setVisits(visits);
		}
		owner.setPets(pets);
		return owner;
	}
	
	@Override
	public Integer save(Owner owner) {
	    if (owner.getId() != null) {
	        
	        Owner existingOwner = repository.findById(owner.getId());
	        boolean isPhoneUpdate = !existingOwner.getTelephone().equals(owner.getTelephone());
	        existingOwner.setFirstName(owner.getFirstName());
	        existingOwner.setLastName(owner.getLastName());
	        existingOwner.setAddress(owner.getAddress());
	        existingOwner.setCity(owner.getCity());
	        existingOwner.setTelephone(owner.getTelephone());
	        repository.save(existingOwner);
	        
	        if (isPhoneUpdate) {
	            System.out.println("Telefone atualizado, mas evento não publicado (BUG INTENCIONAL)");
	        } else {
	            System.out.println("Owner atualizado e evento publicado normalmente");
	        }
	        
	        return existingOwner.getId();
	    }

	   
	    
	    Owner savedOwner = repository.save(owner);
	    return savedOwner.getId();
	}
	
	@Override
	public Page<Owner> findByLastName(String lastname, Pageable pageable) {
		Page<Owner> pageOwner = repository.findByLastName(lastname, pageable);
		List<Owner> ownerList = new ArrayList<>(pageOwner.getContent());
		for (Owner owner : ownerList) {
			List<OwnerPet> pets = petRepository.findPetByOwnerId(owner.getId());
			owner.setPets(pets);
		}
		return new PageImpl<>(ownerList, pageable, pageOwner.getTotalElements());
	}

	@Override
	public List<OwnerPet> findPetByOwner(Integer owner_id) {
		return petRepository.findPetByOwnerId(owner_id);
	}

	@Override
	public Optional<Owner> findByName(String firstName, String lastName) {
	    try {
	        if (firstName == null || lastName == null) {
	            System.out.println("findByName: firstName ou lastName é nulo");
	            return Optional.empty();
	        }
	        
	        System.out.println("Buscando owner com nome: " + firstName + " " + lastName);
	        Optional<Owner> owner = repository.findByName(firstName, lastName);
	        
	        if (owner.isPresent()) {
	            System.out.println("Owner encontrado: ID=" + owner.get().getId());
	            // Carregar os pets do owner para garantir consistência
	            Owner foundOwner = owner.get();
	            List<OwnerPet> pets = petRepository.findPetByOwnerId(foundOwner.getId());
	            foundOwner.setPets(pets);
	            return Optional.of(foundOwner);
	        } else {
	            System.out.println("Nenhum owner encontrado com nome: " + firstName + " " + lastName);
	            
	            // Tentar buscar usando findByLastName como fallback
	            Page<Owner> ownersPage = repository.findByLastName(lastName, PageRequest.of(0, 10));
	            if (!ownersPage.isEmpty()) {
	                Optional<Owner> matchingOwner = ownersPage.getContent().stream()
	                    .filter(o -> o.getFirstName().equals(firstName))
	                    .findFirst();
	                
	                if (matchingOwner.isPresent()) {
	                    System.out.println("Owner encontrado via fallback: ID=" + matchingOwner.get().getId());
	                    Owner foundOwner = matchingOwner.get();
	                    List<OwnerPet> pets = petRepository.findPetByOwnerId(foundOwner.getId());
	                    foundOwner.setPets(pets);
	                    return matchingOwner;
	                }
	            }
	            
	            return Optional.empty();
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao buscar owner por nome: " + e.getMessage());
	        e.printStackTrace();
	        return Optional.empty();
	    }
	}

	@ApplicationModuleListener
	void onNewPetEvent(SavePetEvent event) {
	    System.out.println("Recebido evento SavePetEvent: id=" + event.getId() + 
	                      ", name=" + event.getName() + 
	                      ", owner_id=" + event.getOwner_id() + 
	                      ", isNew=" + event.isNew());
	    try {
	        OwnerPet pet = new OwnerPet(event.getId(), event.getName(), event.getBirthDate(), 
	                                   event.getOwner_id(), event.getType());
	        petRepository.save(event.isNew(), pet);
	        System.out.println("Pet salvo com sucesso via evento: id=" + pet.getId() + 
	                          ", name=" + pet.getName());
	    } catch (Exception e) {
	        System.err.println("Erro ao salvar pet via evento: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	@ApplicationModuleListener
	void onAddVisitPet(AddVisitPet event) {
		petRepository.saveVisit(new OwnerPet.Visit(event.getId(),event.getDescription(),event.getDate(),event.getPet_id()));
	}


}
