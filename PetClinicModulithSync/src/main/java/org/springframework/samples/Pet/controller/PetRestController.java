package org.springframework.samples.Pet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Pet.PetExternalAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "pet-rest-controller")
public class PetRestController {

    private final PetExternalAPI petExternalAPI;
    
    private final OwnerExternalAPI owners;
    
  
    
    @Autowired
    public PetRestController(@Qualifier("petService") PetExternalAPI petExternalAPI, 
							 @Qualifier("ownerService") OwnerExternalAPI ownerExternalAPI) {
        this.petExternalAPI = petExternalAPI;
        this.owners = ownerExternalAPI;
    }

    @GetMapping("/pettypes")
    @Operation(summary = "Get all pet types")
    public ResponseEntity<Collection<PetType>> getPetTypes() {
        return new ResponseEntity<>(this.petExternalAPI.findPetTypes(), HttpStatus.OK);
    }

    @GetMapping("/pets/{petId}")
    @Operation(summary = "Get pet by ID")
    public ResponseEntity<Pet> getPet(@PathVariable("petId") int petId) {
        Pet pet = this.petExternalAPI.getPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @PostMapping("/owners/{ownerId}/pets")
    @Operation(summary = "Create a new pet for an owner")
    public ResponseEntity<OwnerPet> createPet(@PathVariable("ownerId") int ownerId, @Valid @RequestBody OwnerPet pet) {
        // 1. Verificar se o owner existe
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // 2. Definir o owner_id do pet
        pet.setOwner_id(ownerId);
        
        // 3. Adicionar o pet ao owner - Garantir que a lista de pets não é nula
        if (owner.getPets() == null) {
            owner.setPets(new ArrayList<>());
        }
        owner.addPet(pet);
        
        // 4. Salvar o owner
        Integer savedOwnerId = this.owners.save(owner);
        
        // 5. Buscar o owner atualizado
        Owner updatedOwner = this.owners.findById(savedOwnerId);
        
        // 6. Encontrar o pet recém-adicionado
        OwnerPet savedPet = null;
        if (updatedOwner.getPets() != null) {
            for (OwnerPet p : updatedOwner.getPets()) {
                if (p.getName().equals(pet.getName())) {
                    savedPet = p;
                    break;
                }
            }
        }
        
        // 7. Se não encontrou o pet, criar um Pet e salvá-lo diretamente
        if (savedPet == null) {
            System.out.println("Pet não encontrado na lista do owner após salvar. Salvando diretamente.");
            
            // Criar um Pet a partir do OwnerPet
            Pet newPet = new Pet();
            newPet.setName(pet.getName());
            newPet.setBirthDate((LocalDate) pet.getBirthDate());
            newPet.setOwner_id(ownerId);
            
            // Configurar o tipo do pet
            if (pet.getType_name() != null) {
                PetType petType = new PetType();
                petType.setName(pet.getType_name());
                // Buscar o ID do tipo pelo nome, se necessário
                List<PetType> types = (List<PetType>) this.petExternalAPI.findPetTypes();
                for (PetType type : types) {
                    if (type.getName().equalsIgnoreCase(pet.getType_name())) {
                        petType.setId(type.getId());
                        break;
                    }
                }
                newPet.setType(petType);
            }
            
            // Salvar o pet usando o serviço de Pet
            this.petExternalAPI.save(newPet);
            
            // Retornar o pet original com status CREATED
            return new ResponseEntity<>(pet, HttpStatus.CREATED);
        }
        
        // 8. Retornar o pet salvo com o ID gerado
        return new ResponseEntity<>(savedPet, HttpStatus.CREATED);
    }

    @PutMapping("/pets/{petId}")
    @Operation(summary = "Update an existing pet")
    public ResponseEntity<?> updatePet(
            @PathVariable("ownerId") int ownerId,
            @PathVariable("petId") int petId,
            @Valid @RequestBody Pet pet,
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Validar data de nascimento
        if (pet.getBirthDate() != null && ((LocalDate) pet.getBirthDate()).isAfter(LocalDate.now())) {
            response.put("error", "Birth date cannot be in the future");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar erros de validação
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar se o pet existe
        Pet existingPet = petExternalAPI.getPetById(petId);
        if (existingPet == null) {
            response.put("error", "Pet not found with ID: " + petId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Configurar o ID do pet e do owner
        pet.setId(petId);
        pet.setOwner_id(ownerId);

        // Salvar o pet atualizado
        petExternalAPI.save(pet);
        
        // Retornar o pet atualizado
        response.put("message", "Pet details has been edited");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/pets/owners/{ownerId}/pets")
    @Operation(summary = "Get all pets for an owner")
    public ResponseEntity<List<Pet>> getPetsByOwnerId(@PathVariable("ownerId") int ownerId) {
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<OwnerPet> ownerPets = owner.getPets();
        
        // Converter OwnerPet para Pet
        List<Pet> pets = ownerPets.stream()
            .map(ownerPet -> {
                Pet pet = new Pet();
                pet.setId((Integer) ownerPet.getId());
                pet.setName(ownerPet.getName());
                pet.setBirthDate((LocalDate) ownerPet.getBirthDate());
                
                // Configurar o tipo do pet se disponível
                if (ownerPet.getType_name() != null) {
                    PetType petType = new PetType();
                    petType.setName(ownerPet.getType_name());
                    pet.setType(petType);
                }
                
                return pet;
            })
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }
    
    @GetMapping("/pets/owner/{ownerId}")
    @Operation(summary = "Get all pets for an owner")
    public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable("ownerId") int ownerId) {
		List<Pet> pets = (List<Pet>) this.petExternalAPI.findById(ownerId);
		return new ResponseEntity<>(pets, HttpStatus.OK);
}
}