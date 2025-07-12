package org.springframework.samples.Owner.model;



import jakarta.persistence.*; // Importe tudo de jakarta.persistence

import jakarta.validation.constraints.Digits;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;



import java.io.Serializable;

import java.util.ArrayList;

import java.util.Collections;

import java.util.HashSet; // Mudar para HashSet para coleções @OneToMany

import java.util.List;

import java.util.Set; // Usar Set<OwnerPet> para a coleção



// IMPORTANTE: Adicionar estas anotações

@Entity // Indica que esta classe é uma entidade JPA

@Table(name = "owners") // Mapeia para a tabela 'owners' no DB

public class Owner implements Serializable {



@Id // Marca 'id' como chave primária

@GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia para geração automática de ID

private Integer id;



@NotBlank

@Column(name = "first_name") // Mapeia para a coluna first_name

private String firstName;



@NotBlank

@Column(name = "last_name") // Mapeia para a coluna last_name

private String lastName;



@NotBlank

@Column(name = "address")

private String address;



@NotBlank

@Column(name = "city")

private String city;



@NotBlank

@Digits(fraction = 0, integer = 10)

@Column(name = "telephone")

private String telephone;



@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)

@JoinColumn(name = "owner_id") // owner_id na tabela owner_pets

private Set<OwnerPet> pets = new HashSet<>(); // Corrigido para HashSet



public Owner() {

// Construtor padrão necessário para JPA

}


// Construtor

public Owner(Integer id, String firstName, String lastName, String address, String city, String telephone, Set<OwnerPet> pets) { // Parâmetro agora é Set

this.id = id;

this.firstName = firstName;

this.lastName = lastName;

this.address = address;

this.city = city;

this.telephone = telephone;

this.pets = pets;

}

// Métodos get/set para os atributos

public Integer getId() {

return id;

}



public Integer setId(Integer id) {

return this.id = id;

}


public String getFirstName() {

return firstName;


}


public void setFirstName(String firstName) {

this.firstName = firstName;

}


public String getLastName() {


return lastName;

}


public void setLastName(String lastName) {


this.lastName = lastName;

}



public String getAddress() {


return address;

}


public void setAddress(String address) {


this.address = address;

}


public String getCity() {


return city;

}


public void setCity(String city) {


this.city = city;

}


public String getTelephone() {


return telephone;

}


public void setTelephone(String telephone) {


this.telephone = telephone;

}






// Mantenha os seus métodos get/set e addPet

public Set<OwnerPet> getPets() { // Mude o retorno para Set

if (pets == null) {

pets = new HashSet<>();

}

return pets;

}


public void setPets(Set<OwnerPet> pets) { // Parâmetro é Set

this.pets = pets;

}



public void addPet(OwnerPet pet) {

if (pet == null) {

throw new IllegalArgumentException("Pet não pode ser nulo");

}

if (this.pets == null) {

this.pets = new HashSet<>();

}

boolean exists = this.pets.stream().anyMatch(existing ->

(existing.getId() != null && existing.getId().equals(pet.getId())) ||

(existing.getName() != null && existing.getName().equalsIgnoreCase(pet.getName()))

);

if (!exists) {

pet.setOwner_id(this.getId()); // Garanta que o owner_id do pet é setado

this.pets.add(pet);

System.out.println("Pet adicionado: " + pet.getName());

} else {

System.out.println("Pet já existe: " + pet.getName());

}

}





// O outro método addPet também deverá ser ajustado para Set

public void addPet(OwnerPet pet, Set<OwnerPet.Visit> visits) {

if (pet == null) {

throw new IllegalArgumentException("Pet não pode ser nulo");

}

if (visits != null) {

for (OwnerPet.Visit visit : visits) {

pet.addVisit(visit);

}

}

this.addPet(pet); // Chama o método acima

}



}