package io.github.mido8989.leadform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	/**
	 * Called by Salesforce. SecurityConfig only lets a request reach this method
	 * if it carries a valid access token, so no check is needed here.
	 */
	@PostMapping("/api/salesforce/accounts")
	public Map<String, Object> receive(@Valid @RequestBody AccountSyncRequest request) {
		int saved = accountService.saveAll(request.accounts());
		return Map.of("saved", saved);
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
}