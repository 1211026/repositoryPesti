package org.springframework.samples.Visit.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VisitRepository {

    void save(Visit visit);

    Set<Visit> findVisitByPetId(@Param("id") Integer id);

    List<Visit> findAll();


    Visit findById(Integer id);
}
