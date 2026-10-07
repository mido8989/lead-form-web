package io.github.mido8989.leadform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** One Account as Salesforce sends it. */
public record AccountPayload(

		@NotBlank(message = "salesforceId is required.")
		@Size(max = 18, message = "salesforceId is too long.")
		String salesforceId,

		@NotBlank(message = "name is required.")
		@Size(max = 255, message = "name is too long.")
		String name,

		@Size(max = 255, message = "industry is too long.")
		String industry,

		/** When the Account was created, as text like 2026-10-07T02:17:24Z. */
		@Size(max = 40, message = "createdDate is too long.")
		String createdDate) {
}