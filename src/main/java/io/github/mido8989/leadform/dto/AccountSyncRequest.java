package io.github.mido8989.leadform.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/** What Salesforce sends in one call: a list of accounts, so many can arrive together. */
public record AccountSyncRequest(

		@NotEmpty(message = "accounts must not be empty.")
		@Size(max = 200, message = "Too many accounts in one request.")
		@Valid
		List<AccountPayload> accounts) {
}