package org.springframework.samples.petclinic.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vets")
public class VetRestController {

    private static final Logger logger = LoggerFactory.getLogger(VetRestController.class);
    private final VetRepository vetRepository;

    public VetRestController(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @GetMapping
    public ResponseEntity<List<Vet>> getAllVets(Pageable pageable) {
        try {
            Page<Vet> vets = vetRepository.findAll(pageable);
            return ResponseEntity.ok(vets.getContent());
        } catch (Exception e) {
            logger.error("Erro ao buscar Vets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Melhor do que vazio sem explicação
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vet> getVetById(@PathVariable("id") int id) {
        Vet vet = vetRepository.findById(id);
        return vet != null ? ResponseEntity.ok(vet) : ResponseEntity.notFound().build();
    }



    @PostMapping
    public ResponseEntity<?> createVet(@Valid @RequestBody Vet vet) {
        try {
            vetRepository.save(vet);
            return ResponseEntity.status(HttpStatus.CREATED).body(vet);
        } catch (Exception e) {
            logger.error("Erro ao criar Vet", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar vet: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Erro inesperado no VetRestController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + e.getMessage());
    }
}
