package org.springframework.samples.Owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.OwnerPublicAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OwnerService implements OwnerExternalAPI, OwnerPublicAPI {

    private final OwnerRepository repository;
    private final OwnerPetRepository petRepository;

    @Override
    public Owner findById(Integer id) {
        Owner owner = repository.findById(id);
        List<OwnerPet> pets = petRepository.findPetByOwnerId(id);
        for (OwnerPet pet: pets){
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
        Owner newOwner = createOwner(owner);

        return newOwner.getId();
    }

    private Owner createOwner(Owner owner) {
        repository.save(owner);
        return owner;
    }

    @Override
    public Page<Owner> findByLastName(String lastname, Pageable pageable) {
        Page<Owner> pageOwner = repository.findByLastName(lastname,pageable);
        List<Owner> ownerList = new ArrayList<>(pageOwner.getContent());
        for(Owner owner: ownerList) {
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
        return repository.findByName(firstName,lastName);
    }

    @Override
    public void savePet(Integer id, String name, String type, Integer owner_id, LocalDate birthdate) {
        Optional<OwnerPet> existingPet = petRepository.findById(id);
        if (existingPet.isEmpty()) {
            petRepository.save(new OwnerPet(id, name, birthdate, owner_id, type));
        }else {
            existingPet.get().setName(name);
            existingPet.get().setBirthDate(birthdate);
            existingPet.get().setType_name(type);
            petRepository.save(existingPet.get());
        }
    }

    @Override
    public void saveVisit(Integer id, LocalDate date, String description, Integer pet_id) {
        petRepository.save(new OwnerPet.Visit(id,description,date,pet_id));
    }
}