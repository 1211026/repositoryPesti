package org.springframework.samples;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.Test;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Vet.model.Specialty;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Visit.model.Visit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitaryTests {


    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testOwnerSetId() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals(10, owner.getId());
    }

    @Test
    public void testOwnerFirstNameNotBlank() {
        Owner owner = new Owner(10, "", "Doe", "123 Main St", "Springfield", "1234567890", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerSetFirstName() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals("John", owner.getFirstName());
    }

    @Test
    public void testOwnerLastNameNotBlank() {
        Owner owner = new Owner(10, "John", "", "123 Main St", "Springfield", "1234567890", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerSetLastName() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals("Doe", owner.getLastName());
    }

    @Test
    public void testOwnerAddressNotBlank() {
        Owner owner = new Owner(10, "John", "Doe", "", "Springfield", "1234567890", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerSetAddress() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals("123 Main St", owner.getAddress());
    }

    @Test
    public void testOwnerCityNotBlank() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "", "1234567890", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerSetCity() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals("Springfield", owner.getCity());
    }

    @Test
    public void testOwnerTelephoneNotBlank() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

        assertEquals(2, violations.size());
    }

    @Test
    public void testOwnerSetTelephone() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);
        assertEquals("1234567890", owner.getTelephone());
    }

    @Test
    public void testOwnerTelephoneDigits() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "1234567890", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertEquals(0, violations.size());
    }

    @Test
    public void testOwnerTelephoneDigitsInvalid() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "5551234455665", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerTelephoneNotAlphanumeric() {
        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "123456789@", null);

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);

        long numberOfTelephoneViolations = violations.stream()
                .filter(violation -> violation.getPropertyPath().toString().equals("telephone"))
                .count();

        assertEquals(1, numberOfTelephoneViolations);
    }

    @Test
    public void testOwnerSetAndGetPets() {
        OwnerPet pet1 = new OwnerPet();
        pet1.setName("Buddy");

        OwnerPet pet2 = new OwnerPet();
        pet2.setName("Max");

        List<OwnerPet> pets = new ArrayList<>();
        pets.add(pet1);
        pets.add(pet2);

        Owner owner = new Owner(10, "John", "Doe", "123 Main St", "Springfield", "123456789@", pets);

        assertEquals(2, owner.getPets().size());
        assertEquals("Buddy", owner.getPets().get(0).getName());
        assertEquals("Max", owner.getPets().get(1).getName());
    }

    @Test
    public void testOwnerPetNameNotNull() {
        OwnerPet pet = new OwnerPet(null, null, LocalDate.now(), 1, "type");

        Set<ConstraintViolation<OwnerPet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerPetSetName() {
        OwnerPet pet = new OwnerPet(null, "Buddy", LocalDate.now(), 1, "type");
        assertEquals("Buddy", pet.getName());
    }

    @Test
    public void testOwnerPetSetBirthDate() {
        LocalDate birthDate = LocalDate.of(2020, 1, 1);
        OwnerPet pet = new OwnerPet(null, "Buddy", birthDate, 1, "type");
        assertEquals(birthDate, pet.getBirthDate());
    }

    @Test
    public void testOwnerPetSetBirthDateNotNull() {
        OwnerPet pet = new OwnerPet(null, "Buddy", null, 1, "type");

        Set<ConstraintViolation<OwnerPet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerPetSetOwner_idNotNull() {
        OwnerPet pet = new OwnerPet(null, "Buddy", LocalDate.now(), null, "type");

        Set<ConstraintViolation<OwnerPet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerPetSetOwner_id() {
        OwnerPet pet = new OwnerPet(null, "Buddy", LocalDate.now(), 10, "type");
        assertEquals(10, pet.getOwner_id());
    }

    @Test
    public void testOwnerPetSetType() {
        OwnerPet pet = new OwnerPet(null, "Buddy", LocalDate.now(), 1, "Dog");
        assertEquals("Dog", pet.getType_name());
    }

    @Test
    public void testOwnerPetSetTypeNotNull() {
        OwnerPet pet = new OwnerPet(null, "Buddy", LocalDate.now(), 1, null);

        Set<ConstraintViolation<OwnerPet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testOwnerPetSetVisits() {
        OwnerPet.Visit visit1 = new OwnerPet.Visit(5,"test",LocalDate.now(),11);
        Set<OwnerPet.Visit> visits = new HashSet<>();
        visits.add(visit1);
        OwnerPet pet = new OwnerPet(11, "Buddy", LocalDate.now(), 1, "type");
        pet.setVisits(visits);
        assertEquals(1, pet.getVisits().size());
    }

    @Test
    public void testPetNameNotNull() {
        Pet pet = new Pet(null, null, LocalDate.now(), new PetType("type"), 1);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPetSetName() {
        Pet pet = new Pet(null, "Buddy", LocalDate.now(), new PetType("type"), 1);
        assertEquals("Buddy", pet.getName());
    }

    @Test
    public void testPetSetBirthDate() {
        LocalDate birthDate = LocalDate.of(2020, 1, 1);
        Pet pet = new Pet(null, "Buddy", birthDate, new PetType("type"), 1);
        assertEquals(birthDate, pet.getBirthDate());
    }

    @Test
    public void testPetSetBirthDateNotNull() {
        Pet pet = new Pet(null, "Buddy", null, new PetType("type"), 1);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPetSetOwnerIdNotNull() {
        Pet pet = new Pet(null, "Buddy", LocalDate.now(), null, 1);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPetSetOwnerId() {
        Pet pet = new Pet(null, "Buddy", LocalDate.now(), new PetType("Dog"), 10);
        assertEquals(10, pet.getOwner_id());
    }

    @Test
    public void testPetSetType() {
        Pet pet = new Pet(null, "Buddy", LocalDate.now(), new PetType("Dog"), 1);
        assertEquals("Dog", pet.getType().getName());
    }

    @Test
    public void testPetSetTypeNotNull() {
        Pet pet = new Pet(null, "Buddy", LocalDate.now(), null, 1);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPetTypeNameNotNull() {
        PetType petType = new PetType(null);
        Set<ConstraintViolation<PetType>> violations = validator.validate(petType);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPetTypeSetName() {
        PetType petType = new PetType("Dog");
        assertEquals("Dog", petType.getName());
    }

    @Test
    public void testVetFirstNameNotBlank() {
        Vet vet = new Vet("", "Doe", null);
        Set<ConstraintViolation<Vet>> violations = validator.validate(vet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVetSetFirstName() {
        Vet vet = new Vet("John", "Doe", null);
        assertEquals("John", vet.getFirstName());
    }

    @Test
    public void testVetLastNameNotBlank() {
        Vet vet = new Vet("Doe", "", null);
        Set<ConstraintViolation<Vet>> violations = validator.validate(vet);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVetSetLastName() {
        Vet vet = new Vet("John", "Doe", null);
        assertEquals("Doe", vet.getLastName());
    }

    @Test
    public void testVetSpecialtiesNotEmpty() {
        Vet vet = new Vet();
        vet.addSpecialty(new Specialty("Dentistry"));
        assertEquals(1, vet.getSpecialties().size());
    }

    @Test
    public void testVetGetNrOfSpecialties() {
        Vet vet = new Vet();
        vet.addSpecialty(new Specialty("Dentistry"));
        vet.addSpecialty(new Specialty("Surgery"));
        assertEquals(2, vet.getNrOfSpecialties());
    }

    @Test
    public void testVisitSetDate() {
        Visit visit = new Visit(null, 2, LocalDate.now(), "Regular checkup");
        assertEquals(LocalDate.now(), visit.getDate());
    }

    @Test
    public void testVisitSetDateNotNull() {
        Visit visit = new Visit(null, 2, null, "Regular checkup");
        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVisitEnsureDescriptionAccepted() {
        Visit visit = new Visit(null, 2, LocalDate.now(), "Regular checkup");
        assertEquals("Regular checkup", visit.getDescription());
    }

    @Test
    public void testVisitSetDescriptionNotNull() {
        Visit visit = new Visit(null, 2, LocalDate.now(), null);
        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertEquals(2, violations.size());
    }

    @Test
    public void testVisitSetDescriptionNotBlank() {
        Visit visit = new Visit(null, 2, LocalDate.now(), "");
        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVisitEnsurePetIdAccepted() {
        Visit visit = new Visit(null, 2, LocalDate.now(), "Regular checkup");
        assertEquals(2, visit.getPet_id());
    }

    @Test
    public void testVisitSetPetIdNotNull() {
        Visit visit = new Visit(null, null, LocalDate.now(), "Regular checkup");
        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertEquals(1, violations.size());
    }
}
