package org.springframework.samples.Owner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.jmolecules.ddd.types.ValueObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class OwnerPet {

	private Integer id;

	@NotNull
	private String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private LocalDate birthDate;

	@NotNull
	private Integer owner_id;

	private Set<Visit> visits;

	@NotNull
	private String type_name;

	public OwnerPet(Integer id, String name, LocalDate birthDate, Integer owner_id, String type) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.owner_id = owner_id;
		this.type_name = type;
	}

	public OwnerPet() {

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

	public void setOwner_id(Integer owner_id) {
		this.owner_id = owner_id;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public void setVisits(Set<Visit> visits) {
		this.visits = visits;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}