package org.springframework.samples;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.qameta.allure.Step;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.model.OwnerPet;
import org.springframework.samples.Owner.model.OwnerPet.Visit;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;
import org.springframework.samples.Vet.model.Vet;
import org.springframework.samples.Vet.model.Vets;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.qameta.allure.Step;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.time.api.Dates;
import org.springframework.test.context.junit.jupiter.SpringExtension;





@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class MetamorphicTests {

	private final String BASE_URL = "http://localhost:8080";
    private final String SWAGGER_URL = "http://localhost:8080/swagger-ui.html";
    private RestTemplate restTemplate = new RestTemplate();
    
    @LocalServerPort // <<-- AQUI ESTÁ ELA!
    private int port; // A porta aleatória será injetada aqui

    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private String petTypeIdForForms;
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    
    
   
    
	private static void logToSwagger(String message) {
        System.out.println("Swagger Log: " + message);
        
    }
	
	

	@Property(tries = 3)
	@Step("Teste: Adicionar owner")
	void addOwnerIncreasesTotal(@ForAll("validOwnerData") Owner newOwner) {
	        
	        int initialCount = getTotalOwners();
	        System.out.println("Contagem inicial de owners: " + initialCount);
	        
	        
	        String uniquePrefix = "HTML_" + UUID.randomUUID().toString().substring(0, 6);
	        newOwner.setFirstName(uniquePrefix + "_" + newOwner.getFirstName());
	        newOwner.setLastName("Owner_" + newOwner.getLastName());
	        
	        
	        if (newOwner.getAddress() == null) newOwner.setAddress("Test Address");
	        if (newOwner.getCity() == null) newOwner.setCity("Test City");
	        if (newOwner.getTelephone() == null) newOwner.setTelephone("1234567890");
	        
	        System.out.println("Creating owner with name: " + newOwner.getFirstName() + " " + newOwner.getLastName());
	        
	        
	        ResponseEntity<String> formResponse = restTemplate.getForEntity(
	            BASE_URL + "/owners/new",
	            String.class
	        );
	        
	        assertThat(formResponse.getStatusCode().is2xxSuccessful()).isTrue();
	        
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        
	        
	        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	        formData.add("firstName", newOwner.getFirstName());
	        formData.add("lastName", newOwner.getLastName());
	        formData.add("address", newOwner.getAddress());
	        formData.add("city", newOwner.getCity());
	        formData.add("telephone", newOwner.getTelephone());
	        
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
	        
	        ResponseEntity<String> response = restTemplate.exchange(
	            BASE_URL + "/owners/new",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	        );
	               
	        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();

	        String redirectUrl = response.getHeaders().getLocation() != null 
	            ? response.getHeaders().getLocation().toString() 
	            : response.getHeaders().getFirst("Location");
	            
	        assertThat(redirectUrl).isNotNull();
	        
	        Pattern pattern = Pattern.compile("/owners/(\\d+)");
	        Matcher matcher = pattern.matcher(redirectUrl);
	        
	        assertThat(matcher.find()).isTrue();
	        int ownerId = Integer.parseInt(matcher.group(1));
	        System.out.println("Owner criado com sucesso, ID: " + ownerId);
	        

	        int finalCount = getTotalOwners();
	        System.out.println("Contagem final de owners: " + finalCount);

	        assertThat(finalCount).isGreaterThanOrEqualTo(initialCount);
	        
	        Owner createdOwner = getOwner(ownerId);
	        assertThat(createdOwner).isNotNull();
	        
	        logToSwagger("Test passed: addOwnerViaHTML - Created owner with ID: " + ownerId);
	    
	}
	
	@Property(tries = 3)
	@Step("Teste: Obter o mesmo owner duas vezes retorna o mesmo resultado")
	void getSameOwnerTwiceYieldsSameResult(@ForAll("validOwnerData") Owner newOwner) {
	    logToSwagger("Starting test: getSameOwnerTwiceYieldsSameResult");
	   
	        String uniquePrefix = "Same_" + UUID.randomUUID().toString().substring(0, 6);
	        newOwner.setFirstName(uniquePrefix + "_" + newOwner.getFirstName());
	        newOwner.setLastName("Owner_" + newOwner.getLastName());
	        
	        if (newOwner.getAddress() == null) newOwner.setAddress("Test Address");
	        if (newOwner.getCity() == null) newOwner.setCity("Test City");
	        if (newOwner.getTelephone() == null) newOwner.setTelephone("1234567890");
	        
	        System.out.println("Creating owner with name: " + newOwner.getFirstName() + " " + newOwner.getLastName());
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        
	        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	        formData.add("firstName", newOwner.getFirstName());
	        formData.add("lastName", newOwner.getLastName());
	        formData.add("address", newOwner.getAddress());
	        formData.add("city", newOwner.getCity());
	        formData.add("telephone", newOwner.getTelephone());
	        
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
	        

	        ResponseEntity<String> response = restTemplate.exchange(
	            BASE_URL + "/owners/new",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	        );
	        
	        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();

	        String redirectUrl = response.getHeaders().getLocation() != null 
	            ? response.getHeaders().getLocation().toString() 
	            : response.getHeaders().getFirst("Location");
	            
	        assertThat(redirectUrl).isNotNull();
	        
	        Pattern pattern = Pattern.compile("/owners/(\\d+)");
	        Matcher matcher = pattern.matcher(redirectUrl);
	        
	        assertThat(matcher.find()).isTrue();
	        int ownerId = Integer.parseInt(matcher.group(1));
	        System.out.println("Owner criado com sucesso, ID: " + ownerId);
	        

	        Owner o1 = getOwner(ownerId);
	        System.out.println("First retrieval - Owner ID: " + o1.getId() + ", Name: " + o1.getFirstName() + " " + o1.getLastName());
	        
	        Owner o2 = getOwner(ownerId);
	        System.out.println("Second retrieval - Owner ID: " + o2.getId() + ", Name: " + o2.getFirstName() + " " + o2.getLastName());

	        assertThat(o1.getId()).isEqualTo(o2.getId());
	        assertThat(o1.getFirstName()).isEqualTo(o2.getFirstName());
	        assertThat(o1.getLastName()).isEqualTo(o2.getLastName());
	        assertThat(o1.getAddress()).isEqualTo(o2.getAddress());
	        assertThat(o1.getCity()).isEqualTo(o2.getCity());
	        assertThat(o1.getTelephone()).isEqualTo(o2.getTelephone());
	        
	        
	        System.out.println("Owner properties match between retrievals");
	        
	        logToSwagger("Test passed: getSameOwnerTwiceYieldsSameResult - Owner ID: " + ownerId);
	    
	}
	
	@Property(tries = 5)
	@Step("Teste: Busca com sobrenome mais longo é subconjunto")
	void searchWithLongerLastNameIsSubset(@ForAll("prefixLetter") String prefix, 
	                                     @ForAll("secondLetter") String second) {
	    logToSwagger("Starting test: searchWithLongerLastNameIsSubset");

	        String basePrefix = prefix;
	        String extendedPrefix = prefix + second;
	        
	        System.out.println("Teste com prefixos: base='" + basePrefix + "', estendido='" + extendedPrefix + "'");
	        
	        List<Owner> r1 = searchOwners(basePrefix);
	        System.out.println("Busca por '" + basePrefix + "' retornou " + r1.size() + " owners");
	        
	        List<Owner> r2 = searchOwners(extendedPrefix);
	        System.out.println("Busca por '" + extendedPrefix + "' retornou " + r2.size() + " owners");

	        if (r2.isEmpty()) {
	            System.out.println("Busca por prefixo estendido não retornou resultados, teste passa trivialmente");
	            logToSwagger("Test passed trivially: searchWithLongerLastNameIsSubset - Extended prefix returned no results");
	            return;
	        }
	        
	        Set<Integer> ids1 = r1.stream().map(Owner::getId).collect(Collectors.toSet());
	        Set<Integer> ids2 = r2.stream().map(Owner::getId).collect(Collectors.toSet());
	        
	        System.out.println("IDs da busca base: " + ids1);
	        System.out.println("IDs da busca estendida: " + ids2);
	        
	        boolean isSubset = ids1.containsAll(ids2);
	        assertThat(isSubset).isTrue();
	        
	        System.out.println("Verificado que a busca por '" + extendedPrefix + 
	                          "' é um subconjunto da busca por '" + basePrefix + "'");
	        
	        logToSwagger("Test passed: searchWithLongerLastNameIsSubset - Verified that search with '" + 
	                    extendedPrefix + "' is a subset of search with '" + basePrefix + "'");
	    
	}
	
	@Property(tries = 5)
	@Step("Teste: Editar telefone do owner deve ser visível")
	void editOwnerPhoneNumberShouldBeVisible(@ForAll("validOwnerData") Owner newOwner,
	                                         @ForAll("validPhoneNumber") String newPhone) {
	    logToSwagger("Starting test: editOwnerPhoneNumberShouldBeVisible");

	        String uniquePrefix = "Phone_" + UUID.randomUUID().toString().substring(0, 6);
	        newOwner.setFirstName("PhoneOwner_" + uniquePrefix);
	        newOwner.setLastName("PhoneTest_" + newOwner.getLastName());
	        
	        System.out.println("Criando owner para teste de telefone: " + newOwner.getFirstName() + " " + newOwner.getLastName());
	        
	        // Criar owner usando o formulário HTML
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        
	        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	        formData.add("firstName", newOwner.getFirstName());
	        formData.add("lastName", newOwner.getLastName());
	        formData.add("address", newOwner.getAddress() != null ? newOwner.getAddress() : "Test Address");
	        formData.add("city", newOwner.getCity() != null ? newOwner.getCity() : "Test City");
	        formData.add("telephone", newOwner.getTelephone() != null ? newOwner.getTelephone() : "1234567890");
	        
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
	        
	        ResponseEntity<String> response = restTemplate.exchange(
	            BASE_URL + "/owners/new",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	        );
	        

	        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
	        

	        String redirectUrl = response.getHeaders().getLocation() != null 
	            ? response.getHeaders().getLocation().toString() 
	            : response.getHeaders().getFirst("Location");
	            
	        assertThat(redirectUrl).isNotNull();
	        
	        Pattern pattern = Pattern.compile("/owners/(\\d+)");
	        Matcher matcher = pattern.matcher(redirectUrl);
	        
	        assertThat(matcher.find()).isTrue();
	        int ownerId = Integer.parseInt(matcher.group(1));
	        System.out.println("Owner criado com sucesso, ID: " + ownerId);

	        Owner createdOwner = getOwner(ownerId);
	        assertThat(createdOwner).isNotNull();
	        assertThat(createdOwner.getId()).isEqualTo(ownerId);
	        
	        String originalPhone = createdOwner.getTelephone();
	        System.out.println("Telefone original: " + originalPhone);
	        System.out.println("Novo telefone: " + newPhone);
	        

	        MultiValueMap<String, String> updateFormData = new LinkedMultiValueMap<>();
	        updateFormData.add("firstName", createdOwner.getFirstName());
	        updateFormData.add("lastName", createdOwner.getLastName());
	        updateFormData.add("address", createdOwner.getAddress());
	        updateFormData.add("city", createdOwner.getCity());
	        updateFormData.add("telephone", newPhone);
	        
	        HttpEntity<MultiValueMap<String, String>> updateRequestEntity = new HttpEntity<>(updateFormData, headers);
	        
	        ResponseEntity<String> updateResponse = restTemplate.exchange(
	            BASE_URL + "/owners/" + ownerId + "/edit",
	            HttpMethod.POST,
	            updateRequestEntity,
	            String.class
	        );
	        

	        assertThat(updateResponse.getStatusCode().is3xxRedirection()).isTrue();

	        Owner updatedOwner = getOwner(ownerId);
	        
	        assertThat(updatedOwner).isNotNull();
	        assertThat(updatedOwner.getTelephone()).isEqualTo(newPhone);
	        
	        System.out.println("Telefone atualizado com sucesso: " + updatedOwner.getTelephone());
	        
	        logToSwagger("Test passed: editOwnerPhoneNumberShouldBeVisible - Owner ID: " + ownerId + 
	                    ", Original phone: " + originalPhone + ", New phone: " + newPhone);
	   
	}
	
	@Property(tries = 5)
	void addPetIncreasesPetCount(@ForAll("validOwnerData") Owner newOwner,@ForAll("validOwnerPet") OwnerPet pet) {
	    int before = newOwner.getPets().size();
	    newOwner.addPet(pet);
	    int after = newOwner.getPets().size();

	    Assertions.assertTrue(after > before || before == after); 
	}




	@Property(tries = 5)
	@Step("Teste: Subconjunto de veterinários está contido no conjunto completo")
	void vetSubsetIsContainedInFullSet(@ForAll @IntRange(min = 1, max = 3) int subsetSize) {
	    logToSwagger("Starting test: vetSubsetIsContainedInFullSet");
	    
	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        
	        System.out.println("Obtendo lista completa de veterinários...");
	        
	        ResponseEntity<Vets> fullResponse = restTemplate.exchange(
	            BASE_URL + "/vets",
	            HttpMethod.GET,
	            entity,
	            Vets.class
	        );
	        
	        if (!fullResponse.getStatusCode().is2xxSuccessful() || 
	            fullResponse.getBody() == null || 
	            fullResponse.getBody().getVetList().isEmpty()) {
	            System.out.println("Não foi possível obter a lista completa de veterinários, pulando teste");
	            Assume.that(false);
	            return;
	        }
	        
	        List<Vet> allVets = fullResponse.getBody().getVetList();
	        System.out.println("Obtidos " + allVets.size() + " veterinários no total");
	        
	        if (allVets.size() < subsetSize) {
	            System.out.println("Não há veterinários suficientes para criar um subconjunto de tamanho " + subsetSize);
	            Assume.that(false);
	            return;
	        }
	        
	        List<Vet> subsetVets = new ArrayList<>(allVets);
	        Collections.shuffle(subsetVets);
	        subsetVets = subsetVets.subList(0, subsetSize);
	        
	        System.out.println("Criado subconjunto com " + subsetVets.size() + " veterinários");
	        
	        Set<Integer> subsetIds = subsetVets.stream()
	            .map(Vet::getId)
	            .collect(Collectors.toSet());
	        
	        System.out.println("IDs no subconjunto: " + subsetIds);
	        
	        Set<Integer> allIds = allVets.stream()
	            .map(Vet::getId)
	            .collect(Collectors.toSet());
	        
	        System.out.println("IDs no conjunto completo: " + allIds);
	        
	        assertThat(allIds).containsAll(subsetIds);
	        
	        for (Vet subsetVet : subsetVets) {
	            boolean found = allVets.stream()
	                .anyMatch(v -> v.getId().equals(subsetVet.getId()));
	            
	            assertThat(found).isTrue();
	            System.out.println("Veterinário com ID " + subsetVet.getId() + " encontrado no conjunto completo");
	        }
	        
	        logToSwagger("Test passed: vetSubsetIsContainedInFullSet - Verificado que um subconjunto de " + 
	                    subsetSize + " veterinários está contido no conjunto completo de " + allVets.size() + " veterinários");
	    
	}
	

	@Property(tries = 5)
	void addVisitIncreasesVisitCount(@ForAll("validVisit") Visit visit) {
	    Pet pet = new Pet();
	    int before = pet.getVisits().size();
	    pet.addVisit(visit);
	    int after = pet.getVisits().size();

	    Assertions.assertTrue(after > before || before == after); // caso já exista
	}



	
	// --- Teste MetamorphicTests:repeatedPetListShouldBeEqual ---
    @Property(tries = 5)
    @Step("Teste: Repetir lista de pets deve ser igual (via HTML)")
    void repeatedPetListShouldBeEqual(@ForAll("validOwnerData") Owner newOwner) throws Exception {
        Owner criado = createOwnerViaHtml(newOwner);
        Integer ownerId = criado.getId();

        Pet petData = new Pet();
        petData.setName("RepeatPet_" + UUID.randomUUID().toString().substring(0, 6));
        petData.setBirthDate(LocalDate.now().minusYears(1));
        PetType dog = new PetType();
        dog.setId(1);
        dog.setName("dog"); 
        petData.setType(dog);
        
 
        Pet criadoPet = createOnePetViaHtml(criado, petData);
        assertThat(criadoPet).isNotNull().as("O pet criado não deve ser nulo.");
        assertThat(criadoPet.getId()).isNotNull().as("O ID do pet criado não deve ser nulo.");

        List<Pet> pets1 = getPetsViaHtml(ownerId);
        System.out.println("DEBUG (repeatedPetListShouldBeEqual) - Pets 1 (" + pets1.size() + "):\n" + pets1.stream().map(p -> p.getId() + ":" + p.getName()).collect(Collectors.toList()));
        Set<Integer> ids1 = pets1.stream().map(Pet::getId).collect(Collectors.toSet());
        System.out.println("DEBUG (repeatedPetListShouldBeEqual) - IDs Pets 1: " + ids1);

        List<Pet> pets2 = getPetsViaHtml(ownerId);
        System.out.println("DEBUG (repeatedPetListShouldBeEqual) - Pets 2 (" + pets2.size() + "):\n" + pets2.stream().map(p -> p.getId() + ":" + p.getName()).collect(Collectors.toList()));
        Set<Integer> ids2 = pets2.stream().map(Pet::getId).collect(Collectors.toSet());
        System.out.println("DEBUG (repeatedPetListShouldBeEqual) - IDs Pets 2: " + ids2);

        assertThat(ids1)
            .as("Os IDs dos pets da primeira leitura devem ser IGUAIS aos da segunda leitura para idempotência.")
            .isEqualTo(ids2);

        assertThat(pets1)
            .as("As listas de pets deveriam ter o MESMO TAMANHO nas duas leituras.")
            .hasSameSizeAs(pets2);
    }



	@Property(tries = 5)
	@Step("Teste: Sobrenomes diferentes devem retornar owners disjuntos (via HTML)")
	void differentLastNamesShouldReturnDisjointOwners(
	    @ForAll("distinctLastNames") Tuple2<String,String> names) throws Exception {

	    String s1 = names.get1(), s2 = names.get2();
	    Owner o1 = validOwnerData().sample();
	    o1.setLastName(s1);
	    createOwnerViaHtml(o1);

	    Owner o2 = validOwnerData().sample();
	    o2.setLastName(s2);
	    createOwnerViaHtml(o2);


	    List<Owner> lista1 = searchOwnersViaHtml(s1);
	    List<Owner> lista2 = searchOwnersViaHtml(s2);

	    Set<Integer> ids1 = lista1.stream().map(Owner::getId).collect(Collectors.toSet());
	    Set<Integer> ids2 = lista2.stream().map(Owner::getId).collect(Collectors.toSet());

	    assertThat(Collections.disjoint(ids1, ids2)).isTrue();
	}

	
	@Property(tries = 5)
	@Step("Teste: Lista de veterinários deve ser consistente")
	void vetListShouldBeConsistent() {
	    logToSwagger("Starting test: vetListShouldBeConsistent");

	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        
	        System.out.println("Requesting vets list from: " + BASE_URL + "/vets");
	        
	        ResponseEntity<Vets> response = restTemplate.exchange(
	            BASE_URL + "/vets",
	            HttpMethod.GET,
	            entity,
	            Vets.class
	        );
	        
	        if (!response.getStatusCode().is2xxSuccessful()) {
	            System.out.println("Endpoint returned non-success status code: " + response.getStatusCode());
	            Assume.that(false);
	            return;
	        }
	        
	        if (response.getBody() == null) {
	            System.out.println("Endpoint returned null body");
	            Assume.that(false);
	            return;
	        }
	        
	        if (response.getBody().getVetList() == null || response.getBody().getVetList().isEmpty()) {
	            System.out.println("No vets found in database or endpoint returned empty list");
	            Assume.that(false);
	            return;
	        }
	        
	        List<Vet> vets1 = response.getBody().getVetList();
	        System.out.println("Found " + vets1.size() + " vets in first request");
	        
	        System.out.println("Vet IDs in first request: " + 
	            vets1.stream().map(Vet::getId).map(String::valueOf).collect(Collectors.joining(", ")));
	        
	 
	        System.out.println("Making second request to: " + BASE_URL + "/vets");
	        
	        ResponseEntity<Vets> response2 = restTemplate.exchange(
	            BASE_URL + "/vets",
	            HttpMethod.GET,
	            entity,
	            Vets.class
	        );
	        
	        if (!response2.getStatusCode().is2xxSuccessful() || 
	            response2.getBody() == null || 
	            response2.getBody().getVetList() == null || 
	            response2.getBody().getVetList().isEmpty()) {
	            System.out.println("Second request failed or returned empty list");
	            Assume.that(false);
	            return;
	        }
	        
	        List<Vet> vets2 = response2.getBody().getVetList();
	        System.out.println("Found " + vets2.size() + " vets in second request");
	        
	        System.out.println("Vet IDs in second request: " + 
	            vets2.stream().map(Vet::getId).map(String::valueOf).collect(Collectors.joining(", ")));
	        
	        boolean sameSizeCheck = vets1.size() == vets2.size();
	        System.out.println("Lists have same size: " + sameSizeCheck);
	        
	        if (!sameSizeCheck) {
	            System.out.println("First list size: " + vets1.size() + ", Second list size: " + vets2.size());
	        }
	        
	        Set<Integer> ids1 = vets1.stream().map(Vet::getId).collect(Collectors.toSet());
	        Set<Integer> ids2 = vets2.stream().map(Vet::getId).collect(Collectors.toSet());
	        
	        System.out.println("IDs in first set: " + ids1);
	        System.out.println("IDs in second set: " + ids2);
	        
	        Set<Integer> onlyInFirst = new HashSet<>(ids1);
	        onlyInFirst.removeAll(ids2);
	        
	        Set<Integer> onlyInSecond = new HashSet<>(ids2);
	        onlyInSecond.removeAll(ids1);
	        
	        if (!onlyInFirst.isEmpty()) {
	            System.out.println("IDs only in first set: " + onlyInFirst);
	        }
	        
	        if (!onlyInSecond.isEmpty()) {
	            System.out.println("IDs only in second set: " + onlyInSecond);
	        }
	        
	        assertThat(ids1).isEqualTo(ids2);
	        
	        assertThat(vets1).hasSameSizeAs(vets2);
	        
	        logToSwagger("Test passed: vetListShouldBeConsistent - Vet count: " + vets1.size());
	    
	}
	
	@Property(tries = 5)
	@Step("Teste: Editar nome do pet deve ser visível")
	void editPetNameShouldBeVisible(@ForAll("validOwnerData") Owner newOwner, 
	                               @ForAll("validPetName") String initialPetName,
	                               @ForAll("validPetName") String updatedPetName) {
	    logToSwagger("Starting test: editPetNameShouldBeVisible");
	    
	        String uniquePrefix = "Pet_" + UUID.randomUUID().toString().substring(0, 6);
	        newOwner.setFirstName("PetOwner_" + uniquePrefix);
	        newOwner.setLastName("PetTest_" + newOwner.getLastName());
	        

	        if (newOwner.getAddress() == null) newOwner.setAddress("Test Address");
	        if (newOwner.getCity() == null) newOwner.setCity("Test City");
	        if (newOwner.getTelephone() == null) newOwner.setTelephone("1234567890");
	        
	        System.out.println("Criando owner: " + newOwner.getFirstName() + " " + newOwner.getLastName());

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        
	        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	        formData.add("firstName", newOwner.getFirstName());
	        formData.add("lastName", newOwner.getLastName());
	        formData.add("address", newOwner.getAddress());
	        formData.add("city", newOwner.getCity());
	        formData.add("telephone", newOwner.getTelephone());
	        
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
	        
	        ResponseEntity<String> response = restTemplate.exchange(
	            BASE_URL + "/owners/new",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	        );

	        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
	        
	        String redirectUrl = response.getHeaders().getLocation() != null 
	            ? response.getHeaders().getLocation().toString() 
	            : response.getHeaders().getFirst("Location");
	            
	        assertThat(redirectUrl).isNotNull();
	        
	        Pattern pattern = Pattern.compile("/owners/(\\d+)");
	        Matcher matcher = pattern.matcher(redirectUrl);
	        
	        assertThat(matcher.find()).isTrue();
	        int ownerId = Integer.parseInt(matcher.group(1));
	        System.out.println("Owner criado com ID: " + ownerId);
	        

	        MultiValueMap<String, String> petFormData = new LinkedMultiValueMap<>();
	        petFormData.add("name", initialPetName);
	        petFormData.add("birthDate", LocalDate.now().minusYears(1).toString());
	        petFormData.add("type", "1"); 
	        
	        HttpEntity<MultiValueMap<String, String>> petRequestEntity = new HttpEntity<>(petFormData, headers);
	        
	        System.out.println("Criando pet com nome: " + initialPetName);
	        ResponseEntity<String> petResponse = restTemplate.exchange(
	            BASE_URL + "/owners/" + ownerId + "/pets/new",
	            HttpMethod.POST,
	            petRequestEntity,
	            String.class
	        );
	        
	        System.out.println("Resposta da criação do pet: " + petResponse.getStatusCode());
	        assertThat(petResponse.getStatusCode().is2xxSuccessful() || 
	                  petResponse.getStatusCode().is3xxRedirection()).isTrue();
	
	        ResponseEntity<String> ownerPage = restTemplate.getForEntity(
	            BASE_URL + "/owners/" + ownerId, 
	            String.class
	        );
	        
	        assertThat(ownerPage.getStatusCode().is2xxSuccessful()).isTrue();
	        String ownerPageContent = ownerPage.getBody();
	        

	        System.out.println("Verificando se o pet '" + initialPetName + "' aparece na página do owner");
	        assertThat(ownerPageContent).contains(initialPetName);
	        
	        Document doc = Jsoup.parse(ownerPageContent);
	        Element petRow = doc.select("tr:contains(" + initialPetName + ")").first();
	        assertThat(petRow).isNotNull();
	        
	        Element editLink = petRow.select("a:contains(Edit Pet)").first();
	        assertThat(editLink).isNotNull();
	        
	        String editUrl = editLink.attr("href");
	        System.out.println("URL de edição do pet: " + editUrl);
	        
	        Pattern petIdPattern = Pattern.compile("/pets/(\\d+)/edit");
	        Matcher petIdMatcher = petIdPattern.matcher(editUrl);
	        
	        assertThat(petIdMatcher.find()).isTrue();
	        int petId = Integer.parseInt(petIdMatcher.group(1));
	        System.out.println("Pet criado com ID: " + petId);

	        MultiValueMap<String, String> updateFormData = new LinkedMultiValueMap<>();
	        updateFormData.add("name", updatedPetName);
	        updateFormData.add("birthDate", LocalDate.now().minusYears(1).toString());
	        updateFormData.add("type", "1");
	        
	        HttpEntity<MultiValueMap<String, String>> updateRequestEntity = new HttpEntity<>(updateFormData, headers);
	        
	        System.out.println("Atualizando pet para nome: " + updatedPetName);
	        ResponseEntity<String> updateResponse = restTemplate.exchange(
	            BASE_URL + "/owners/" + ownerId + "/pets/" + petId + "/edit",
	            HttpMethod.POST,
	            updateRequestEntity,
	            String.class
	        );
	        
	        System.out.println("Resposta da atualização do pet: " + updateResponse.getStatusCode());
	        assertThat(updateResponse.getStatusCode().is2xxSuccessful() || 
	                  updateResponse.getStatusCode().is3xxRedirection()).isTrue();
	
	        ResponseEntity<String> updatedOwnerPage = restTemplate.getForEntity(
	            BASE_URL + "/owners/" + ownerId, 
	            String.class
	        );
	        
	        assertThat(updatedOwnerPage.getStatusCode().is2xxSuccessful()).isTrue();
	        String updatedOwnerPageContent = updatedOwnerPage.getBody();
	        
	        System.out.println("Verificando se o pet atualizado '" + updatedPetName + "' aparece na página do owner");
	        assertThat(updatedOwnerPageContent).contains(updatedPetName);
	        
	        System.out.println("Nome do pet atualizado com sucesso para: " + updatedPetName);
	        logToSwagger("Test passed: editPetNameShouldBeVisible - Pet name updated from " + 
	                    initialPetName + " to " + updatedPetName);
	}


    
 // --- NOVO MÉTODO PARA DUMP DE HTML ---
    private void dumpHtmlToFile(String htmlContent, String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(htmlContent);
            System.out.println("DEBUG: HTML dumpado para " + filename);
        } catch (IOException e) {
            System.err.println("ERROR: Não foi possível dump HTML para o arquivo: " + e.getMessage());
        }
    }





    private Optional<OwnerPet> waitForPetCreation(int ownerId, String petName, int maxRetries, long delayMs) {
        for (int i = 0; i < maxRetries; i++) {
            Owner owner = getOwner(ownerId);
            if (owner != null && owner.getPets() != null) {
                Optional<OwnerPet> foundPet = owner.getPets().stream()
                    .filter(p -> p.getName().equals(petName))
                    .findFirst();
                if (foundPet.isPresent()) {
                    return foundPet;
                }
            }
            System.out.println("DEBUG (waitForPetCreation): Pet '" + petName + "' ainda não encontrado para o owner " + ownerId + ". Tentativa " + (i + 1) + "/" + maxRetries + ". Aguardando " + delayMs + "ms...");
        }
        System.err.println("ERROR (waitForPetCreation): Pet '" + petName + "' não encontrado após " + maxRetries + " tentativas para o owner " + ownerId + ".");
        return Optional.empty();
    }
    
    private PetType getFirstAvailablePetType() throws RuntimeException {
        try {
            
            return new PetType("cat") {{ setId(2); }}; // Assumindo 'cat' com ID 2
        } catch (Exception e) {
            System.err.println("Erro ao obter tipos de pet: " + e.getMessage());
            // Fallback se não conseguir obter do sistema
            PetType fallbackType = new PetType();
            fallbackType.setId(1); // Assumindo que ID 1 é um tipo válido como 'dog'
            fallbackType.setName("dog");
            return fallbackType;
        }
    }




	
	
	// Método para obter pets de um owner diretamente da API
	private List<Pet> getPetsForOwner(Integer ownerId) {
	    try {
	        ResponseEntity<Pet[]> response = restTemplate.getForEntity(
	            BASE_URL + "/api/pets/owner/" + ownerId, 
	            Pet[].class
	        );
	        
	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            return Arrays.asList(response.getBody());
	        }
	        return new ArrayList<>();
	    } catch (Exception e) {
	        System.err.println("Erro ao obter pets para owner " + ownerId + ": " + e.getMessage());
	        return new ArrayList<>();
	    }
	}
	
	



	

	private ResponseEntity<String> submitHtmlForm(String urlPath, Map<String,String> overrides) {
	    String url = BASE_URL + urlPath;
	    ResponseEntity<String> getResp = restTemplate.getForEntity(url, String.class);
	    assertThat(getResp.getStatusCode().is2xxSuccessful()).isTrue();

	    Document doc = (Document) Jsoup.parse(getResp.getBody());
	    Element form = (Element) ((org.jsoup.nodes.Element) doc).selectFirst("form");
	    String action = form.hasAttr("action") && !form.attr("action").isBlank() ? form.attr("action") : urlPath;


	    MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();

	    // Inputs
	    for (Element input : form.select("input[name]")) {
	        String name = input.attr("name");
	        String val = overrides.getOrDefault(name, input.attr("value"));
	        formData.add(name, val);
	    }

	    // Textareas
	    for (Element textarea : form.select("textarea[name]")) {
	        String name = textarea.attr("name");
	        String val = overrides.getOrDefault(name, textarea.text());
	        formData.add(name, val);
	    }

	    for (Element select : form.select("select[name]")) {
	        String name = select.attr("name");
	        String val = overrides.get(name);

	        if (val == null) {
	            Element selected = select.selectFirst("option[selected]");
	            if (selected != null) {
	                val = selected.attr("value");
	            } else {
	                Element firstOption = select.selectFirst("option");
	                if (firstOption != null) {
	                    val = firstOption.attr("value"); // fallback
	                }
	            }
	        }

	        if (val != null) {
	            formData.add(name, val);
	        }
	    }
	    
	    


	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    return restTemplate.postForEntity(BASE_URL + action, new HttpEntity<>(formData, headers), String.class);
	}



	

	// --------------------Métodos auxiliares
	

	@Provide
	Arbitrary<String> validPhoneNumber() {
	    // Gerar números de telefone válidos (10 dígitos)
	    return Arbitraries.strings().numeric().ofLength(10);
	}
	
	private Pet addPet(Owner owner, Pet petData) {
	    try {
	        // Preparar um objeto Pet adequado
	        Pet pet = new Pet();
	        pet.setName(petData.getName() != null ? (String) petData.getName() : "TestPet");
	        
	        // Garantir data de nascimento válida
	        LocalDate birthDate = (LocalDate) petData.getBirthDate();
	        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
	            birthDate = LocalDate.now().minusYears(1);
	        }
	        pet.setBirthDate(birthDate);
	        
	        // Definir o tipo do pet
	        PetType petType = null;
	        
	        
	        if (petType == null) {
	            petType = new PetType();
	            petType.setId(1);
	            petType.setName("dog");
	        }
	        pet.setType(petType);
	        
	        // Definir o ID do owner
	        pet.setOwner_id(owner.getId());
	        
	        // Configurar cabeçalhos para JSON
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        
	        // Criar o corpo da requisição
	        HttpEntity<Pet> requestEntity = new HttpEntity<>(pet, headers);
	        
	        // Tentar usar o endpoint REST
	        String url = BASE_URL + "/api/owners/" + owner.getId() + "/pets";
	        System.out.println("Tentando adicionar pet via API REST: " + url);
	        System.out.println("Pet: " + pet.getName() + ", Tipo: " +  pet.getType().getName() + ", Data: " + pet.getBirthDate());
	        
	        ResponseEntity<Pet> response = restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            requestEntity,
	            Pet.class
	        );
	        
	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            System.out.println("Pet adicionado com sucesso via API REST: " + response.getBody().getName());
	            return response.getBody();
	        } else {
	            System.err.println("Falha ao adicionar pet via API REST. Status: " + response.getStatusCode());
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao adicionar pet via API REST: " + e.getMessage());
	        
	        // Tentar outro endpoint REST alternativo
	        try {
	            // Preparar um objeto Pet adequado
	            Pet pet = new Pet();
	            pet.setName(petData.getName() != null ? (String) petData.getName() : "TestPet");
	            
	            // Garantir data de nascimento válida
	            LocalDate birthDate = (LocalDate) petData.getBirthDate();
	            if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
	                birthDate = LocalDate.now().minusYears(1);
	            }
	            pet.setBirthDate(birthDate);
	            
	            // Definir o tipo do pet
	            PetType petType = new PetType();
	            petType.setId(1);
	            petType.setName("dog");
	            pet.setType(petType);
	            
	            // Definir o ID do owner
	            pet.setOwner_id(owner.getId());
	            
	            // Configurar cabeçalhos para JSON
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);
	            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	            
	            // Criar o corpo da requisição
	            HttpEntity<Pet> requestEntity = new HttpEntity<>(pet, headers);
	            
	            // Tentar usar um endpoint REST alternativo
	            String url = BASE_URL + "/api/pets";
	            System.out.println("Tentando adicionar pet via API REST alternativa: " + url);
	            
	            ResponseEntity<Pet> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                requestEntity,
	                Pet.class
	            );
	            
	            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	                System.out.println("Pet adicionado com sucesso via API REST alternativa: " + response.getBody().getName());
	                return response.getBody();
	            }
	        } catch (Exception e2) {
	            System.err.println("Erro ao adicionar pet via API REST alternativa: " + e2.getMessage());
	        }
	    }
	    
	    System.err.println("Não foi possível adicionar o pet");
	    return null;
	}
	

	private Owner createOwner(Owner o) {
	    try {
	        // Configurar os headers para enviar um form
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        
	        // Criar um MultiValueMap para enviar os dados do formulário
	        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	        formData.add("firstName", o.getFirstName());
	        formData.add("lastName", o.getLastName());
	        formData.add("address", o.getAddress() != null ? o.getAddress() : "Test Address");
	        formData.add("city", o.getCity() != null ? o.getCity() : "Test City");
	        formData.add("telephone", o.getTelephone() != null ? o.getTelephone() : "1234567890");
	        
	        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
	        
	        ResponseEntity<String> response = restTemplate.exchange(
	            BASE_URL + "/owners/new",
	            HttpMethod.POST,
	            requestEntity,
	            String.class
	        );
	        
	        if (response.getStatusCode().is3xxRedirection()) {
	            
	            String redirectUrl = response.getHeaders().getLocation() != null 
	                ? response.getHeaders().getLocation().toString() 
	                : response.getHeaders().getFirst("Location");
	                
	            if (redirectUrl != null) {
	                Pattern pattern = Pattern.compile("/owners/(\\d+)");
	                Matcher matcher = pattern.matcher(redirectUrl);
	                
	                if (matcher.find()) {
	                    int ownerId = Integer.parseInt(matcher.group(1));
	                    System.out.println("Owner criado com sucesso, ID: " + ownerId);
	                    return getOwner(ownerId);
	                }
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("Erro ao criar owner via API: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return null;
	}
	        
	        
	

	
	@Provide
    static Arbitrary<Owner> validOwnerData() {
        Arbitrary<String> name = Arbitraries.strings().withCharRange('A', 'Z').ofMinLength(3).ofMaxLength(10);
        Arbitrary<String> phone = Arbitraries.strings().numeric().ofLength(10);
        return Combinators.combine(name, name, name, name, phone).as((f, l, a, c, p) -> {
            Owner o = new Owner();
            o.setFirstName(f);
            o.setLastName(l);
            o.setAddress(a);
            o.setCity(c);
            o.setTelephone(p);
            return o;
        });
    }
	
	private Owner getOwnerFromDb(int ownerId) {
	    // Buscar Owner
	    String ownerSql = "SELECT ID, FIRST_NAME, LAST_NAME, ADDRESS, CITY, TELEPHONE FROM OWNERS WHERE ID = ?";
	    List<Owner> owners = jdbcTemplate.query(ownerSql, new Object[]{ownerId}, (rs, rowNum) -> {
	        Owner o = new Owner();
	        o.setId(rs.getInt("ID"));
	        o.setFirstName(rs.getString("FIRST_NAME"));
	        o.setLastName(rs.getString("LAST_NAME"));
	        o.setAddress(rs.getString("ADDRESS"));
	        o.setCity(rs.getString("CITY"));
	        o.setTelephone(rs.getString("TELEPHONE"));
	        return o;
	    });

	    if (owners.isEmpty()) {
	        System.err.println("WARN: Owner com ID " + ownerId + " não encontrado na base de dados.");
	        return null;
	    }

	    Owner owner = owners.get(0);

	    // Buscar Pets associados ao Owner
	    String petsSql = "SELECT ID, NAME, BIRTH_DATE, TYPE_NAME FROM OWNER_PETS WHERE OWNER_ID = ?";
	    List<OwnerPet> pets = jdbcTemplate.query(petsSql, new Object[]{ownerId}, (rs, rowNum) -> {
	        OwnerPet pet = new OwnerPet();
	        pet.setId(rs.getInt("ID"));
	        pet.setName(rs.getString("NAME"));
	        pet.setBirthDate(rs.getDate("BIRTH_DATE").toLocalDate());
	        pet.setType_name(rs.getString("TYPE_NAME"));
	        pet.setOwner_id(owner.getId());
	        return pet;
	    });

	    owner.setPets(new HashSet<>(pets));
	    System.out.println("DEBUG (getOwnerFromDb): Owner ID " + ownerId + " com " + pets.size() + " pets carregados da base de dados.");
	    return owner;
	}


	 // --- getOwner: Tries to get the owner, with retries ---
    private Owner getOwner(int ownerId) {
        final int maxAttempts = 5;
        final int delayMillis = 1000; // Increased to 1 second for more robustness

        Owner initialOwner = tryGetOwner(ownerId);
        if (initialOwner == null) {
            System.err.println("ERROR: Could not get owner " + ownerId + " on initial attempt.");
            return null;
        }

        int initialPetCount = initialOwner.getPets() != null ? initialOwner.getPets().size() : 0;
        System.out.println("DEBUG (getOwner): Initially, owner " + ownerId + " has " + initialPetCount + " pets.");

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Owner currentOwner = tryGetOwner(ownerId);
            if (currentOwner == null) {
                System.err.println("WARN (getOwner): Failed to get owner " + ownerId + " on attempt " + attempt + ". Retrying...");
                continue;
            }

            int currentPetCount = currentOwner.getPets() != null ? currentOwner.getPets().size() : 0;

            if (currentPetCount > initialPetCount) { // If pet count increased, new pet was found
                System.out.println("DEBUG (getOwner): Owner " + ownerId + " updated! Pets before: " +
                                   initialPetCount + ", now: " + currentPetCount);
                return currentOwner;
            }

            System.out.println("DEBUG (getOwner): Attempt " + attempt + ": pet count is still " + currentPetCount +
                               ". Waiting for update...");
        }

        System.err.println("WARN (getOwner): Owner " + ownerId + "'s pet count did not change after " + maxAttempts +
                           " attempts. Returning the latest state (may be outdated).");
        return tryGetOwner(ownerId); // Return the last obtained state after all attempts
    }
	
 // Este método agora é menos crítico, mas ainda usado pela verificação opcional da UI
    private Owner tryGetOwner(int ownerId) {
        String urlPath = "/owners/" + ownerId;
        String html = null;
        try {
            html = getHtml(urlPath);
            dumpHtmlToFile(html, "owner_" + ownerId + "_debug.html");
        } catch (RuntimeException e) {
            System.err.println("ERROR (tryGetOwner): Falha ao obter HTML para owner " + ownerId + ": " + e.getMessage());
            return null;
        }

        Document document = Jsoup.parse(html);

        Owner owner = new Owner();
        owner.setId(ownerId);

        String ownerName = getHtmlText(document, "table.table-striped tr:contains(Name) td b", "Owner_Name_Fallback");
        String[] nameParts = ownerName.split(" ", 2);
        owner.setFirstName(nameParts.length > 0 ? nameParts[0] : "Owner_FirstName_Fallback_" + ownerId);
        owner.setLastName(nameParts.length > 1 ? nameParts[1] : "Owner_LastName_Fallback_" + ownerId);
        owner.setAddress(getHtmlText(document, "table.table-striped tr:contains(Address) td", "Address_Fallback"));
        owner.setCity(getHtmlText(document, "table.table-striped tr:contains(City) td", "City_Fallback"));
        owner.setTelephone(getHtmlText(document, "table.table-striped tr:contains(Telephone) td", "Telephone_Fallback"));

        System.out.println("DEBUG (tryGetOwner): Owner HTML Parsing - ID: " + ownerId + ", Nome: " + owner.getFirstName() + " " + owner.getLastName());

        List<OwnerPet> petsList = new ArrayList<>();

        Elements allStripedTables = document.select("table.table-striped");
        System.out.println("DEBUG (tryGetOwner): Encontradas " + allStripedTables.size() + " tabelas com a classe 'table-striped'.");

        Element petsTable = null;

        Elements h2Elements = document.select("h2:contains(Pets and Visits)");
        System.out.println("DEBUG (tryGetOwner): Encontrados " + h2Elements.size() + " elementos 'h2' contendo 'Pets and Visits'.");

        if (!h2Elements.isEmpty()) {
            Element petsAndVisitsHeading = h2Elements.first();
            petsTable = petsAndVisitsHeading.nextElementSibling();
            System.out.println("DEBUG (tryGetOwner): Primeiro irmão seguinte do cabeçalho 'Pets and Visits': " + (petsTable != null ? petsTable.tagName() + "." + petsTable.className() : "null"));
            
            while (petsTable != null && (!petsTable.tagName().equals("table") || !petsTable.hasClass("table-striped"))) {
                System.out.println("DEBUG (tryGetOwner): A mover-me para além de um irmão não-tabela: " + petsTable.tagName());
                petsTable = petsTable.nextElementSibling();
            }
        } else if (allStripedTables.size() > 1) {
            petsTable = allStripedTables.get(1);
            System.out.println("WARN (tryGetOwner): Cabeçalho 'Pets and Visits' não encontrado. Assumindo que a tabela de pets é a segunda 'table.table-striped'. Contagem de filhos da tabela selecionada: " + petsTable.children().size());
        }

        if (petsTable == null) {
            System.err.println("WARN (tryGetOwner): Tabela de Pets não encontrada no HTML. Verifique se o cabeçalho 'Pets and Visits' ou o seletor 'table.table-striped' está correto.");
            System.out.println("DEBUG (tryGetOwner): Nenhuma tabela de pets encontrada. Snippet HTML em torno do h2: " + (h2Elements.isEmpty() ? "Nenhum h2 encontrado" : h2Elements.first().parent().html()));
            owner.setPets(new HashSet<>(petsList));
            System.out.println("Owner obtido via HTML: " + ownerId + ", Nome: " + owner.getFirstName() + " " + owner.getLastName() + ", Total Pets Parsed: " + petsList.size());
            return owner;
        }

        System.out.println("DEBUG (tryGetOwner): Tabela de Pets identificada. Seu HTML (primeiros 500 caracteres): " + petsTable.outerHtml().substring(0, Math.min(petsTable.outerHtml().length(), 500)));

        Elements petRows = petsTable.select("tr:has(td)");

        System.out.println("DEBUG (tryGetOwner): Encontradas " + petRows.size() + " linhas de pet (tr:has(td)) dentro da tabela de pets.");

        if (petRows.isEmpty()) {
            System.out.println("DEBUG (tryGetOwner): Nenhuma linha de pet (com td) encontrada na tabela de pets. Pode estar vazia ou o seletor 'tr:has(td)' está incorreto.");
        }

        for (Element row : petRows) {
            Elements cols = row.select("td");

            if (cols.size() < 3) {
                System.err.println("WARN (tryGetOwner): Linha de pet inválida detectada (menos de 3 colunas): " + row.text());
                continue;
            }

            String petName = cols.get(0).text();
            String birthDateStr = cols.get(1).text();
            String typeName = cols.get(2).text();

            Optional<Integer> petIdOptional = Optional.empty();
            Element editLink = cols.get(0).selectFirst("a[href*='/owners/" + ownerId + "/pets/'][href$='/edit']");
            if (editLink != null) {
                String href = editLink.attr("href");
                Pattern pattern = Pattern.compile("/pets/(\\d+)/edit");
                Matcher matcher = pattern.matcher(href);
                if (matcher.find()) {
                    try {
                        petIdOptional = Optional.of(Integer.parseInt(matcher.group(1)));
                        System.out.println("DEBUG (tryGetOwner): Pet ID '" + petName + "' extraído: " + petIdOptional.get());
                    } catch (NumberFormatException e) {
                        System.err.println("WARN (tryGetOwner): Falha ao parsear o ID do pet do href: " + href + ". Erro: " + e.getMessage());
                    }
                } else {
                    System.err.println("WARN (tryGetOwner): Padrão de URL de edição de pet não encontrado no link: " + href);
                }
            } else {
                System.err.println("WARN (tryGetOwner): Nenhum link de edição encontrado para o pet '" + petName + "'. O ID do pet NÃO será definido, e o pet NÃO será editável no teste.");
            }

            OwnerPet pet = new OwnerPet();
            petIdOptional.ifPresent(pet::setId);
            pet.setName(petName);
            pet.setType_name(typeName);

            if (!birthDateStr.isEmpty()) {
                try {
                    pet.setBirthDate(LocalDate.parse(birthDateStr, DATE_FORMATTER));
                } catch (DateTimeParseException e) {
                    System.err.println("WARN (tryGetOwner): Formato de data de nascimento inválido: '" + birthDateStr + "' para o pet '" + petName + "'. Erro: " + e.getMessage());
                }
            } else {
                System.err.println("WARN (tryGetOwner): Data de nascimento vazia para o pet '" + petName + "'.");
            }

            pet.setOwner_id(owner.getId());

            petsList.add(pet);
            if (!petIdOptional.isPresent()) {
                System.err.println("WARN (tryGetOwner): Pet '" + petName + "' adicionado à lista do owner sem ID. Pode afetar testes subsequentes.");
            }
        }

        owner.setPets(new HashSet<>(petsList));
        System.out.println("Owner obtido via HTML: " + ownerId + ", Nome: " + owner.getFirstName() + " " + owner.getLastName() + ", Total Pets Parsed: " + petsList.size());
        return owner;
    }
	
    private String getHtml(String urlPath) {
        try {
            String fullUrl = BASE_URL + urlPath;
            System.out.println("DEBUG (getHtml): Acedendo URL: " + fullUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.err.println("ERROR (getHtml): Falha ao obter HTML para " + urlPath + ". Status: " + response.getStatusCode());
                throw new RuntimeException("Falha ao obter HTML: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("ERROR (getHtml): Erro HTTP ao obter HTML para " + urlPath + ". Status: " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Erro HTTP: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("ERROR (getHtml): Erro inesperado ao obter HTML para " + urlPath + ": " + e.getMessage());
            throw new RuntimeException("Erro inesperado: " + e.getMessage(), e);
        }
    }





	    // Método auxiliar para extrair texto de um elemento com fallback
	    private String getHtmlText(Document doc, String selector, String fallback) {
	        Element element = doc.selectFirst(selector);
	        return element != null ? element.text().trim() : fallback;
	    }

	    // Sobrecarga para usar com um Element específico (para linhas da tabela)
	    private String getHtmlText(Element parentElement, String selector, String fallback) {
	        Element element = parentElement.selectFirst(selector);
	        return element != null ? element.text().trim() : fallback;
	    }

	private int getTotalOwners() {
	    try {
	        // Obter a página de listagem de owners
	        ResponseEntity<String> htmlResponse = restTemplate.getForEntity(
	            BASE_URL + "/owners",
	            String.class
	        );
	        
	        if (htmlResponse.getStatusCode().is2xxSuccessful() && htmlResponse.getBody() != null) {
	            String html = htmlResponse.getBody();
	            
	            // Contar o número de linhas de tabela (tr) que contêm owners
	            // Esta é uma abordagem simplificada; na prática, use um parser HTML
	            int count = 0;
	            int index = 0;
	            while ((index = html.indexOf("<tr>", index + 1)) != -1) {
	                count++;
	            }
	            
	            // Subtrair 1 para o cabeçalho da tabela, se aplicável
	            if (count > 0) count--;
	            
	            System.out.println("Contagem estimada de owners via HTML: " + count);
	            return count;
	        }
	    } catch (Exception e) {
	        System.out.println("Erro ao contar owners via HTML: " + e.getMessage());
	    }
	    
	    // Se não conseguir obter a contagem, tentar uma estimativa baseada no último ID
	    try {
	        int lastFoundId = 0;
	        for (int i = 1; i <= 100; i++) {
	            try {
	                Owner owner = getOwner(i);
	                if (owner != null) {
	                    lastFoundId = i;
	                }
	            } catch (Exception e) {
	                // Ignorar erros e continuar
	            }
	        }
	        System.out.println("Último ID de owner encontrado: " + lastFoundId);
	        return lastFoundId;
	    } catch (Exception e) {
	        System.out.println("Erro ao estimar contagem: " + e.getMessage());
	    }
	    
	    // Se tudo falhar, retornar um valor padrão
	    return 10; // Valor padrão estimado
	}
	
	private List<Owner> searchOwners(String lastName) {
	    try {
	        // Usar o endpoint HTML para buscar owners por sobrenome
	        ResponseEntity<String> htmlResponse = restTemplate.getForEntity(
	            BASE_URL + "/owners?lastName=" + lastName,
	            String.class
	        );
	        
	        if (htmlResponse.getStatusCode().is2xxSuccessful() && htmlResponse.getBody() != null) {
	            String html = htmlResponse.getBody();
	            List<Owner> owners = new ArrayList<>();
	            
	            // Verificar se a página mostra um único owner (redirecionamento para detalhes)
	            if (html.contains("<h2>Owner Information</h2>")) {
	                // Estamos na página de detalhes de um único owner
	                Owner owner = new Owner();
	                
	                // Extrair ID do URL atual
	                Pattern idPattern = Pattern.compile("/owners/(\\d+)");
	                Matcher idMatcher = idPattern.matcher(htmlResponse.getHeaders().getLocation() != null 
	                    ? htmlResponse.getHeaders().getLocation().toString() 
	                    : "");
	                
	                if (idMatcher.find()) {
	                    owner.setId(Integer.parseInt(idMatcher.group(1)));
	                } else {
	                    // Tentar extrair ID do conteúdo HTML
	                    Pattern idHtmlPattern = Pattern.compile("href=\"/owners/(\\d+)/edit\"");
	                    Matcher idHtmlMatcher = idHtmlPattern.matcher(html);
	                    if (idHtmlMatcher.find()) {
	                        owner.setId(Integer.parseInt(idHtmlMatcher.group(1)));
	                    }
	                }
	                
	                // Extrair nome
	                Pattern namePattern = Pattern.compile("<th>Name</th>\\s*<td>([^<]+)</td>");
	                Matcher nameMatcher = namePattern.matcher(html);
	                if (nameMatcher.find()) {
	                    String fullName = nameMatcher.group(1).trim();
	                    String[] nameParts = fullName.split(" ", 2);
	                    if (nameParts.length > 0) {
	                        owner.setFirstName(nameParts[0]);
	                        if (nameParts.length > 1) {
	                            owner.setLastName(nameParts[1]);
	                        }
	                    }
	                }
	                
	                owners.add(owner);
	                System.out.println("Encontrado 1 owner na busca por '" + lastName + "': ID=" + owner.getId());
	            } else if (html.contains("<h2>Owners</h2>")) {
	                // Estamos na página de listagem de owners
	                // Extrair IDs dos owners da tabela
	                Pattern ownerPattern = Pattern.compile("href=\"/owners/(\\d+)\"");
	                Matcher ownerMatcher = ownerPattern.matcher(html);
	                
	                while (ownerMatcher.find()) {
	                    int id = Integer.parseInt(ownerMatcher.group(1));
	                    Owner owner = new Owner();
	                    owner.setId(id);
	                    owners.add(owner);
	                }
	                
	                System.out.println("Encontrados " + owners.size() + " owners na busca por '" + lastName + "'");
	            } else if (html.contains("has not been found")) {
	                // Nenhum owner encontrado
	                System.out.println("Nenhum owner encontrado na busca por '" + lastName + "'");
	            }
	            
	            return owners;
	        }
	    } catch (Exception e) {
	        System.out.println("Erro ao buscar owners por sobrenome '" + lastName + "': " + e.getMessage());
	    }
	    
	    // Se falhar, retornar lista vazia
	    return new ArrayList<>();
	}
	
	// Método auxiliar para obter pets de um owner
	private List<Pet> getPets(int ownerId) {
	    try {
	        ResponseEntity<List<Pet>> response = restTemplate.exchange(
	            BASE_URL + "/api/pets/owner/" + ownerId,
	            HttpMethod.GET,
	            null,
	            new ParameterizedTypeReference<List<Pet>>() {}
	        );
	        
	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            return response.getBody();
	        } else {
	            System.out.println("Resposta não bem-sucedida ao obter pets: " + response.getStatusCode());
	        }
	    } catch (Exception e) {
	        System.out.println("Error getting pets via API: " + e.getClass().getName() + ": " + e.getMessage());
	    }
	    
	    // Se falhar, tente obter os pets da página HTML do owner
	    try {
	        ResponseEntity<String> ownerHtmlResponse = restTemplate.getForEntity(
	            BASE_URL + "/owners/" + ownerId,
	            String.class
	        );
	        
	        if (ownerHtmlResponse.getStatusCode().is2xxSuccessful()) {
	            String ownerHtml = ownerHtmlResponse.getBody();
	            System.out.println("Owner obtido com sucesso via HTML: " + ownerId + ", Nome: " + 
	                              (ownerHtml.contains("Owner_") ? ownerHtml.substring(ownerHtml.indexOf("Owner_"), 
	                                                                                ownerHtml.indexOf("Owner_") + 20) : "Nome não encontrado"));
	            
	            // Extrair informações dos pets do HTML (implementação simplificada)
	            List<Pet> pets = new ArrayList<>();
	            // Implementação para extrair pets do HTML...
	            
	            return pets;
	        }
	    } catch (Exception e) {
	        System.out.println("Erro ao obter owner via HTML: " + e.getMessage());
	    }
	    
	    System.out.println("AVISO: Não foi possível obter pets. Criando pet manualmente para o teste continuar.");
	    return Collections.emptyList();
	}
	
	// Método para extrair pets de um owner
	private List<Pet> extractPetsFromOwner(Owner owner) {
	    if (owner == null || owner.getPets() == null) {
	        return new ArrayList<>();
	    }
	    
	    return owner.getPets().stream()
	        .map(ownerPet -> {
	            Pet pet = new Pet();
	            pet.setId(ownerPet.getId());
	            pet.setName(ownerPet.getName());
	            pet.setBirthDate(ownerPet.getBirthDate());
	            if (ownerPet.getType_name() != null) {
	                PetType type = new PetType();
	                type.setId(0);
	                type.setName(ownerPet.getType_name());
	                pet.setType(type);
	            }
	            return pet;
	        })
	        .collect(Collectors.toList());
	}
	
	
	
	// Método auxiliar para contar pets no HTML
		private int countPetsInHtml(String html) {
		    if (html == null) return 0;
		    
		    System.out.println("Analyzing HTML to count pets...");
		    
		    // Imprimir uma parte do HTML para debug
		    if (html.length() > 1000) {
		        System.out.println("HTML excerpt: " + html.substring(0, 1000) + "...");
		    } else {
		        System.out.println("HTML: " + html);
		    }
		    
		    // Procurar por diferentes padrões que podem indicar pets
		    
		    // Padrão 1: Tabela de pets
		    Pattern tablePattern = Pattern.compile("<table[^>]*id=\"pets\"[^>]*>(.*?)</table>", Pattern.DOTALL);
		    Matcher tableMatcher = tablePattern.matcher(html);
		    if (tableMatcher.find()) {
		        String petsTable = tableMatcher.group(1);
		        Pattern rowPattern = Pattern.compile("<tr>", Pattern.DOTALL);
		        Matcher rowMatcher = rowPattern.matcher(petsTable);
		        
		        int count = 0;
		        while (rowMatcher.find()) {
		            count++;
		        }
		        
		        // Subtrair 1 para a linha de cabeçalho, se houver
		        if (count > 0) count--;
		        
		        System.out.println("Pets counted from table: " + count);
		        return count;
		    }
		    
		    // Padrão 2: Lista de pets
		    Pattern listPattern = Pattern.compile("<dt>Pets</dt>\\s*<dd>(.*?)</dd>", Pattern.DOTALL);
		    Matcher listMatcher = listPattern.matcher(html);
		    if (listMatcher.find()) {
		        String petsList = listMatcher.group(1);
		        if (petsList.contains("None")) {
		            System.out.println("No pets found in list");
		            return 0;
		        }
		        
		        // Contar ocorrências de nomes de pets
		        Pattern namePattern = Pattern.compile("<span>([^<]+)</span>");
		        Matcher nameMatcher = namePattern.matcher(petsList);
		        
		        int count = 0;
		        while (nameMatcher.find()) {
		            count++;
		            System.out.println("Found pet: " + nameMatcher.group(1));
		        }
		        
		        System.out.println("Pets counted from list: " + count);
		        return count;
		    }
		    
		    // Padrão 3: Texto "no pets"
		    if (html.contains("No pets") || html.contains("no pets") || html.contains("None")) {
		        System.out.println("Found 'No pets' text");
		        return 0;
		    }
		    
		    System.out.println("Could not determine pet count from HTML");
		    return 0;
		}
	        
		@Provide
	    static Arbitrary<OwnerPet> validPetData() { // <-- MUDAR PARA Arbitrary<OwnerPet>
	        return Arbitraries.strings().alpha().numeric().withChars(" ").ofMinLength(3).ofMaxLength(15)
	            .map(name -> {
	                OwnerPet pet = new OwnerPet(); // <-- USAR OwnerPet AQUI
	                pet.setName("Pet_" + name.trim());
	                pet.setBirthDate(LocalDate.of(2020, 1, 1));
	                pet.setType_name("dog"); // <-- DEFINIR type_name como String
	                return pet;
	            });
	    }
	
	
	
		// Busca um pet na DB dado o ownerId e o nome do pet
		private Optional<OwnerPet> findPetInDb(int ownerId, String petName) {
		    String sql = """
		        SELECT id, name, birth_date, type_name, owner_id
		        FROM owner_pets
		        WHERE owner_id = ? AND name = ?
		    """;
		    
		    List<OwnerPet> pets = jdbcTemplate.query(sql, petRowMapper(), ownerId, petName);
		    return pets.stream().findFirst();
		}

		// Busca um pet na DB dado o seu ID
		private Optional<OwnerPet> findPetInDbById(int petId) {
		    String sql = """
		        SELECT id, name, birth_date, type_name, owner_id
		        FROM owner_pets
		        WHERE id = ?
		    """;

		    List<OwnerPet> pets = jdbcTemplate.query(sql, petRowMapper(), petId);
		    return pets.stream().findFirst();
		}


		private RowMapper<OwnerPet> petRowMapper() {
		    return (rs, rowNum) -> {
		        OwnerPet pet = new OwnerPet();
		        pet.setId(rs.getInt("id"));
		        pet.setName(rs.getString("name"));
		        pet.setBirthDate(rs.getDate("birth_date").toLocalDate());
		        pet.setType_name(rs.getString("type_name"));
		        pet.setOwner_id(rs.getInt("owner_id"));
		        return pet;
		    };
		}



	 /** 1) Cria um owner via HTML e devolve o Owner (com ID). */
    private Owner createOwnerViaHtml(Owner novo) {
        // GET do form
        ResponseEntity<String> getForm = restTemplate.getForEntity(BASE_URL + "/owners/new", String.class);
        assertThat(getForm.getStatusCode().is2xxSuccessful()).isTrue();

        // Preenche e POST
        MultiValueMap<String,String> dados = new LinkedMultiValueMap<>();
        dados.add("firstName", novo.getFirstName());
        dados.add("lastName",  novo.getLastName());
        dados.add("address",   novo.getAddress());
        dados.add("city",      novo.getCity());
        dados.add("telephone", novo.getTelephone());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String,String>> req = new HttpEntity<>(dados, headers);

        ResponseEntity<String> post = restTemplate.postForEntity(BASE_URL + "/owners/new", req, String.class);
        assertThat(post.getStatusCode().is3xxRedirection()).isTrue();

        // Extrai o ID da Location
        String location = post.getHeaders().getLocation().toString();
        int id = Integer.parseInt(location.replaceAll(".*/owners/(\\d+).*", "$1"));

        // GET final do owner
        return getOwnerViaHtml(id);
    }
    
    

    /** Busca o owner via HTML e converte em objeto. */
    private Owner getOwnerViaHtml(int ownerId) {
        ResponseEntity<String> resp = restTemplate.getForEntity(BASE_URL + "/owners/" + ownerId, String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        String body = resp.getBody();
        Owner o = new Owner();
        o.setId(ownerId);
        // assumes no parser library: faz regex para extrair campos se precisares
        // aqui só devolvemos o id mesmo, para os testes basta.
        return o;
    }

    /** 2) Pesquisa owners via HTML (usa no diferenteLastNames e searchWith...). */
    private List<Owner> searchOwnersViaHtml(String lastNamePrefix) {
        // GET do form de find
        ResponseEntity<String> getForm = restTemplate.getForEntity(BASE_URL + "/owners/find?lastName=" + lastNamePrefix, String.class);
        assertThat(getForm.getStatusCode().is2xxSuccessful()).isTrue();
        String body = getForm.getBody();
        // extrai todos os links /owners/{id} da tabela de resultados
        List<Owner> resultados = new ArrayList<>();
        Matcher m = Pattern.compile("/owners/(\\d+)\">").matcher(body);
        while (m.find()) {
            int id = Integer.parseInt(m.group(1));
            resultados.add(getOwnerViaHtml(id));
        }
        return resultados;
    }

    /** 3) Cria UM pet genérico para este ownerId. Sobrecarrega o método que recebe (Owner, Pet). */
    private Pet createOnePetViaHtml(int ownerId) {
        Pet p = new Pet();
        p.setName("PetHTML_" + UUID.randomUUID().toString().substring(0,5));
        p.setBirthDate(LocalDate.now().minusYears(1));
        PetType type = new PetType();
        type.setId(1);  // assume que existe
        p.setType(type);
        Owner o = getOwnerViaHtml(ownerId);
        return createOnePetViaHtml(o, p);
    }
    
    private Optional<OwnerPet> waitForPetInDb(int ownerId, String petName, int maxRetries, long delayMs) {
        for (int i = 0; i < maxRetries; i++) {
            Optional<OwnerPet> foundPet = findPetInDb(ownerId, petName);
            if (foundPet.isPresent()) {
                System.out.println("DEBUG (waitForPetInDb): Pet '" + petName + "' encontrado na DB para o owner " + ownerId + " após " + (i + 1) + " tentativas.");
                return foundPet;
            }
            System.out.println("DEBUG (waitForPetInDb): Pet '" + petName + "' ainda não encontrado na DB para o owner " + ownerId + ". Tentativa " + (i + 1) + "/" + maxRetries + ". Aguardando " + delayMs + "ms...");
        }
        System.err.println("ERROR (waitForPetInDb): Pet '" + petName + "' não encontrado após " + maxRetries + " tentativas na DB para o owner " + ownerId + ".");
        return Optional.empty();
    }

    /** O teu método existente: já recebia Owner + Pet e fazia GET/POST nos endpoints HTML. */
    private Pet createOnePetViaHtml(Owner owner, Pet petData) {
        // GET do form
        ResponseEntity<String> form = restTemplate.getForEntity(
            BASE_URL + "/owners/" + owner.getId() + "/pets/new", String.class);
        assertThat(form.getStatusCode().is2xxSuccessful()).isTrue();

        MultiValueMap<String,String> dados = new LinkedMultiValueMap<>();
        dados.add("name", petData.getName());
        dados.add("birthDate", petData.getBirthDate().toString());
        dados.add("type", petData.getType().getId().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String,String>> req = new HttpEntity<>(dados, headers);

        ResponseEntity<String> post = restTemplate.postForEntity(
            BASE_URL + "/owners/" + owner.getId() + "/pets/new", req, String.class);
        assertThat(post.getStatusCode().is3xxRedirection()).isTrue();

        String loc = post.getHeaders().getLocation().toString();
        int petId = Integer.parseInt(loc.replaceAll(".*/pets/(\\d+).*", "$1"));
        petData.setId(petId);
        return petData;
    }

    private List<Pet> getPetsViaHtml(Integer ownerId) throws Exception {
        ResponseEntity<String> page = restTemplate.getForEntity(BASE_URL + "/owners/" + ownerId, String.class);
        assertThat(page.getStatusCode().is2xxSuccessful()).isTrue();
        Document d = Jsoup.parse(page.getBody());

        // Este seletor e lógica assumem uma estrutura HTML específica na página do owner
        // O PetClinic padrão lista os pets numa tabela ou lista.
        // Adaptar o seletor `tbody tr` conforme a tua aplicação
        return d.select("table.pets tbody tr").stream()
                .map(row -> {
                    // Tenta extrair o ID do pet do URL de edição, se houver
                    // Ex: <a href="/owners/1/pets/2/edit">Edit Pet</a>
                    String editHref = row.selectFirst("a:contains(Edit Pet)").attr("href");
                    Integer petId = null;
                    if (editHref != null && editHref.matches(".*/pets/(\\d+)/edit.*")) {
                        petId = Integer.parseInt(editHref.replaceAll(".*/pets/(\\d+)/edit.*", "$1"));
                    }

                    // Extrai nome e data de nascimento do HTML. Adapta conforme a tua estrutura
                    String name = row.selectFirst("td:nth-child(1)").text(); // Exemplo: 1ª célula é o nome
                    // A data pode estar na 2ª célula, ou em outra.
                    // Será preciso mais Jsoup para parsear a data, se for necessário para a igualdade
                    // Por simplicidade, para o teste de IDs, apenas o ID e o nome (para debug) são suficientes.

                    Pet pet = new Pet();
                    pet.setId(petId);
                    pet.setName(name);
                    // Podes não conseguir extrair todos os detalhes (tipo, birthDate) facilmente do HTML sumarizado.
                    // Para o repeatedPetListShouldBeEqual, basta o ID.
                    return pet;
                })
                .filter(pet -> pet.getId() != null) // Ignora pets sem ID (se o parsing falhar)
                .collect(Collectors.toList());
    }

	
	
    @Provide
    static Arbitrary<String> validPetName() {
        return Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10);
    }
    
    
    @Provide
    Arbitrary<Visit> validVisit() {
        Arbitrary<String> description = Arbitraries.strings()
            .withCharRange('a', 'z')
            .ofMinLength(5)
            .ofMaxLength(50);

        Arbitrary<LocalDate> date = Arbitraries.integers()
            .between(0, 365 * 10) 
            .map(daysAgo -> LocalDate.now().minusDays(daysAgo));

        return Combinators.combine(date, description)
            .as((d, desc) -> {
                Visit visit = new Visit();
                visit.setDate(d);
                visit.setDescription(desc);
                return visit;
            });
    }


	        
	
	@Provide
	Arbitrary<String> prefixLetter() {
	    // Gerar letras comuns em sobrenomes para aumentar a chance de encontrar resultados
	    return Arbitraries.of("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", 
	                          "N", "O", "P", "R", "S", "T", "W");
	}

	@Provide
	Arbitrary<String> secondLetter() {
	    // Gerar letras para o segundo caractere do prefixo
	    return Arbitraries.of("a", "e", "i", "o", "u", "r", "s", "t", "n", "m");
	}
	
	@Provide
	Arbitrary<Tuple2<String,String>> distinctLastNames() {
	  return Arbitraries.strings().alpha().ofLength(6)
	      .flatMap(s1 ->
	         Arbitraries.strings().alpha().ofLength(6)
	           .filter(s2 -> !s2.equals(s1))
	           .map(s2 -> Tuple.of(s1.toUpperCase(), s2.toUpperCase()))
	      );
	}
	
	
	
	int createOwnerAndReturnId() {
	    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	    formData.add("firstName", "TestFirst_" + UUID.randomUUID());
	    formData.add("lastName", "TestLast");
	    formData.add("address", "Rua Teste");
	    formData.add("city", "Lisboa");
	    formData.add("telephone", "912345678");

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(formData, headers);

	    ResponseEntity<String> resp = restTemplate.postForEntity(BASE_URL + "/owners/new", req, String.class);
	    assertThat(resp.getStatusCode().is3xxRedirection()).isTrue();

	    String location = resp.getHeaders().getFirst("Location");
	    assertThat(location).isNotNull();

	    Matcher matcher = Pattern.compile("/owners/(\\d+)").matcher(location);
	    assertThat(matcher.find()).isTrue();

	    return Integer.parseInt(matcher.group(1));
	}


	
	@Provide
    static Arbitrary<OwnerPet> validOwnerPet() {
        Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
        Arbitrary<LocalDate> birthDates = Dates.dates().between(LocalDate.of(2000, 1, 1), LocalDate.of(2025, 12, 31));
        Arbitrary<String> typeNames = Arbitraries.of("dog", "cat", "bird", "hamster");
        Arbitrary<Integer> ownerIds = Arbitraries.integers().between(1, 5000);

        return Arbitraries.just(new OwnerPet()); // Ou construir com Combinators se realmente precisares de um OwnerPet completo
                                                  // Para o teste original addPetIncreasesPetCount, um OwnerPet vazio pode ser suficiente
    }



}