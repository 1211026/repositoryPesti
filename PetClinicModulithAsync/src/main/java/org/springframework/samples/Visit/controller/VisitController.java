
package org.springframework.samples.Visit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Pet.PetExternalAPI;
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


import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "visit-controller")
public class VisitController {

	private final VisitExternalAPI visitExternalAPI;
	
	private final PetExternalAPI petPublicAPI;
	
	private final OwnerExternalAPI ownerExternalAPI;
	
	@Autowired
	public VisitController(VisitExternalAPI visitExternalAPI, PetExternalAPI petPublicAPI, OwnerExternalAPI ownerPublicAPI) {
		this.visitExternalAPI = visitExternalAPI;
		this.petPublicAPI = petPublicAPI;
		this.ownerExternalAPI = ownerPublicAPI;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, @PathVariable("ownerId") int ownerId, Map<String, Object> model) {
	    Visit visit = new Visit();
	    
	    // Carregar o pet
	    Pet pet = this.petPublicAPI.findById(petId);
	    if (pet != null) {
	        model.put("pet", pet);
	        visit.setPet_id(petId);
	    }
	    
	    // Carregar o owner
	    Owner owner = this.ownerExternalAPI.findById(ownerId);
	    if (owner != null) {
	        model.put("owner", owner);
	    }
	    
	    model.put("visit", visit);
	    return visit;
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	@Operation(summary = "Initiate New Visit Form")
	public String initNewVisitForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, ModelMap model) {
	    // O método loadPetWithVisit já foi chamado e configurou o modelo
	    // Podemos adicionar mais dados ao modelo se necessário
	    
	    // Garantir que o pet_id está definido
	    Visit visit = (Visit) model.get("visit");
	    if (visit != null && visit.getPet_id() == null) {
	        visit.setPet_id(petId);
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
	    
	    visit.setPet_id(petId);
	    this.visitExternalAPI.save(visit);
	    return "redirect:/owners/" + ownerId;
	}

}
