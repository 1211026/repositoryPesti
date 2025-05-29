
package org.springframework.samples.petclinic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitRestController {

    private static final Logger logger = LoggerFactory.getLogger(VisitRestController.class);

    @Autowired
    private OwnerRepository ownerRepository;

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        try {
            if (visit.getPet() == null || visit.getPet().getId() == null) {
                logger.error("Visit creation failed: Pet is null or has no ID");
                return ResponseEntity.badRequest().build();
            }

            Pet pet = ownerRepository.findPetById(visit.getPet().getId());
            if (pet == null) {
                logger.error("Visit creation failed: Pet with ID {} not found", visit.getPet().getId());
                return ResponseEntity.notFound().build();
            }

            // Garantir que a data está definida
            if (visit.getDate() == null) {
                visit.setDate(LocalDate.now());
            }

            // Adicionar a visita ao pet
            pet.addVisit(visit);

            // Salvar o owner
            Owner owner = ownerRepository.findOwnerByPetId(pet.getId());
            if (owner == null) {
                logger.error("Visit creation failed: Owner for pet with ID {} not found", pet.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            ownerRepository.save(owner);

            logger.info("Visit created successfully for pet ID {}: {}", pet.getId(), visit.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(visit);
        } catch (Exception e) {
            logger.error("Unexpected error creating visit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<Visit>> getVisitsByPetId(@PathVariable("petId") Integer petId) {
        try {
            Pet pet = ownerRepository.findPetById(petId);
            if (pet == null) {
                logger.error("Get visits failed: Pet with ID {} not found", petId);
                return ResponseEntity.notFound().build();
            }

            List<Visit> visits = new ArrayList<>(pet.getVisits());
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            logger.error("Error getting visits for pet with ID {}", petId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
