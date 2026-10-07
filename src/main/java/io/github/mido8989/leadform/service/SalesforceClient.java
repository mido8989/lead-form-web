package io.github.mido8989.leadform.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import io.github.mido8989.leadform.config.SalesforceProperties;
import io.github.mido8989.leadform.entity.Lead;

/** Everything that talks to Salesforce over HTTP lives in this one class. */
@Component
public class SalesforceClient {

	private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
			new ParameterizedTypeReference<>() {
			};

	private final RestClient restClient;
	private final SalesforceProperties properties;

	public SalesforceClient(RestClient salesforceRestClient, SalesforceProperties properties) {
		this.restClient = salesforceRestClient;
		this.properties = properties;
	}

	public boolean isConfigured() {
		return properties.isConfigured();
	}

	/** Creates the lead in Salesforce and returns the Id Salesforce gave it. */
	public String createLead(Lead lead) {
		Map<String, Object> token = requestToken();
		String accessToken = (String) token.get("access_token");
		String instanceUrl = (String) token.get("instance_url");

		Map<String, Object> fields = new LinkedHashMap<>();
		putIfPresent(fields, "FirstName", lead.getFirstName());
		fields.put("LastName", lead.getLastName());
		fields.put("Company", lead.getCompany());
		fields.put("Email", lead.getEmail());
		putIfPresent(fields, "Phone", lead.getPhone());
		putIfPresent(fields, "Description", lead.getMessage());
		fields.put("LeadSource", "Web");

		Map<String, Object> result = restClient.post()
				.uri(instanceUrl + "/services/data/" + properties.apiVersion() + "/sobjects/Lead")
				.header("Authorization", "Bearer " + accessToken)
				// Save the lead even if Salesforce flags it as a possible duplicate.
				.header("Sforce-Duplicate-Rule-Header", "allowSave=true")
				.contentType(MediaType.APPLICATION_JSON)
				.body(fields)
				.retrieve()
				.body(JSON_OBJECT);

		if (result == null || result.get("id") == null) {
			throw new RestClientException("Salesforce did not return a Lead Id");
		}
		return (String) result.get("id");
	}

	/** Signs in with the app's key and secret (client credentials flow) and returns the token response. */
	private Map<String, Object> requestToken() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "client_credentials");
		form.add("client_id", properties.clientId());
		form.add("client_secret", properties.clientSecret());

		Map<String, Object> token = restClient.post()
				.uri(properties.domain() + "/services/oauth2/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(JSON_OBJECT);

		if (token == null || token.get("access_token") == null) {
			throw new RestClientException("Salesforce did not return an access token");
		}
		return token;
	}

	private static void putIfPresent(Map<String, Object> fields, String name, String value) {
		if (value != null) {
			fields.put(name, value);
		}
	}
}