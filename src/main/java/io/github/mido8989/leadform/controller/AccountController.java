package io.github.mido8989.leadform.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.github.mido8989.leadform.config.WebhookProperties;
import io.github.mido8989.leadform.dto.AccountSyncRequest;
import io.github.mido8989.leadform.entity.SalesforceAccount;
import io.github.mido8989.leadform.service.AccountService;
import jakarta.validation.Valid;

/** Two addresses: one Salesforce calls to send accounts, one the page calls to show them. */
@RestController
public class AccountController {

	/** What the page receives for each account. Only fields that are safe to show publicly. */
	public record AccountView(String name, String industry, String createdAt) {
	}

	private final AccountService accountService;
	private final WebhookProperties webhookProperties;

	public AccountController(AccountService accountService, WebhookProperties webhookProperties) {
		this.accountService = accountService;
		this.webhookProperties = webhookProperties;
	}

	/** Called by Salesforce. Rejected unless the request carries our shared secret. */
	@PostMapping("/api/salesforce/accounts")
	public ResponseEntity<Map<String, Object>> receive(
			@RequestHeader(value = "X-Webhook-Secret", required = false) String providedSecret,
			@Valid @RequestBody AccountSyncRequest request) {

		if (!secretMatches(providedSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authorized."));
		}

		int saved = accountService.saveAll(request.accounts());
		return ResponseEntity.ok(Map.of("saved", saved));
	}

	/** Called by the page. Read-only: there is no way to change an account through this address. */
	@GetMapping("/api/accounts")
	public List<AccountView> list() {
		return accountService.latest().stream()
				.map(AccountController::toView)
				.toList();
	}

	private static AccountView toView(SalesforceAccount account) {
		String createdAt = account.getSalesforceCreatedAt() == null ? null : account.getSalesforceCreatedAt().toString();
		return new AccountView(account.getName(), account.getIndustry(), createdAt);
	}

	/** True only if a secret is configured and the caller sent exactly that value. */
	private boolean secretMatches(String providedSecret) {
		String expected = webhookProperties.secret();
		if (expected == null || expected.isBlank() || providedSecret == null) {
			return false;
		}
		// Compares in constant time, so the response time reveals nothing about the secret.
		return MessageDigest.isEqual(
				expected.getBytes(StandardCharsets.UTF_8),
				providedSecret.getBytes(StandardCharsets.UTF_8));
	}
}