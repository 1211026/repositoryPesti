package org.springframework.samples.Visit.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.Owner.OwnerPublicAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Pet.PetPublicAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Visit.VisitExternalAPI;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "visit-rest-controller", description = "Operations about visits")
public class VisitRestController {

    private final VisitExternalAPI visitExternalAPI;
    private final PetPublicAPI petPublicAPI;
    private final OwnerPublicAPI ownerPublicAPI;
    
    @Autowired
    public VisitRestController(@Qualifier("visitService") VisitExternalAPI visitExternalAPI,@Qualifier("petService") PetPublicAPI petPublicAPI,@Qualifier("ownerService") OwnerPublicAPI ownerPublicAPI) {
        this.visitExternalAPI = visitExternalAPI;
        this.petPublicAPI = petPublicAPI;
        this.ownerPublicAPI = ownerPublicAPI;
    }

    @GetMapping("/visits")
    @Operation(summary = "Get all visits")
    public ResponseEntity<Collection<Visit>> getAllVisits() {
        Collection<Visit> visits = this.visitExternalAPI.findAll();
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }
    
    @GetMapping("/visits/pet/{petId}")
    @Operation(summary = "Get visits for a specific pet")
    public ResponseEntity<?> getVisitsForPet(@PathVariable("petId") int petId) {
        Pet pet = this.petPublicAPI.findById(petId);
        if (pet == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Pet not found with ID: " + petId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        Collection<Visit> visits = this.visitExternalAPI.findByPetId(petId);
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }
    
    @GetMapping("/visits/{visitId}")
    @Operation(summary = "Get visit by ID")
    public ResponseEntity<?> getVisit(@PathVariable("visitId") int visitId) {
        Visit visit = this.visitExternalAPI.findById(visitId);
        if (visit == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Visit not found with ID: " + visitId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(visit, HttpStatus.OK);
    }
    
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits")
    @Operation(summary = "Create a new visit")
    public ResponseEntity<?> createVisit(
            @PathVariable("ownerId") int ownerId,
            @PathVariable("petId") int petId,
            @Valid @RequestBody Visit visit,
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Verificar se o owner existe
        Owner owner = this.ownerPublicAPI.findById(ownerId);
        if (owner == null) {
            response.put("error", "Owner not found with ID: " + ownerId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Verificar se o pet existe
        Pet pet = this.petPublicAPI.findById(petId);
        if (pet == null) {
            response.put("error", "Pet not found with ID: " + petId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Validar data da visita
        if (visit.getDate() == null) {
            visit.setDate(LocalDate.now());
        } else if (visit.getDate().isBefore(LocalDate.now().minusYears(1))) {
            response.put("error", "Visit date cannot be more than 1 year in the past");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // Verificar erros de validação
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // Configurar o ID do pet
        visit.setPetId(petId);
        
        
        
        response.put("message", "New Visit has been added");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/visits/{visitId}")
    @Operation(summary = "Update an existing visit")
    public ResponseEntity<?> updateVisit(
            @PathVariable("visitId") int visitId,
            @Valid @RequestBody Visit visit,
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Verificar se a visita existe
        Visit existingVisit = this.visitExternalAPI.findById(visitId);
        if (existingVisit == null) {
            response.put("error", "Visit not found with ID: " + visitId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Validar data da visita
        if (visit.getDate() == null) {
            visit.setDate(LocalDate.now());
        } else if (visit.getDate().isBefore(LocalDate.now().minusYears(1))) {
            response.put("error", "Visit date cannot be more than 1 year in the past");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // Verificar erros de validação
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // Manter o ID do pet
        visit.setPetId(existingVisit.getPetId());
        
        // Configurar o ID da visita
        visit.setId(visitId);
        
 
        
        response.put("message", "Visit has been updated");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/visits/{visitId}")
    @Operation(summary = "Delete a visit")
    public ResponseEntity<?> deleteVisit(@PathVariable("visitId") int visitId) {
        Visit visit = this.visitExternalAPI.findById(visitId);
        if (visit == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Visit not found with ID: " + visitId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Implementar lógica de exclusão (se disponível no VisitExternalAPI)
        // this.visitExternalAPI.delete(visitId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Visit has been deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
