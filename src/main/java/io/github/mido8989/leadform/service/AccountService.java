package io.github.mido8989.leadform.service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.mido8989.leadform.dto.AccountPayload;
import io.github.mido8989.leadform.entity.SalesforceAccount;
import io.github.mido8989.leadform.repository.SalesforceAccountRepository;

/** The business logic for accounts received from Salesforce. */
@Service
public class AccountService {

	private static final Logger log = LoggerFactory.getLogger(AccountService.class);

	private final SalesforceAccountRepository accountRepository;

	public AccountService(SalesforceAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * Stores the accounts Salesforce sent. An account we already have is updated, not duplicated,
	 * so it is safe for Salesforce to send the same account more than once.
	 */
	public int saveAll(List<AccountPayload> payloads) {
		for (AccountPayload payload : payloads) {
			SalesforceAccount account = accountRepository.findBySalesforceId(payload.salesforceId())
					.orElseGet(() -> new SalesforceAccount(payload.salesforceId()));
			account.update(payload.name().trim(), trimToNull(payload.industry()), parseInstant(payload.createdDate()));
			accountRepository.save(account);
		}
		log.info("Received {} account(s) from Salesforce", payloads.size());
		return payloads.size();
	}

	/** The accounts to show on the page, newest first. */
	public List<SalesforceAccount> latest() {
		return accountRepository.findTop20ByOrderByReceivedAtDesc();
	}

	private static Instant parseInstant(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Instant.parse(value.trim());
		} catch (DateTimeParseException exception) {
			log.warn("Could not read createdDate '{}'; storing the account without it", value);
			return null;
		}
	}

	private static String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}