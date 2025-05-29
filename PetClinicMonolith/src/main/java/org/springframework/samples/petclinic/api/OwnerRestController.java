package org.springframework.samples.petclinic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.repository.OwnerRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owners")
public class OwnerRestController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerRestController.class);
    private final OwnerRepository ownerRepository;

    public OwnerRestController(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Owner>> getAllOwners(Pageable pageable) {
        try {
            List<Owner> owners = ownerRepository.findAll(pageable).getContent();

            // força a inicialização de pets para evitar LazyInitializationException
            owners.forEach(o -> o.getPets().size());

            return owners.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(owners);
        } catch (Exception e) {
            logger.error("Erro ao buscar Owners", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable("id") int id) {
        Owner owner = ownerRepository.findById(id);
        return (owner != null) ? ResponseEntity.ok(owner) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Owner> createOwner(@RequestBody Owner owner) {
        try {
            if (owner.getPets() != null) {
                for (Pet pet : owner.getPets()) {
                    pet.setOwner(owner);  // ✅ assegura a relação bidirecional
                }
            }

            ownerRepository.save(owner);
            return ResponseEntity.status(HttpStatus.CREATED).body(owner);
        } catch (Exception e) {
            logger.error("Erro ao criar Owner", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOwner(@PathVariable("id") int id, @RequestBody Owner updatedOwner) {
        try {
            Owner existing = ownerRepository.findById(id);
            if (existing == null) return ResponseEntity.notFound().build();

            existing.setFirstName(updatedOwner.getFirstName());
            existing.setLastName(updatedOwner.getLastName());
            existing.setAddress(updatedOwner.getAddress());
            existing.setCity(updatedOwner.getCity());
            existing.setTelephone(updatedOwner.getTelephone());

            existing.getPets().clear();
            if (updatedOwner.getPets() != null) {
                for (Pet pet : updatedOwner.getPets()) {
                    pet.setOwner(existing);   // 🛠 define o dono corretamente
                    existing.addPet(pet);     // 🛠 método seguro e bidirecional
                }
            }

            ownerRepository.save(existing);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar Owner com ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{ownerId}/pets")
    public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable("ownerId") int ownerId) {
        Owner owner = ownerRepository.findById(ownerId);
        return (owner == null)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(owner.getPets().stream().collect(Collectors.toList()));
    }

    @GetMapping(params = "lastName")
    public ResponseEntity<List<Owner>> searchOwners(@RequestParam("lastName") String lastName) {
        return ResponseEntity.ok(ownerRepository.findByLastNameRaw(lastName));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        logger.error("Erro inesperado no OwnerRestController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + e.getMessage());
    }
}
