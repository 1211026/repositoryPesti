package org.springframework.samples.petclinic.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/vets")
@Tag(name = "vet-rest-controller", description = "Operations about veterinarians")
public class VetRestController {

    private final VetRepository vets;

    public VetRestController(VetRepository vets) {
        this.vets = vets;
    }

    @GetMapping("/list-all")
    @Operation(summary = "Get all vets or paginated list")
    public ResponseEntity<List<Vet>> getVets(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size) {
        
        if (size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Vet> pageResult = this.vets.findAll(pageable);
            return new ResponseEntity<>(pageResult.getContent(), HttpStatus.OK);
        } else {
            // Usar findAll com paginação, mas com um tamanho grande para obter todos
            Pageable pageable = PageRequest.of(0, 1000);
            Page<Vet> pageResult = this.vets.findAll(pageable);
            return new ResponseEntity<>(pageResult.getContent(), HttpStatus.OK);
        }
    }

    @GetMapping("/{vetId}")
    @Operation(summary = "Get vet by ID")
    public ResponseEntity<Vet> getVet(@PathVariable("vetId") int vetId) {
        // Como não há método findById no VetRepository, precisamos buscar todos e filtrar
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Vet> pageResult = this.vets.findAll(pageable);
        List<Vet> allVets = pageResult.getContent();
        
        Vet vet = allVets.stream()
            .filter(v -> v.getId() != null && v.getId().equals(vetId))
            .findFirst()
            .orElse(null);
            
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vet, HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all vets as array")
    public ResponseEntity<List<Vet>> getAllVets() {
        // Usar findAll com paginação, mas com um tamanho grande para obter todos
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Vet> pageResult = this.vets.findAll(pageable);
        return new ResponseEntity<>(pageResult.getContent(), HttpStatus.OK);
    }
    
    
}
