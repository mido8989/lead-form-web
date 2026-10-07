package io.github.mido8989.leadform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings for calls coming in from Salesforce, read from the lines starting with "webhook.". */
@ConfigurationProperties(prefix = "webhook")
public record WebhookProperties(String secret) {
}