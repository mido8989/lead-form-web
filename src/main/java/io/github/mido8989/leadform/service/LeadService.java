package io.github.mido8989.leadform.service;

import org.springframework.stereotype.Service;

import io.github.mido8989.leadform.dto.LeadRequest;
import io.github.mido8989.leadform.entity.Lead;
import io.github.mido8989.leadform.repository.LeadRepository;

/** The business logic for leads: what happens when a form is submitted. */
@Service
public class LeadService {

	private final LeadRepository leadRepository;

	public LeadService(LeadRepository leadRepository) {
		this.leadRepository = leadRepository;
	}

	/** Saves a submitted form as a new lead and returns the saved row. */
	public Lead submit(LeadRequest request) {
		Lead lead = new Lead(
				trimToNull(request.firstName()),
				request.lastName().trim(),
				request.company().trim(),
				request.email().trim(),
				trimToNull(request.phone()),
				trimToNull(request.message()));
		return leadRepository.save(lead);
	}

	/** Turns empty or blank text into null, so optional fields are stored as "no value". */
	private static String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}