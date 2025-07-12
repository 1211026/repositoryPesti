package org.springframework.samples.Owner.service;



import lombok.RequiredArgsConstructor;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;

import org.springframework.samples.Owner.OwnerExternalAPI;

import org.springframework.samples.Owner.OwnerPublicAPI;

import org.springframework.samples.Owner.model.Owner;

import org.springframework.samples.Owner.model.OwnerPet;

import org.springframework.samples.Owner.model.OwnerPet.Visit;



import org.springframework.samples.Pet.model.Pet;

import org.springframework.samples.Pet.model.PetType;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDate;

import java.util.ArrayList;

import java.util.Collection;

import java.util.HashSet;

import java.util.List;

import java.util.Optional;

import java.util.Set;



@Service

@RequiredArgsConstructor

public class OwnerService implements OwnerExternalAPI, OwnerPublicAPI {



private final OwnerRepository ownerRepository;

private final OwnerPetRepository ownerPetRepository;


@Autowired

public OwnerService(OwnerRepository ownerRepository, OwnerPetRepository ownerPetRepository) {

this.ownerRepository = ownerRepository;

this.ownerPetRepository = ownerPetRepository;

}



@Override

@Transactional(readOnly = true)

public Owner findById(Integer id) {

Owner owner = ownerRepository.findById(id);

if (owner != null) {

// Buscar os pets do owner

List<OwnerPet> pets = ownerPetRepository.findPetByOwnerId(id);

if (pets != null && !pets.isEmpty()) {

System.out.println("Encontrados " + pets.size() + " pets para o owner ID: " + id);

for (OwnerPet pet : pets) {

// Buscar as visitas para cada pet

Set<OwnerPet.Visit> visits = ownerPetRepository.findVisitByPetId(pet.getId());

// Adicionar o pet com suas visitas ao owner

addPetToOwner(owner, pet, visits);

System.out.println("Adicionado pet: " + pet.getName() + " (ID: " + pet.getId() + ") com " +

(visits != null ? visits.size() : 0) + " visitas");

}

} else {

System.out.println("Nenhum pet encontrado para o owner ID: " + id);

}

}

return owner;

}


private void addPetToOwner(Owner owner, OwnerPet ownerPet, Set<OwnerPet.Visit> visits) {

// CORREÇÃO AQUI: Use HashSet para inicializar

if (owner.getPets() == null) {

owner.setPets(new HashSet<>()); // MUDANÇA DE ArrayList para HashSet

}



// Converter OwnerPet para Pet (este bloco parece que está a duplicar OwnerPet,

// o que não seria necessário se OwnerPet fosse a entidade Pet real)

// Se OwnerPet é a entidade Pet, você simplesmente adicionaria ownerPet à coleção

// Sem a necessidade de criar um novo OwnerPet aqui.

// Vou manter a sua lógica existente para não introduzir mais mudanças agora,

// mas vale a pena rever este trecho.

OwnerPet pet = new OwnerPet();

pet.setId(ownerPet.getId());

pet.setName(ownerPet.getName());

pet.setBirthDate(ownerPet.getBirthDate());

pet.setOwner_id(owner.getId());



// Definir o tipo do pet

PetType type = new PetType();

type.setName(ownerPet.getType_name());

pet.setType(type);



// Adicionar visitas ao pet

if (visits != null && !visits.isEmpty()) {

for (OwnerPet.Visit visit : visits) {

Visit petVisit = new Visit(); // Isso parece ser OwnerPet.Visit

petVisit.setId(visit.id());

petVisit.setDate(visit.getVisit_date());

petVisit.setDescription(visit.getDescription());

pet.addVisit(petVisit);

}

}



// Adicionar o pet à lista de pets do owner

owner.getPets().add(pet);

}


@Override

@Transactional

public Integer save(Owner owner) {

if (owner.getId() != null) {

Owner existingOwner = ownerRepository.findById(owner.getId());

if (existingOwner != null) {

existingOwner.setFirstName(owner.getFirstName());

existingOwner.setLastName(owner.getLastName());

existingOwner.setAddress(owner.getAddress());

existingOwner.setCity(owner.getCity());

existingOwner.setTelephone(owner.getTelephone());



// Garantir que os pets sejam preservados

if (owner.getPets() != null && !owner.getPets().isEmpty()) {

// CORREÇÃO AQUI: Use HashSet para inicializar

if (existingOwner.getPets() == null) {

existingOwner.setPets(new HashSet<>()); // MUDANÇA DE ArrayList para HashSet

}



// Adicionar novos pets que não existem no owner existente

for (OwnerPet pet : owner.getPets()) {

boolean petExists = false;

for (OwnerPet existingPet : existingOwner.getPets()) {

if ((existingPet.getId() != null && existingPet.getId().equals(pet.getId())) ||

(existingPet.getName() != null && existingPet.getName().equals(pet.getName()))) {

petExists = true;

break;

}

}



if (!petExists) {

pet.setOwner_id(owner.getId());

existingOwner.getPets().add(pet);

System.out.println("Adicionado pet ao owner existente: " + pet.getName());



// Salvar o pet no repositório

savePet(pet);

}

}

}



ownerRepository.save(existingOwner);

return existingOwner.getId();

}

}



// Criar novo owner

Owner savedOwner = ownerRepository.save(owner);



// Salvar os pets do novo owner, se houver

if (owner.getPets() != null && !owner.getPets().isEmpty()) {

for (OwnerPet pet : owner.getPets()) {

pet.setOwner_id(savedOwner.getId());

savePet(pet);

}

}



return savedOwner.getId();

}


@Override

@Transactional

public void savePet(Pet pet) {

if (pet == null) {

throw new IllegalArgumentException("Pet não pode ser nulo");

}


if (pet.getOwner_id() == null || pet.getOwner_id() == 0) {

throw new IllegalArgumentException("Owner ID não pode ser nulo ou zero");

}


// Extrair os dados do pet

Integer id = pet.getId();

String name = pet.getName();

String typeName = pet.getType() != null ? pet.getType().getName() : "unknown";

Integer ownerId = pet.getOwner_id();

LocalDate birthDate = pet.getBirthDate() instanceof LocalDate ?

(LocalDate) pet.getBirthDate() :

LocalDate.now();


// Chamar o outro método savePet para evitar duplicação de código

savePet(id, name, typeName, ownerId, birthDate);


// Processar as visitas, se houver

Object visitsObj = pet.getVisits();

if (visitsObj != null && visitsObj instanceof Set) {

try {

@SuppressWarnings("unchecked")

Set<Pet.Visit> visits = (Set<Pet.Visit>) visitsObj;


for (Pet.Visit petVisit : visits) {

// Converter Pet.Visit para OwnerPet.Visit

OwnerPet.Visit ownerVisit = new OwnerPet.Visit(

petVisit.id(),

petVisit.description(),

petVisit.visit_date(),

pet.getId()

);


// Salvar a visita

ownerPetRepository.save(ownerVisit);

}

} catch (ClassCastException e) {

System.err.println("Erro ao processar visitas: " + e.getMessage());

e.printStackTrace();

}

}

}



@Override

@Transactional

public void savePet(Integer id, String name, String type, Integer owner_id, LocalDate birthdate) {

// 1. Verificar se o pet já existe

OwnerPet existingPet = null;

if (id != null) {

Optional<OwnerPet> existingPetOpt = ownerPetRepository.findById(id);

if (existingPetOpt.isPresent()) {

existingPet = existingPetOpt.get();

}

}



// 2. Criar ou atualizar o pet

OwnerPet pet = existingPet != null ? existingPet : new OwnerPet();

pet.setId(id);

pet.setName(name);

pet.setType_name(type);

pet.setOwner_id(owner_id);

pet.setBirthDate(birthdate);



// 3. Salvar o pet

ownerPetRepository.save(pet);

System.out.println("Pet salvo com parâmetros: " + name + " (ID: " + pet.getId() + ")");



// 4. Atualizar a associação com o owner

Owner owner = ownerRepository.findById(owner_id);

if (owner != null) {

// CORREÇÃO AQUI: Use HashSet para inicializar

if (owner.getPets() == null) {

owner.setPets(new HashSet<>()); // MUDANÇA DE ArrayList para HashSet

}



// Verificar se o pet já existe na lista do owner

boolean petExists = false;

for (OwnerPet p : owner.getPets()) {

if ((p.getId() != null && p.getId().equals(pet.getId())) ||

(p.getName() != null && p.getName().equals(name))) {

// Atualizar o pet existente

p.setName(name);

p.setBirthDate(birthdate);

p.setType_name(type);

petExists = true;

break;

}

}



// Adicionar o pet à lista do owner se não existir

if (!petExists) {

owner.getPets().add(pet);

System.out.println("Pet adicionado à lista do owner: " + name);

}



// Salvar o owner para atualizar a associação

ownerRepository.save(owner);

}

}



// Método auxiliar para processar uma visita com base em seu tipo real

private void processVisit(Object visitObj, Integer petId) {

try {

if (visitObj instanceof Visit) {

// Se for uma visita do módulo Pet

Visit petVisit =

(Visit) visitObj;


OwnerPet.Visit ownerVisit = new OwnerPet.Visit(

petVisit.getId(),

petVisit.getDescription(),

petVisit.getVisit_date(),

petId

);


ownerPetRepository.save(ownerVisit);

System.out.println("Visita do tipo Pet.model.Visit salva para o pet ID: " + petId);

}

else if (visitObj instanceof OwnerPet.Visit) {

// Se já for uma visita do módulo Owner

OwnerPet.Visit ownerVisit = (OwnerPet.Visit) visitObj;


// Garantir que o pet_id está correto

OwnerPet.Visit visitWithCorrectPetId = new OwnerPet.Visit(

ownerVisit.id(),

ownerVisit.getDescription(),

ownerVisit.getVisit_date(),

petId

);


ownerPetRepository.save(visitWithCorrectPetId);

System.out.println("Visita do tipo OwnerPet.Visit salva para o pet ID: " + petId);

}

else {

// Tipo desconhecido

System.out.println("Tipo de visita desconhecido: " +

(visitObj != null ? visitObj.getClass().getName() : "null"));

}

} catch (Exception e) {

System.err.println("Erro ao processar visita individual: " + e.getMessage());

e.printStackTrace();

}

}


@Override

@Transactional

public Page<Owner> findByLastName(String lastname, Pageable pageable) {

Page<Owner> pageOwner = ownerRepository.findByLastName(lastname, pageable);

List<Owner> ownerList = new ArrayList<>(pageOwner.getContent());


for (Owner owner : ownerList) {

List<OwnerPet> pets = ownerPetRepository.findPetByOwnerId(owner.getId());

if (pets != null && !pets.isEmpty()) {

for (OwnerPet pet : pets) {

Set<OwnerPet.Visit> visits = ownerPetRepository.findVisitByPetId(pet.getId());

addPetToOwner(owner, pet, visits);

}

}

}


return new PageImpl<>(ownerList, pageable, pageOwner.getTotalElements());

}



@Override

@Transactional(readOnly = true)

public List<OwnerPet> findPetByOwner(Integer owner_id) {

return ownerPetRepository.findPetByOwnerId(owner_id);

}



@Override

@Transactional(readOnly = true)

public Optional<Owner> findByName(String firstName, String lastName) {

return ownerRepository.findByName(firstName, lastName);

}





@Override

@Transactional(readOnly = true)

public List<OwnerPet.Visit> findVisitByPetId(Integer pet_id) {

Set<Visit> visits = ownerPetRepository.findVisitByPetId(pet_id);

return new ArrayList<>(visits);

}



@Override

@Transactional(readOnly = true)

public Owner findById(int ownerId) {

return findById(Integer.valueOf(ownerId));

}


@Override

@Transactional

public void deletePet(Integer petId) {

if (petId != null) {

ownerPetRepository.deletePet(petId);

System.out.println("Pet excluído: ID=" + petId);

}

}


@Override

@Transactional(readOnly = true)

public Pet findPetById(Integer petId) {

if (petId == null) {

return null;

}


Optional<OwnerPet> ownerPetOpt = ownerPetRepository.findById(petId);

if (ownerPetOpt.isPresent()) {

OwnerPet ownerPet = ownerPetOpt.get();


// Converter OwnerPet para Pet

Pet pet = new Pet();

pet.setId(ownerPet.getId());

pet.setName(ownerPet.getName());

pet.setBirthDate(ownerPet.getBirthDate());

pet.setOwner_id(ownerPet.getOwner_id());


// Definir o tipo do pet

PetType type = new PetType();

type.setName(ownerPet.getType_name());

pet.setType(type);


// Buscar as visitas do pet

Set<OwnerPet.Visit> visits = ownerPetRepository.findVisitByPetId(petId);


// Adicionar as visitas ao pet

if (visits != null && !visits.isEmpty()) {

for (OwnerPet.Visit visit : visits) {

Visit petVisit = new Visit();

petVisit.setId(visit.id());

petVisit.setDate(visit.getVisit_date());

petVisit.setDescription(visit.getDescription());

pet.addVisit(petVisit);

}

}


return pet;

}


return null;

}


@Override

@Transactional

public void savePet(OwnerPet pet) {

if (pet == null) {

throw new IllegalArgumentException("Pet não pode ser nulo");

}


if (pet.getOwner_id() == null || pet.getOwner_id() == 0) {

throw new IllegalArgumentException("Owner ID não pode ser nulo ou zero");

}


// Extrair os dados do pet

Integer id = pet.getId();

String name = pet.getName();

String typeName = pet.getType_name();

Integer ownerId = pet.getOwner_id();

LocalDate birthDate = pet.getBirthDate() instanceof LocalDate ?

(LocalDate) pet.getBirthDate() :

LocalDate.now();


// Chamar o método principal savePet para evitar duplicação de código

savePet(id, name, typeName, ownerId, birthDate);

}



@Override

@Transactional

public void saveVisit(Integer id, LocalDate date, String description, Integer pet_id) {

if (pet_id == null) {

throw new IllegalArgumentException("Pet ID não pode ser nulo");

}


// Verificar se o pet existe

Optional<OwnerPet> petOpt = ownerPetRepository.findById(pet_id);

if (!petOpt.isPresent()) {

throw new IllegalArgumentException("Pet não encontrado com ID: " + pet_id);

}


// Criar a visita

OwnerPet.Visit visit = new OwnerPet.Visit(id, description, date, pet_id);


// Salvar a visita no repositório

ownerPetRepository.save(visit);


// Adicionar a visita ao pet

OwnerPet pet = petOpt.get();

pet.addVisit(visit);


System.out.println("Visita salva: ID=" + id + ", Pet ID=" + pet_id + ", Data=" + date);

}



@Override

@Transactional

public void saveVisit(Visit visit) {

if (visit == null) {

throw new IllegalArgumentException("Visit não pode ser nula");

}


if (visit.getPet_id() == null) {

throw new IllegalArgumentException("Pet ID não pode ser nulo");

}


// Verificar se o pet existe

Optional<OwnerPet> petOpt = ownerPetRepository.findById(visit.getPet_id());

if (!petOpt.isPresent()) {

throw new IllegalArgumentException("Pet não encontrado com ID: " + visit.getPet_id());

}


// Salvar a visita no repositório

ownerPetRepository.save(visit);


// Adicionar a visita ao pet

OwnerPet pet = petOpt.get();

pet.addVisit(visit);


System.out.println("Visita salva diretamente: ID=" + visit.getId() + ", Pet ID=" + visit.getPet_id());

}



@Override

@Transactional(readOnly = true)

public int countPetsByOwnerId(Integer ownerId) {

if (ownerId == null) {

return 0;

}


List<OwnerPet> pets = ownerPetRepository.findPetByOwnerId(ownerId);

return pets != null ? pets.size() : 0;

}



@Override

@Transactional(readOnly = true)

public List<OwnerPet> findAllPets() {

return ownerPetRepository.findAll();

}





@Override

@Transactional

public void saveOwner(Owner owner) {

if (owner == null) {

throw new IllegalArgumentException("Owner não pode ser nulo");

}


save(owner);

System.out.println("Owner salvo: " + owner.getFirstName() + " " + owner.getLastName() + " (ID: " + owner.getId() + ")");

}



@Override

@Transactional(readOnly = true)

public PetType findPetTypeByName(String typeName) {

if (typeName == null || typeName.isEmpty()) {

return null;

}


// Criar um novo PetType com o nome fornecido

PetType petType = new PetType();

petType.setName(typeName);


// Aqui você poderia buscar o ID do tipo no banco de dados, se necessário

// Por enquanto, estamos apenas retornando um objeto com o nome


return petType;

}




}

