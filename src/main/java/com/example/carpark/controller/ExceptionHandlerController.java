package com.example.carpark.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionHandlerController {
	
	private static final String TIME_STAMP = "timestamp";
	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";
	private static final String PATH = "path";
	private static final String DETAILS = "details";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e,
		HttpServletRequest request
	){
		Map<String, Object> body = new LinkedHashMap<>();
		Map<String, String> fieldErrors = new HashMap<>();
		body.put(TIME_STAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.BAD_REQUEST.value());
		body.put(ERROR, "Validation Failed.");
		body.put(MESSAGE, "One or more fields in the request body failed validation.");
		body.put(PATH, request.getRequestURI());
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
		body.put(DETAILS, fieldErrors);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
 	
 	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleConstraintViolationException(
		ConstraintViolationException e,
		HttpServletRequest request
	){
		Map<String, Object> body = new LinkedHashMap<>();
		Map<String, String> fieldErrors = new HashMap<>();
		body.put(TIME_STAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.BAD_REQUEST.value());
		body.put(ERROR, "Bad Request");
		body.put(MESSAGE, "Validation failed for method parameters");
		body.put(PATH, request.getRequestURI());
		 e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            int lastDotIndex = propertyPath.lastIndexOf('.');
            String fieldName = lastDotIndex != -1 ? 
            	propertyPath.substring(lastDotIndex + 1) : propertyPath;
            fieldErrors.put(fieldName, violation.getMessage());
        });
		body.put(DETAILS, fieldErrors);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
 	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(
	    HttpMessageNotReadableException e,
	    HttpServletRequest request
	){
		Map<String, Object> body = new LinkedHashMap<>();
		Map<String, String> fieldErrors = new HashMap<>();
		body.put(TIME_STAMP, LocalDateTime.now());
		body.put(STATUS, HttpStatus.BAD_REQUEST.value());
		body.put(ERROR, "Bad Request");
		String topLevelMessage = "Failed to process request body. Please check your data.";
		String detailValue = "An unexpected error occurred during request body parsing.";
		String detailKey = "requestbody";
		Throwable mostSpecificCause = e.getMostSpecificCause();
		if(mostSpecificCause instanceof InvalidFormatException invalidFormatException) {
		    detailValue = invalidFormatException.getOriginalMessage();
		    boolean hasInvalidFormatPath = invalidFormatException.getPath() != null && 
		    	!invalidFormatException.getPath().isEmpty();
		    if(hasInvalidFormatPath) {
		    	String fieldName = invalidFormatException.getPath().get(invalidFormatException
		    		.getPath().size() - 1).getFieldName();
			    if(fieldName != null) {
			    	detailKey = fieldName;
			    	topLevelMessage = String
			    		.format("Invalid data provided for field '%s'.", fieldName);
			    } 
			    if(fieldName == null) topLevelMessage = "JSON parsing error: Malformed structure.";
		    } 
		    if(!hasInvalidFormatPath) topLevelMessage = "JSON parsing error: Malformed structure.";
		 }
		 else {
		     detailValue = e.getMessage();
		     topLevelMessage = "Missing or unreadable request body.";
		 }
		 fieldErrors.put(detailKey, detailValue);
		 body.put(MESSAGE, topLevelMessage);
		 body.put(PATH, request.getRequestURI());
		 body.put(DETAILS, fieldErrors);
		 return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(
    	ResourceNotFoundException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(ERROR, "Not Found");
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
 	
 	@ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(
    	ResourceAlreadyExistsException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.CONFLICT.value());
        body.put(ERROR, "Resource Conflict");
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
 	
 	@ExceptionHandler(IncompatibleParkingSpaceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleIncompatibleParkingSpaceException(
    	IncompatibleParkingSpaceException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, "Unprocessable Entity");
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }
 	
 	@ExceptionHandler(OccupiedParkingSpaceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleOccupiedParkingSpaceException(
    	OccupiedParkingSpaceException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.CONFLICT.value());
        body.put(ERROR, "Conflict");
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
