package org.springframework.samples;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.service.OwnerRepository;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Pet.service.PetRepository;
import org.springframework.samples.Pet.service.PetTypeRepository;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.service.VetRepository;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.service.VisitRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {

    @Autowired
    protected OwnerRepository owners;

    @Autowired
    protected PetRepository petRepository;

    @Autowired
    protected PetTypeRepository petTypeRepository;

    @Autowired
    protected VisitRepository visitRepository;

    @Autowired
    protected VetRepository vetRepository;

    @Test
    void shouldFindOwnersByLastName() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Owner> owners = this.owners.findByLastName("Davis", pageable);
        assertThat(owners).hasSize(2);

        owners = this.owners.findByLastName("Daviss", pageable);
        assertThat(owners).isEmpty();
    }

    @Test
    void shouldFindOwnerAndVerifyFirstName() {
        Owner owner = this.owners.findById(1);
        assertThat(owner).isNotNull();
        assertThat(owner.getFirstName()).isEqualTo("George");
    }


    @Test
    void shouldFindOwnerAndVerifyCity() {
        Owner owner = this.owners.findById(1);
        assertThat(owner).isNotNull();
        assertThat(owner.getCity()).isEqualTo("Madison");
    }

    @Test
    void shouldFindOwnerAndVerifyAddress() {
        Owner owner = this.owners.findById(1);
        assertThat(owner).isNotNull();
        assertThat(owner.getAddress()).isEqualTo("110 W. Liberty St.");
    }

    @Test
    void shouldFindOwnerAndVerifyTelephone() {
        Owner owner = this.owners.findById(1);
        assertThat(owner).isNotNull();
        assertThat(owner.getTelephone()).isEqualTo("6085551023");
    }


    @Test
    void shouldReturnNullWhenOwnerNotFound() {
        Owner owner = this.owners.findById(999);
        assertThat(owner).isNull();
    }


    @Test
    @Transactional
    @Order(1)
    void shouldInsertOwner() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Owner> owners = this.owners.findByLastName("Schultz", pageable);
        int found = (int) owners.getTotalElements();

        Owner owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Schultz");
        owner.setAddress("4, Evans Street");
        owner.setCity("Wollongong");
        owner.setTelephone("4444444444");
        this.owners.save(owner);
        assertThat(owner.getId().longValue()).isNotEqualTo(0);
        owners = this.owners.findByLastName("Schultz", pageable);
        assertThat(owners.getTotalElements()).isEqualTo(found + 1);
    }

    @Test
    void shouldCreatedOwnerAndVerifyFirstName() {
        Owner owner = this.owners.findById(11);
        assertThat(owner).isNotNull();
        assertThat(owner.getFirstName()).isEqualTo("Sam");
    }


    @Test
    void shouldCreatedOwnerAndVerifyCity() {
        Owner owner = this.owners.findById(11);
        assertThat(owner).isNotNull();
        assertThat(owner.getCity()).isEqualTo("Wollongong");
    }

    @Test
    void shouldCreatedOwnerAndVerifyAddress() {
        Owner owner = this.owners.findById(11);
        assertThat(owner).isNotNull();
        assertThat(owner.getAddress()).isEqualTo("4, Evans Street");
    }

    @Test
    void shouldCreatedOwnerAndVerifyTelephone() {
        Owner owner = this.owners.findById(11);
        assertThat(owner).isNotNull();
        assertThat(owner.getTelephone()).isEqualTo("4444444444");
    }



    @Test
    @Transactional
    @Order(2)
    void shouldUpdateOwner() {
        Owner owner = this.owners.findById(1);
        String oldLastName = owner.getLastName();
        String newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        this.owners.save(owner);
        assertThat(owner.getLastName()).isEqualTo(newLastName);
    }


    @Test
    void shouldFindSingleOwnerWithPet() {
        List<Pet> pets = this.petRepository.findPetByOwnerId(1);
        assertThat(pets.get(0).getName()).isEqualTo("Leo");
        assertThat(pets.get(0).getOwner_id()).isEqualTo(1);
    }

    @Test
    void shouldFindPetAndVerifyName() {
        Optional<Pet> petOpt = this.petRepository.findPetByName("Leo");
        assertThat(petOpt).isPresent();

        Pet pet = petOpt.get();
        assertThat(pet.getName()).isEqualTo("Leo");
    }

    @Test
    void shouldFindPetAndVerifyType() {
        Optional<Pet> petOpt = this.petRepository.findPetByName("Leo");
        petOpt.get().setType(new PetType("cat"));
        assertThat(petOpt).isPresent();

        Pet pet = petOpt.get();
        assertThat(pet.getType().getName()).isEqualTo("cat");
    }

    @Test
    void shouldFindPetAndVerifyOwnerId() {
        Optional<Pet> petOpt = this.petRepository.findPetByName("Leo");
        assertThat(petOpt).isPresent();

        Pet pet = petOpt.get();
        assertThat(pet.getOwner_id()).isEqualTo(1);
    }

    @Test
    void shouldFindPetAndVerifyBirthDate() {
        Optional<Pet> petOpt = this.petRepository.findPetByName("Leo");
        assertThat(petOpt).isPresent();

        Pet pet = petOpt.get();

        assertThat(pet.getBirthDate()).isEqualTo(LocalDate.of(2010,9, 7));
    }

    @Test
    void shouldFindPetAndVerifyID() {
        Optional<Pet> petOpt = this.petRepository.findPetByName("Leo");
        assertThat(petOpt).isPresent();

        Pet pet = petOpt.get();
        assertThat(pet.getId()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyListWhenFindingPetsByInvalidOwnerId() {
        List<Pet> pets = this.petRepository.findPetByOwnerId(999);
        assertThat(pets).isEmpty();
    }

    @Test
    @Transactional
    @Order(3)
    void shouldInsertPet() {
        List<Pet> pets = petRepository.findPetByOwnerId(1);
        int found = pets.size();

        Pet pet = new Pet();
        pet.setName("bowser");
        PetType petType = petTypeRepository.findById(1);
        pet.setType(petType);
        pet.setBirthDate(LocalDate.now());
        pet.setOwner_id(1);
        pets.add(pet);
        assertThat(pets.size()).isEqualTo(found + 1);

        this.petRepository.save(pet,true);

        pet = petRepository.findPetByName("bowser").get();
        assertThat(pet.getId()).isNotNull();
    }

    @Test
    void shouldCreatedPetAndVerifyID() {
        Pet pet = this.petRepository.findById(14);
        assertThat(pet).isNotNull();
        assertThat(pet.getId()).isEqualTo(14);
    }

    @Test
    void shouldCreatedPetAndVerifyName() {
        Pet pet = this.petRepository.findById(14);
        assertThat(pet).isNotNull();
        assertThat(pet.getName()).isEqualTo("bowser");
    }

    @Test
    void shouldCreatedPetAndVerifyBirthDate() {
        Pet pet = this.petRepository.findById(14);
        assertThat(pet).isNotNull();
        assertThat(pet.getBirthDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldCreatedPetAndVerifyOwnerID() {
        Pet pet = this.petRepository.findById(14);
        assertThat(pet).isNotNull();
        assertThat(pet.getOwner_id()).isEqualTo(1);
    }

    @Test
    @Transactional
    void shouldUpdatePetName() {
        PetType type = new PetType();
        type.setId(1);
        List<Pet> pets = petRepository.findPetByOwnerId(1);
        Pet newPet = pets.get(0);
        String oldName = newPet.getName();

        String newName = oldName + "X";
        newPet.setName(newName);
        newPet.setType(type);
        this.petRepository.save(newPet, false);

        List<Pet> pets1 = petRepository.findPetByOwnerId(1);
        assertThat(pets1.get(0).getName()).isEqualTo(newName);
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenUpdatingPetWithInvalidType() {
        List<Pet> pets = petRepository.findPetByOwnerId(1);
        Pet pet = pets.get(0);

        PetType invalidType = new PetType();
        invalidType.setId(999);

        pet.setType(invalidType);

        org.junit.jupiter.api.Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.petRepository.save(pet, false);
        });
    }


    @Test
    void shouldFindVetAndVerifyID() {
        Optional<Vet> vetOpt = this.vetRepository.findById(1);
        assertThat(vetOpt).isPresent();

        Vet vet = vetOpt.get();
        assertThat(vet.getId()).isEqualTo(1);
    }

    @Test
    void shouldFindVetAndVerifyFirstName() {
        Optional<Vet> vetOpt = this.vetRepository.findById(1);
        assertThat(vetOpt).isPresent();

        Vet vet = vetOpt.get();
        assertThat(vet.getFirstName()).isEqualTo("James");
    }

    @Test
    void shouldFindVetAndVerifyLastName() {
        Optional<Vet> vetOpt = this.vetRepository.findById(1);
        assertThat(vetOpt).isPresent();

        Vet vet = vetOpt.get();
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindVetAndVerifySpecialtyNull() {
        Optional<Vet> vetOpt = this.vetRepository.findById(1);
        assertThat(vetOpt).isPresent();

        Vet vet = vetOpt.get();
        assertThat(vet.getSpecialties()).isEmpty();
    }


    @Test
    void shouldFindVetAndVerifySpecialty() {
        Optional<Vet> vetOpt = this.vetRepository.findById(2);
        assertThat(vetOpt).isPresent();

        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        Set<Specialty> specialties = new HashSet<>();
        specialties.add(specialty);

        Vet vet = vetOpt.get();

        Set<String> actualSpecialties = vet.getSpecialties().stream()
                .map(Specialty::toString)
                .collect(Collectors.toSet());
        Set<String> expectedSpecialties = specialties.stream()
                .map(Specialty::toString)
                .collect(Collectors.toSet());

        assertThat(actualSpecialties).containsExactlyInAnyOrderElementsOf(expectedSpecialties);
    }

    @Test
    void testSerialization() {
        Vet vet = new Vet();
        vet.setFirstName("Zaphod");
        vet.setLastName("Beeblebrox");
        vet.setId(123);
        Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
        AssertionsForClassTypes.assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
        AssertionsForClassTypes.assertThat(other.getLastName()).isEqualTo(vet.getLastName());
        AssertionsForClassTypes.assertThat(other.getId()).isEqualTo(vet.getId());
    }

    @Test
    @Transactional
    @Order(4)
    void shouldAddNewVisitForPet() {

        Set<Visit> visits = visitRepository.findVisitByPetId(1);
        int found = visits.size();

        Visit visit = new Visit();
        visit.setDescription("test");
        visit.setPet_id(1);
        visit.setDate(LocalDate.now());
        visits.add(visit);
        this.visitRepository.save(visit);

        Set<Visit> visits2 = visitRepository.findVisitByPetId(1);

        assertThat(visits2)
                .hasSize(found + 1)
                .allMatch(value -> value.getId() != null);
    }

    @Test
    void shouldCreatedVisitAndVerifyID() {
        Visit visit = this.visitRepository.findById(5);
        assertThat(visit).isNotNull();
        assertThat(visit.getId()).isEqualTo(5);
    }

    @Test
    void shouldCreatedVisitAndVerifyDescription() {
        Visit visit = this.visitRepository.findById(5);
        assertThat(visit).isNotNull();
        assertThat(visit.getDescription()).isEqualTo("test");
    }

    @Test
    void shouldCreatedVisitAndVerifyDate() {
        Visit visit = this.visitRepository.findById(5);
        assertThat(visit).isNotNull();
        assertThat(visit.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldCreatedVisitAndVerifyPetID() {
        Visit visit = this.visitRepository.findById(5);
        assertThat(visit).isNotNull();
        assertThat(visit.getPet_id()).isEqualTo(1);
    }

    @Test
    void shouldFindVisitsByPetId() {
        Collection<Visit> visits = visitRepository.findVisitByPetId(7);

        assertThat(visits) //
                .hasSize(2) //
                .element(0)
                .extracting(Visit::getDate)
                .isNotNull();
    }

    @Test
    void shouldFindVisitAndVerifyDate() {
        Collection<Visit> visits = visitRepository.findVisitByPetId(7);
        assertThat(visits).isNotEmpty();

        Visit visit = visits.iterator().next();
        assertThat(visit.getDate()).isEqualTo(LocalDate.of(2024, 5, 18));
    }

    @Test
    void shouldFindVisitAndVerifyID() {
        Collection<Visit> visits = visitRepository.findVisitByPetId(7);
        assertThat(visits).isNotEmpty();

        Visit visit = visits.iterator().next();
        assertThat(visit.getId()).isEqualTo(1);
    }

    @Test
    void shouldFindVisitAndVerifyDescription() {
        Collection<Visit> visits = visitRepository.findVisitByPetId(7);
        assertThat(visits).isNotEmpty();

        Visit visit = visits.iterator().next();
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
    }

    @Test
    void shouldFindVisitAndVerifyPetID() {
        Collection<Visit> visits = visitRepository.findVisitByPetId(7);
        assertThat(visits).isNotEmpty();

        Visit visit = visits.iterator().next();
        assertThat(visit.getPet_id()).isEqualTo(7);
    }



}
