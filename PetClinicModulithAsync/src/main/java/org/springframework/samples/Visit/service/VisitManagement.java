package org.springframework.samples.Visit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.notifications.AddVisitEvent;
import org.springframework.samples.Visit.VisitExternalAPI;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitManagement implements VisitExternalAPI {

	private final VisitRepository visitRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public void save(Visit visit) {
		eventPublisher
			.publishEvent(new AddVisitEvent(visit.getId(), visit.getDate(), visit.getDescription(), visit.getPet_id()));
		visitRepository.save(visit);
	}

	@Override
	public List<Visit> findAll() {
		return visitRepository.findAll();
	}

}
