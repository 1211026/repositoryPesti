package org.springframework.samples.Pet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.jmolecules.ddd.types.ValueObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
@Getter
public class Pet {

	private Integer id;

	@NotBlank
	private String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	private PetType type;

	private Integer owner_id;

	private Set<Pet.Visit> visits = new LinkedHashSet<>();

	public Pet(Integer id, String name, LocalDate birthDate, PetType type, Integer owner_id) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.type = type;
		this.owner_id = owner_id;
	}

	public Pet() {
	}

	public record Visit (Integer id, String description, LocalDate visit_date, Integer pet_id) implements ValueObject {}


	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public void setOwner_id(Integer owner_id) {
		this.owner_id = owner_id;
	}

	public void setVisits(Set<Pet.Visit> visits) {
		this.visits = visits;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
