package org.springframework.samples.Pet.model;

import lombok.Getter;
import lombok.Setter; // Make sure this is imported

import java.io.Serializable;

public class PetType implements Serializable {

    private Integer id;
    private String name;

    public PetType() {
    }

    public PetType(String name) {
        this.name = name;
    }

    // Este construtor é útil para RowMapper
    public PetType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        // Agora, com @Getter, this.getName() deve funcionar sem problemas
        return this.getName();
    }
    
    public Integer getId() {
		return id;
	}
    
    public void setId(Integer id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    

    
    
}