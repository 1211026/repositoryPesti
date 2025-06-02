package org.springframework.samples.petclinic.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "visit-rest-controller", description = "Operations about visits")
public class VisitRestController {

    private final OwnerRepository owners;

    public VisitRestController(OwnerRepository owners) {
        this.owners = owners;
    }

    @GetMapping("/visits/pet/{petId}")
    @Operation(summary = "Get all visits for a pet")
    public ResponseEntity<Collection<Visit>> getVisitsForPet(@PathVariable("petId") int petId) {
        Pet pet = this.owners.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pet.getVisits(), HttpStatus.OK);
    }

    @PostMapping("/visits/pet/{petId}")
    @Operation(summary = "Create a new visit for a pet")
    public ResponseEntity<Visit> addVisit(@PathVariable("petId") int petId, @Valid @RequestBody Visit visit) {
        // Encontrar o pet
        Pet pet = this.owners.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Encontrar o owner do pet
        Owner owner = null;
        Pageable pageable = PageRequest.of(0, 1000); // Usar um tamanho grande para obter todos
        Page<Owner> ownersPage = this.owners.findAll(pageable);
        List<Owner> allOwners = ownersPage.getContent();
        
        for (Owner o : allOwners) {
            for (Pet p : o.getPets()) {
                if (p.getId() != null && p.getId().equals(petId)) {
                    owner = o;
                    break;
                }
            }
            if (owner != null) break;
        }
        
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Adicionar a visita ao pet
        pet.addVisit(visit);
        
        // Salvar o owner, que cascateia para o pet e a visita
        this.owners.save(owner);
        
        return new ResponseEntity<>(visit, HttpStatus.CREATED);
    }

    @GetMapping("/visits")
    @Operation(summary = "Get all visits")
    public ResponseEntity<List<Visit>> getAllVisits() {
        List<Visit> allVisits = new ArrayList<>();
        
        // Como não temos um método direto para obter todas as visitas,
        // vamos buscar todos os pets e coletar suas visitas
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Owner> ownersPage = this.owners.findAll(pageable);
        List<Owner> allOwners = ownersPage.getContent();
        
        for (Owner owner : allOwners) {
            for (Pet pet : owner.getPets()) {
                allVisits.addAll(pet.getVisits());
            }
        }
        
        return new ResponseEntity<>(allVisits, HttpStatus.OK);
    }
}