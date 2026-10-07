package io.github.mido8989.leadform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/** Decides which addresses are public and which need a valid OAuth 2.0 access token. */
@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Who may call what.
				.authorizeHttpRequests(requests -> requests
						// Salesforce sending us data: only with a valid access token.
						.requestMatchers(HttpMethod.POST, "/api/salesforce/**").authenticated()
						// Everything else (the page, the lead form, the accounts list) stays public.
						.anyRequest().permitAll())
				// Accept access tokens in the JWT format. Spring checks the signature, the expiry,
				// the issuer and the audience using the settings in application.properties.
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				// Every request stands on its own; the server keeps no login sessions.
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// CSRF protection guards cookie-based logins. This app has none, so it is switched off.
				.csrf(csrf -> csrf.disable())
				// Lets the local H2 console display inside its own frames.
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}
}