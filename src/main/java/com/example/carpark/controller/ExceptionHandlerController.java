package com.example.carpark.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException e,
			HttpServletRequest request
	){
		Map<String, Object> body = new LinkedHashMap<>();
		Map<String, String> fieldErrors = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Validation Failed.");
		body.put("message", "One or more fields in the request body failed validation.");
		body.put("path", request.getRequestURI());
		e.getBindingResult().getAllErrors().forEach(error -> {
			if (error instanceof FieldError fieldError) {
				 String fieldName = fieldError.getField();
	            String errorMessage = error.getDefaultMessage();
	            fieldErrors.put(fieldName, errorMessage);
	        }
			else {
				fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
			}
		});
		body.put("details", fieldErrors);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}
