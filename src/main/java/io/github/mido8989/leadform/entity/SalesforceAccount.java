package io.github.mido8989.leadform.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A copy of an Account that Salesforce sent us. One row per Salesforce Account. */
@Entity
@Table(name = "salesforce_accounts")
public class SalesforceAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** The Account's Id in Salesforce. Unique, so the same Account is never stored twice. */
	@Column(nullable = false, unique = true, updatable = false, length = 18)
	private String salesforceId;

	@Column(nullable = false)
	private String name;

	@Column(length = 255)
	private String industry;

	/** When the Account was created in Salesforce. */
	private Instant salesforceCreatedAt;

	/** When we last received this Account from Salesforce. */
	@Column(nullable = false)
	private Instant receivedAt = Instant.now();

	/** Required by JPA. Not for use in your own code. */
	protected SalesforceAccount() {
	}

	public SalesforceAccount(String salesforceId) {
		this.salesforceId = salesforceId;
	}

	/** Applies the latest values sent by Salesforce. */
	public void update(String name, String industry, Instant salesforceCreatedAt) {
		this.name = name;
		this.industry = industry;
		this.salesforceCreatedAt = salesforceCreatedAt;
		this.receivedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getSalesforceId() {
		return salesforceId;
	}

	public String getName() {
		return name;
	}

	public String getIndustry() {
		return industry;
	}

	public Instant getSalesforceCreatedAt() {
		return salesforceCreatedAt;
	}

	public Instant getReceivedAt() {
		return receivedAt;
	}
}