package org.springframework.samples.Visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.samples.Owner.OwnerPublicAPI;
import org.springframework.samples.Pet.PetPublicAPI;
import org.springframework.samples.Visit.VisitExternalAPI;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.samples.Visit.service.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService implements VisitExternalAPI {

    private final VisitRepository visitRepository;
    private final PetPublicAPI petPublicAPI;
    private final OwnerPublicAPI ownerPublicAPI;

    @Override
    public void save(Visit visit) {
        petPublicAPI.saveVisit(visit.getId(),visit.getDate(), visit.getDescription(), visit.getPet_id());
        ownerPublicAPI.saveVisit(visit.getId(),visit.getDate(), visit.getDescription(), visit.getPet_id());
        visitRepository.save(visit);
    }

    @Override
    public List<Visit> findAll() {
        return visitRepository.findAll();
    }


}