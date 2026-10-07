package io.github.mido8989.leadform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.mido8989.leadform.entity.SalesforceAccount;

/** Database access for the accounts received from Salesforce. */
public interface SalesforceAccountRepository extends JpaRepository<SalesforceAccount, Long> {

	/** Finds the stored copy of a Salesforce Account, if we already have it. */
	Optional<SalesforceAccount> findBySalesforceId(String salesforceId);

	/** The 20 most recently received accounts, newest first. */
	List<SalesforceAccount> findTop20ByOrderByReceivedAtDesc();
}