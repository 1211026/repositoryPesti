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
	
	public OwnerController(OwnerExternalAPI ownerExternalAPI) {
		this.ownerExternalAPI = ownerExternalAPI;
	}
	

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner1")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
	    try {
	        if (ownerId == null) {
	            System.out.println("Criando novo objeto Owner (ownerId é nulo)");
	            return new Owner();
	        }
	        
	        System.out.println("Buscando owner para @ModelAttribute - ID: " + ownerId);
	        Owner owner = this.ownerExternalAPI.findById(ownerId);
	        
	        if (owner == null) {
	            System.out.println("Owner não encontrado com ID: " + ownerId + ", criando novo objeto");
	            return new Owner();
	        }
	        
	        System.out.println("Owner encontrado: " + owner.getFirstName() + " " + owner.getLastName());
	        
	        // Garantir que a lista de pets está carregada
	        if (owner.getPets() == null) {
	            List<OwnerPet> pets = ownerExternalAPI.findPetByOwner(ownerId);
	            owner.setPets(pets);
	            System.out.println("Carregados " + (pets != null ? pets.size() : 0) + " pets para o owner");
	        }
	        
	        return owner;
	    } catch (Exception e) {
	        System.err.println("Erro ao buscar owner para @ModelAttribute: " + e.getMessage());
	        e.printStackTrace();
	        return new Owner();
	    }
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
	    try {
	        System.out.println("Processando formulário de criação de owner: " + 
	                          owner.getFirstName() + " " + owner.getLastName());
	        
	        // Verificar se já existe um owner com o mesmo nome
	        if (StringUtils.hasText(owner.getLastName()) && 
	            StringUtils.hasText(owner.getFirstName()) && 
	            ownerExternalAPI.findByName(owner.getFirstName(), owner.getLastName()).isPresent()) {
	            
	            System.out.println("Owner com mesmo nome já existe");
	            result.rejectValue("firstName", "duplicate", "already exists");
	            result.rejectValue("lastName", "duplicate", "already exists");
	        }

	        if (result.hasErrors()) {
	            System.out.println("Erros de validação encontrados: " + result.getAllErrors());
	            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	        }

	        // Salvar o owner
	        System.out.println("Salvando novo owner");
	        Integer new_owner_id = ownerExternalAPI.save(owner);
	        System.out.println("Novo owner criado com ID: " + new_owner_id);
	        
	        // Verificar se o owner foi realmente salvo
	        if (new_owner_id == null) {
	            System.out.println("Erro: ID do novo owner é nulo");
	            model.addAttribute("errorMessage", "Failed to save owner. Please try again.");
	            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	        }
	        
	        redirectAttributes.addFlashAttribute("message", "New Owner Created");
	        return "redirect:/owners/" + new_owner_id;
	    } catch (Exception e) {
	        System.err.println("Erro ao processar formulário de criação de owner: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
	        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	    }
	}

	@GetMapping("/owners/find")
	@Operation(summary = "Initiate Find Owner Form")
	public String initFindForm(Model model) {
	    // Adicionar um objeto Owner vazio ao modelo para o formulário
	    model.addAttribute("owner", new Owner());
	    System.out.println("Iniciando formulário de busca de owners");
	    return "owners/findOwners";
	}

	@GetMapping("/owners")
	@Operation(summary = "Process Find Owner Form")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
	        Model model) {
	    try {
	        System.out.println("Processando busca de owners - lastName: " + 
	                          (owner.getLastName() != null ? owner.getLastName() : "null"));
	        
	        if (owner.getLastName() == null) {
	            owner.setLastName("");
	        }
	        
	        // Buscar owners paginados pelo sobrenome
	        Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, owner.getLastName());
	        System.out.println("Resultados encontrados: " + ownersResults.getTotalElements());
	        
	        if (ownersResults.isEmpty()) {
	            System.out.println("Nenhum owner encontrado com lastName: " + owner.getLastName());
	            result.rejectValue("lastName", "notFound", "not found");
	            return "owners/findOwners";
	        }
	        
	        if (ownersResults.getTotalElements() == 1) {
	            owner = ownersResults.iterator().next();
	            System.out.println("Um único owner encontrado, redirecionando para: " + owner.getId());
	            
	            // Garantir que todos os dados relacionados estão carregados
	            Owner completeOwner = this.ownerExternalAPI.findById(owner.getId());
	            if (completeOwner != null) {
	                return "redirect:/owners/" + completeOwner.getId();
	            } else {
	                System.out.println("Erro: Owner encontrado na busca mas não localizado pelo ID: " + owner.getId());
	                result.rejectValue("lastName", "notFound", "owner data inconsistency detected");
	                return "owners/findOwners";
	            }
	        }
	        
	        System.out.println("Múltiplos owners encontrados, exibindo lista paginada");
	        return addPaginationModel(page, model, ownersResults);
	    } catch (Exception e) {
	        System.err.println("Erro ao processar busca de owners: " + e.getMessage());
	        e.printStackTrace();
	        result.rejectValue("lastName", "error", "An error occurred while searching");
	        return "owners/findOwners";
	    }
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
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId, 
	                                     RedirectAttributes redirectAttributes, ModelMap model) {
	    try {
	        System.out.println("Processando atualização do owner ID: " + ownerId);
	        
	        if (result.hasErrors()) {
	            System.out.println("Erros de validação encontrados: " + result.getAllErrors());
	            model.addAttribute("ownerMessage", "There was an error updating the owner.");
	            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	        }
	        
	        // Buscar os pets do owner antes de atualizar
	        System.out.println("Buscando pets do owner");
	        List<OwnerPet> ownerPets = ownerExternalAPI.findPetByOwner(ownerId);
	        System.out.println("Encontrados " + (ownerPets != null ? ownerPets.size() : 0) + " pets");
	        
	        // Definir o ID e os pets no objeto owner
	        owner.setPets(ownerPets);
	        owner.setId(ownerId);
	        
	        // Salvar o owner atualizado
	        System.out.println("Salvando owner atualizado");
	        Integer updatedOwnerId = this.ownerExternalAPI.save(owner);
	        System.out.println("Owner atualizado, ID retornado: " + updatedOwnerId);
	        
	        // Verificar se a atualização foi bem-sucedida
	        if (updatedOwnerId == null || updatedOwnerId != ownerId) {
	            System.out.println("Erro: ID do owner atualizado não corresponde ao esperado");
	            model.addAttribute("ownerMessage", "There was an error updating the owner. Please try again.");
	            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	        }
	        
	        redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
	        return "redirect:/owners/{ownerId}";
	    } catch (Exception e) {
	        System.err.println("Erro ao processar atualização do owner: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("ownerMessage", "An error occurred: " + e.getMessage());
	        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	    }
	}
	

	@GetMapping("/owners/{ownerId}")
	@Operation(summary = "Show Owner Details")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
	    try {
	        System.out.println("Exibindo detalhes do owner ID: " + ownerId);
	        ModelAndView mav = new ModelAndView("owners/ownerDetails");
	        
	        // Buscar o owner com todos os dados relacionados
	        Owner owner = this.ownerExternalAPI.findById(ownerId);
	        
	        if (owner == null) {
	            System.out.println("Owner não encontrado com ID: " + ownerId);
	            mav.setViewName("exception");
	            mav.addObject("exception", new RuntimeException("Owner not found with ID: " + ownerId));
	            return mav;
	        }
	        
	        System.out.println("Owner encontrado: " + owner.getFirstName() + " " + owner.getLastName());
	        
	        // Verificar se a lista de pets está carregada
	        if (owner.getPets() == null) {
	            System.out.println("Lista de pets é nula, buscando pets para o owner");
	            List<OwnerPet> pets = ownerExternalAPI.findPetByOwner(ownerId);
	            owner.setPets(pets);
	            System.out.println("Encontrados " + (pets != null ? pets.size() : 0) + " pets");
	        } else {
	            System.out.println("Owner já tem " + owner.getPets().size() + " pets carregados");
	        }
	        
	        mav.addObject(owner);
	        return mav;
	    } catch (Exception e) {
	        System.err.println("Erro ao exibir detalhes do owner: " + e.getMessage());
	        e.printStackTrace();
	        ModelAndView mav = new ModelAndView("exception");
	        mav.addObject("exception", e);
	        return mav;
	    }
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
	    try {
	        System.out.println("Buscando owners paginados - página: " + page + ", lastName: " + lastname);
	        int pageSize = 5;
	        Pageable pageable = PageRequest.of(page - 1, pageSize);
	        Page<Owner> result = ownerExternalAPI.findByLastName(lastname, pageable);
	        System.out.println("Encontrados " + result.getTotalElements() + " owners no total");
	        return result;
	    } catch (Exception e) {
	        System.err.println("Erro ao buscar owners paginados: " + e.getMessage());
	        e.printStackTrace();
	        // Retornar uma página vazia em caso de erro
	        return Page.empty();
	    }
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
	    try {
	        List<Owner> listOwners = paginated.getContent();
	        System.out.println("Adicionando modelo de paginação - página: " + page + 
	                          ", total de páginas: " + paginated.getTotalPages() + 
	                          ", itens na página atual: " + listOwners.size());
	        
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", paginated.getTotalPages());
	        model.addAttribute("totalItems", paginated.getTotalElements());
	        model.addAttribute("listOwners", listOwners);
	        return "owners/ownersList";
	    } catch (Exception e) {
	        System.err.println("Erro ao adicionar modelo de paginação: " + e.getMessage());
	        e.printStackTrace();
	        // Adicionar mensagem de erro ao modelo
	        model.addAttribute("error", "An error occurred while preparing the pagination");
	        return "owners/ownersList";
	    }
	}

	

}
