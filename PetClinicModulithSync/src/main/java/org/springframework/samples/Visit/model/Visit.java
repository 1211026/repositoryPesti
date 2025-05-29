package org.springframework.samples.Visit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Getter
public class Visit {


    private Integer id;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String description;


    private Integer pet_id;


    public Visit() {
        this.date = LocalDate.now();
    }

    public Visit(Integer id, Integer pet_id , LocalDate date,String description ) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.pet_id = pet_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPet_id(Integer pet_id) {
        this.pet_id = pet_id;
    }

    public boolean isNew() {
        return this.id == null;
    }



}

