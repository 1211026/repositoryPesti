package org.springframework.samples.Owner.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.Owner.OwnerExternalAPI;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "owner-controller")
public class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	@Autowired
	private final OwnerExternalAPI ownerExternalAPI;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner1")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.ownerExternalAPI.findById(ownerId);
	}

	@GetMapping("/owners/new")
	@Operation(summary = "Initiate Owner Creation Form")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	@Operation(summary = "Process Owner Creation Form")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes, ModelMap model) {
		if (StringUtils.hasText(owner.getLastName())  && ownerExternalAPI.findByName(owner.getFirstName(), owner.getLastName()).isPresent()) {
			result.rejectValue("firstName", "duplicate", "already exists");
			result.rejectValue("lastName", "duplicate", "already exists");
		}

		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		Integer new_owner_id = ownerExternalAPI.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + new_owner_id;
	}

	@GetMapping("/owners/find")
	@Operation(summary = "Initiate Find Owner Form")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	@Operation(summary = "Process Find Owner Form")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		if (owner.getLastName() == null) {
			owner.setLastName("");
		}
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, owner.getLastName());
		if (ownersResults.isEmpty()) {
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		if (ownersResults.getTotalElements() == 1) {
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		return addPaginationModel(page, model, ownersResults);
	}

	@GetMapping("/owners/{ownerId}/edit")
	@Operation(summary = "Initiate Owner Update Form")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, ModelMap model) {
		Owner owner = this.ownerExternalAPI.findById(ownerId);
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	@Operation(summary = "Process Owner Update Form")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId, RedirectAttributes redirectAttributes,
										 ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("ownerMessage", "There was an error updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		List<OwnerPet> ownerPets = ownerExternalAPI.findPetByOwner(ownerId);
		owner.setPets(ownerPets);
		owner.setId(ownerId);
		this.ownerExternalAPI.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}")
	@Operation(summary = "Show Owner Details")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.ownerExternalAPI.findById(ownerId);
		mav.addObject(owner);
		return mav;
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return ownerExternalAPI.findByLastName(lastname, pageable);
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

}
