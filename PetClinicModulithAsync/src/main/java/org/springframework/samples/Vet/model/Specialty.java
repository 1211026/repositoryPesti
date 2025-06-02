
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

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	public Integer getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.id = Integer.valueOf(id);
	}
	
	public void setName(Integer name) {
		// TODO Auto-generated method stub
		this.name = String.valueOf(name);
	}
	
	public void setName(String name, String id) {
		// TODO Auto-generated method stub
		this.name = name;
		this.id = Integer.valueOf(id);
	}
	
	public String getSpecialty() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	

}
