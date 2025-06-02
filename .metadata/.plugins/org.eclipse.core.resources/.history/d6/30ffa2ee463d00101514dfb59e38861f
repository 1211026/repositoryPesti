
package org.springframework.samples.Pet.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;



@Getter
public class PetType implements Serializable {

	private Integer id;

	private String name;

    public PetType(String name) {
    }

	public PetType() {
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
