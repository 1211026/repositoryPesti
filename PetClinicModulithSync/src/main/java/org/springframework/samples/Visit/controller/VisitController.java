package org.springframework.samples.Visit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.samples.Owner.OwnerPublicAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Pet.PetPublicAPI;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Visit.VisitExternalAPI;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "visit-controller")
public class VisitController {

    private final VisitExternalAPI visitExternalAPI;
    
    private final OwnerPublicAPI ownerPublicAPI;
    
    private final PetPublicAPI petPublicAPI;
    
    @Autowired
    public VisitController(@Qualifier("visitService") VisitExternalAPI visitExternalAPI,
						   @Qualifier("ownerService") OwnerPublicAPI ownerPublicAPI,
						   @Qualifier("petService") PetPublicAPI petPublicAPI) {
		this.visitExternalAPI = visitExternalAPI;
		this.ownerPublicAPI = ownerPublicAPI;
		this.petPublicAPI = petPublicAPI;
	}

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("visit")
    public Visit loadPetWithVisit(@PathVariable("petId") int petId, @PathVariable("ownerId") int ownerId, Map<String, Object> model) {
        Visit visit = new Visit();
        
        
        Pet pet = this.petPublicAPI.findById(petId);
        if (pet != null) {
            model.put("pet", pet);
            visit.setId(petId);
        }
        
        
        Owner owner = this.ownerPublicAPI.findById(ownerId);
        if (owner != null) {
            model.put("owner", owner);
        }
        
        model.put("visit", visit);
        return visit;
    }
    
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    @Operation(summary = "Initiate New Visit Form")
    public String initNewVisitForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, ModelMap model) {
        
        Visit visit = (Visit) model.get("visit");
        if (visit != null && visit.getPet_id() == null) {
            visit.setId(petId);
        }
        
        return "pets/createOrUpdateVisitForm";
    }

    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    @Operation(summary = "Process New Visit Form")
    public String processNewVisitForm(
        @Valid Visit visit, 
        BindingResult result, 
        @PathVariable("ownerId") int ownerId,
        @PathVariable("petId") int petId) {
        
        if (result.hasErrors()) {
            return "pets/createOrUpdateVisitForm";
        }
        
        visit.setId(petId);
        this.visitExternalAPI.save(visit);
        return "redirect:/owners/" + ownerId;
    }
}
