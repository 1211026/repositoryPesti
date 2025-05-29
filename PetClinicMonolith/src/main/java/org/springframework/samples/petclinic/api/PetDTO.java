

package org.springframework.samples.petclinic.api;

import java.time.LocalDate;

public class PetDTO {
    private String name;
    private LocalDate birthDate;
    private Integer typeId;
    
    // Getters e setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}