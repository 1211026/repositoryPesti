
package org.springframework.samples.Vet.model;

import jakarta.persistence.*;
import lombok.Getter;


@Getter
public class Specialty {

	private Integer id;

	public Specialty(String name) {
		this.name = name;
	}

	public Specialty() {

	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isNew() {
		return this.id == null;
	}

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
