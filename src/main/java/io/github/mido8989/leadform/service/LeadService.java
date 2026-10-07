package io.github.mido8989.leadform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import io.github.mido8989.leadform.dto.LeadRequest;
import io.github.mido8989.leadform.entity.Lead;
import io.github.mido8989.leadform.repository.LeadRepository;

/** The business logic for leads: what happens when a form is submitted. */
@Service
public class LeadService {

	private static final Logger log = LoggerFactory.getLogger(LeadService.class);

	private final LeadRepository leadRepository;
	private final SalesforceClient salesforceClient;

	public LeadService(LeadRepository leadRepository, SalesforceClient salesforceClient) {
		this.leadRepository = leadRepository;
		this.salesforceClient = salesforceClient;
	}

	/** Saves a submitted form as a new lead, then sends it to Salesforce. */
	public Lead submit(LeadRequest request) {
		Lead lead = new Lead(
				trimToNull(request.firstName()),
				request.lastName().trim(),
				request.company().trim(),
				request.email().trim(),
				trimToNull(request.phone()),
				trimToNull(request.message()));

		// Save first. Whatever happens with Salesforce next, the lead is safely in our database.
		lead = leadRepository.save(lead);

		if (!salesforceClient.isConfigured()) {
			log.warn("Salesforce is not configured; lead {} stays PENDING", lead.getId());
			return lead;
		}

		try {
			String salesforceId = salesforceClient.createLead(lead);
			lead.markSynced(salesforceId);
			log.info("Lead {} created in Salesforce as {}", lead.getId(), salesforceId);
		} catch (RestClientException exception) {
			lead.markFailed();
			log.error("Lead {} could not be sent to Salesforce: {}", lead.getId(), exception.getMessage());
		}

		// Save again to record the outcome (SYNCED with the Salesforce Id, or FAILED).
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