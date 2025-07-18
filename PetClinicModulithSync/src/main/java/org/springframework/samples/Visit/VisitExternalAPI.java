package org.springframework.samples.Visit;

import org.springframework.samples.Visit.model.Visit;

import java.util.Collection;
import java.util.List;

public interface VisitExternalAPI {

    void save(Visit visit);
    List<Visit> findAll();
	List<Visit> findByPetId(int petId);
	Visit findById(int visitId);
}
