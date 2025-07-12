package org.springframework.samples.Owner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.samples.Pet.model.Pet;

@Getter
public class Owner implements Serializable {
	private Integer id;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String address;

	@NotBlank
	private String city;

	@NotBlank
	@Digits(fraction = 0, integer = 10)
	private String telephone;

	private List<OwnerPet> pets = new ArrayList<>();

	public Owner(Integer id, String firstName, String lastName, String address, String city, String telephone,
			List<OwnerPet> pets) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
		this.pets = pets;
	}

	public Owner() {

	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPets(List<OwnerPet> pets) {
		this.pets = pets;
	}

	public String getLastName() {
		return this.lastName;
	}

	public Integer getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	
	public List<OwnerPet> getPets() {
		return this.pets;
	}

	
	
	
	
	

}
