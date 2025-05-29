package org.springframework.samples.Owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
			existingOwner.setFirstName(owner.getFirstName());
			existingOwner.setLastName(owner.getLastName());
			existingOwner.setAddress(owner.getAddress());
			existingOwner.setCity(owner.getCity());
			existingOwner.setTelephone(owner.getTelephone());
			repository.save(existingOwner);
			return existingOwner.getId();

		}

		return repository.save(owner).getId();
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
		return repository.findByName(firstName, lastName);
	}

	@ApplicationModuleListener
	void onNewPetEvent(SavePetEvent event) {
		petRepository.save(event.isNew(), new OwnerPet(event.getId(), event.getName(), event.getBirthDate(), event.getOwner_id(),
				event.getType()));

	}

	@ApplicationModuleListener
	void onAddVisitPet(AddVisitPet event) {
		petRepository.saveVisit(new OwnerPet.Visit(event.getId(),event.getDescription(),event.getDate(),event.getPet_id()));
	}


}
