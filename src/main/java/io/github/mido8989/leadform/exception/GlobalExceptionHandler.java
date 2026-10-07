package io.github.mido8989.leadform.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Turns errors into clear JSON answers for the form, for every controller in the app. */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Runs when the submitted data breaks a rule in LeadRequest. Answers 400 with the reasons. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" "));
		return ResponseEntity.badRequest().body(Map.of("error", message));
	}
}