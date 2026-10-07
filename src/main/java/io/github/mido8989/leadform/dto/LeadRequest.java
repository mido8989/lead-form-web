package io.github.mido8989.leadform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The data the form sends. The annotations are the rules each field must pass. */
public record LeadRequest(

		@Size(max = 40, message = "First name is too long.")
		String firstName,

		@NotBlank(message = "Last name is required.")
		@Size(max = 80, message = "Last name is too long.")
		String lastName,

		@NotBlank(message = "Company is required.")
		@Size(max = 255, message = "Company is too long.")
		String company,

		@NotBlank(message = "Email is required.")
		@Email(message = "Email is not valid.")
		@Size(max = 80, message = "Email is too long.")
		String email,

		@Size(max = 40, message = "Phone is too long.")
		String phone,

		@Size(max = 2000, message = "Message is too long.")
		String message,

		/** Hidden trap field. People never fill it in; simple bots do. */
		String website) {
}