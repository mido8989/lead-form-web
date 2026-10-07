package io.github.mido8989.leadform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** The Salesforce settings, read from the lines starting with "salesforce." in application.properties. */
@ConfigurationProperties(prefix = "salesforce")
public record SalesforceProperties(
		String domain,
		String clientId,
		String clientSecret,
		String apiVersion) {

	/** True only when the address, key and secret have all been provided. */
	public boolean isConfigured() {
		return hasText(domain) && hasText(clientId) && hasText(clientSecret);
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}