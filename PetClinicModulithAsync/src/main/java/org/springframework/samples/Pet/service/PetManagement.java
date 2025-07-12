package org.springframework.samples.Pet.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.samples.notifications.SavePetEvent;
import org.springframework.samples.notifications.AddVisitPet;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

import org.springframework.samples.notifications.AddVisitEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PetManagement implements PetExternalAPI {

	private final PetRepository petRepository;

	private final PetTypeRepository petTypeRepository;

	private final ApplicationEventPublisher events;
	
	@Autowired
	public PetManagement(PetRepository petRepository, PetTypeRepository petTypeRepository, ApplicationEventPublisher events) {
		this.petRepository = petRepository;
		this.petTypeRepository = petTypeRepository;
		this.events = events;
	}

	@Override
	public Collection<PetType> findPetTypes() {
		return petTypeRepository.findPetTypes();
	}

	@Override
	public Pet getPetById(Integer petId) {
		return petRepository.findById(petId);
	}

	@Override
	public Optional<Pet> getPetByName(String name, boolean isNew) {
		return petRepository.findPetByName(name);
	}

	@Override
	@Transactional
	public void save(Pet pet) {
	    System.out.println("Iniciando salvamento de pet: name=" + pet.getName() + 
	                      ", owner_id=" + pet.getOwner_id());
	    
	    boolean isNew = (pet.getId() == null);
	    
	    try {
	        // Salvar o pet no repositório local
	        petRepository.save(pet, isNew);  // Método retorna void
	        System.out.println("Pet salvo no repositório local: id=" + pet.getId());
	        
	        // Publicar evento para notificar outros módulos
	        Integer petId = pet.getId();
	        String petName = pet.getName();
	        LocalDate birthDate = pet.getBirthDate();
	        Integer typeId = pet.getType() != null ? pet.getType().getId() : null;
	        String typeName = pet.getType() != null ? pet.getType().getName() : null;
	        Integer ownerId = pet.getOwner_id();
	        
	        SavePetEvent event = new SavePetEvent(petId, petName, birthDate, typeId, typeName, ownerId, isNew);
	        System.out.println("Publicando evento SavePetEvent: id=" + petId + 
	                          ", name=" + petName + 
	                          ", owner_id=" + ownerId);
	        events.publishEvent(event);
	        System.out.println("Evento SavePetEvent publicado com sucesso");
	    } catch (Exception e) {
	        System.err.println("Erro ao salvar pet ou publicar evento: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }
	}

	@ApplicationModuleListener
	void onNewVisitEvent(AddVisitEvent event) {
		petRepository.save(new Pet.Visit(event.getId(), event.getDescription(), event.getDate(), event.getPet_id()));
		events.publishEvent(new AddVisitPet(event.getId(), event.getDate(), event.getDescription(), event.getPet_id()));
	}
	
	@Override
	public Pet findById(int petId) {
	    return petRepository.findById(petId);
	}
}
