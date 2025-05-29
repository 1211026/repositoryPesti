package org.springframework.samples.Pet.service;

import lombok.RequiredArgsConstructor;

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

import java.util.*;

@Service
@RequiredArgsConstructor
public class PetManagement implements PetExternalAPI {

	private final PetRepository petRepository;

	private final PetTypeRepository petTypeRepository;

	private final ApplicationEventPublisher events;

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
		boolean isNew = (pet.getId() == null);
		petRepository.save(pet, isNew);
		events.publishEvent(new SavePetEvent(pet.getId(), pet.getName(), pet.getBirthDate(), pet.getType().getId(),
				pet.getType().getName(), pet.getOwner_id(), isNew));
	}

	@ApplicationModuleListener
	void onNewVisitEvent(AddVisitEvent event) {
		petRepository.save(new Pet.Visit(event.getId(), event.getDescription(), event.getDate(), event.getPet_id()));
		events.publishEvent(new AddVisitPet(event.getId(), event.getDate(), event.getDescription(), event.getPet_id()));
	}
}
