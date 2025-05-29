package org.springframework.samples.Visit;

import org.springframework.samples.Visit.model.Visit;

import java.util.List;
import java.util.Set;

public interface VisitExternalAPI {

	void save(Visit visit);

	List<Visit> findAll();

}
