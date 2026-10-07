package io.github.mido8989.leadform.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Sets up what the app needs to talk to Salesforce. */
@Configuration
@EnableConfigurationProperties(SalesforceProperties.class)


public class SalesforceConfig {

	/** The HTTP client used to call Salesforce. Declared here so Spring can hand it to any class that asks. */
	@Bean
	public RestClient salesforceRestClient() {
		return RestClient.create();
	}
}