package io.github.mido8989.leadform.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.mido8989.leadform.dto.LeadRequest;
import io.github.mido8989.leadform.entity.Lead;
import io.github.mido8989.leadform.service.LeadService;
import jakarta.validation.Valid;

/** The web address the form talks to: POST /api/leads. */
@RestController
@RequestMapping("/api/leads")
public class LeadController {

	private final LeadService leadService;

	public LeadController(LeadService leadService) {
		this.leadService = leadService;
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody LeadRequest request) {
		// A filled-in trap field means a bot. Answer as if it worked, but save nothing.
		if (request.website() != null && !request.website().isBlank()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "received"));
		}

		Lead lead = leadService.submit(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", lead.getId(), "status", "received"));
	}
}