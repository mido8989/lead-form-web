package io.github.mido8989.leadform.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One form submission. Each object of this class is one row in the "leads" table. */
@Entity
@Table(name = "leads")
public class Lead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 40)
	private String firstName;

	@Column(nullable = false, length = 80)
	private String lastName;

	@Column(nullable = false)
	private String company;

	@Column(nullable = false, length = 80)
	private String email;

	@Column(length = 40)
	private String phone;

	@Column(length = 2000)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SyncStatus syncStatus = SyncStatus.PENDING;

	/** The Id Salesforce gives the Lead once it has been created there. */
	@Column(length = 18)
	private String salesforceId;

	/** The Lead's status in Salesforce, filled in when Salesforce reports back. */
	@Column(length = 40)
	private String salesforceStatus;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	/** Required by JPA, which creates objects when it loads rows. Not for use in your own code. */
	protected Lead() {
	}

	public Lead(String firstName, String lastName, String company, String email, String phone, String message) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.company = company;
		this.email = email;
		this.phone = phone;
		this.message = message;
	}

	public void markSynced(String salesforceId) {
		this.salesforceId = salesforceId;
		this.syncStatus = SyncStatus.SYNCED;
	}

	public void markFailed() {
		this.syncStatus = SyncStatus.FAILED;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCompany() {
		return company;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getMessage() {
		return message;
	}

	public SyncStatus getSyncStatus() {
		return syncStatus;
	}

	public String getSalesforceId() {
		return salesforceId;
	}

	public String getSalesforceStatus() {
		return salesforceStatus;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}