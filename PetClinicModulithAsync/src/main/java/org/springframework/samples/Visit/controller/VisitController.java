
package org.springframework.samples.Visit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
		Visit visit = new Visit();
		model.put("visit", visit);
		return visit;
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	@Operation(summary = "Initiate New Visit Form")
	public String initNewVisitForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			ModelMap model) {

		Visit visit = new Visit();
		model.put("visit", visit);
		return "pets/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	@Operation(summary = "Process New Visit Form")
	public String processNewVisitForm(@PathVariable("petId") int petId, @Valid Visit visit,
									  RedirectAttributes redirectAttributes, BindingResult result) {
		if(visit.getDescription().isBlank()){
			result.rejectValue("description", "null", "não deve estar em branco");
		}
		if (result.hasErrors()){
			return "pets/createOrUpdateVisitForm";
		}
		visit.setPet_id(petId);
		visitExternalAPI.save(visit);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

}
