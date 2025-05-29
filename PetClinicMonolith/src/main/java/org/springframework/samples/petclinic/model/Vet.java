package org.springframework.samples.petclinic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlElement;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

import java.util.*;

@Entity
@Table(name = "vets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Evita problemas de proxy
public class Vet extends Person {

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties",
			joinColumns = @JoinColumn(name = "vet_id"),
			inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties = new HashSet<>();

	@XmlElement
	public List<Specialty> getSpecialties() {
		List<Specialty> sortedSpecs = new ArrayList<>(specialties);
		PropertyComparator.sort(sortedSpecs, new MutableSortDefinition("name", true, true));
		return Collections.unmodifiableList(sortedSpecs);
	}

	public Set<Specialty> getSpecialtiesInternal() {
		return this.specialties;
	}

	public void setSpecialtiesInternal(Set<Specialty> specialties) {
		this.specialties = specialties;
	}

	public void setSpecialties(Set<Specialty> specialties) {
		this.specialties = specialties;
	}

	public int getNrOfSpecialties() {
		return specialties.size();
	}

	public void addSpecialty(Specialty specialty) {
		this.specialties.add(specialty);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vet vet = (Vet) o;
		return Objects.equals(getId(), vet.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
