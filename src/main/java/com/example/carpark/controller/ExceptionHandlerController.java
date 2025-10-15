package com.example.carpark.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.carpark.customexception.ForbiddenFieldModificationException;
import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.IncompatibleTypeOfVehicleException;
import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

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
		body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
		body.put(MESSAGE, "One or more fields in the request body failed validation.");
		body.put(PATH, request.getRequestURI());
		e.getBindingResult().getAllErrors().forEach(error -> {
			if (error instanceof FieldError fieldError) {
				String fieldName = fieldError.getField();
	            String errorMessage = error.getDefaultMessage();
	            fieldErrors.put(fieldName, errorMessage);
	        }
			else fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
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
		body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
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
		body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
		String topLevelMessage = "Failed to process request body. Please check your data.";
		String detailValue = "An unexpected error occurred during request body parsing.";
		String detailKey = "requestbody";
		Throwable mostSpecificCause = e.getMostSpecificCause();
		if(mostSpecificCause instanceof InvalidFormatException invalidFormatException) {
		    detailValue = invalidFormatException.getOriginalMessage();
		    boolean hasInvalidFormatPath = invalidFormatException.getPath() != null &&
		    	!invalidFormatException.getPath().isEmpty();
		    if(hasInvalidFormatPath) {
		    	String fieldName = invalidFormatException.getPath()
		    		.get(invalidFormatException.getPath().size() - 1).getFieldName();
			    if(fieldName != null) {
			    	detailKey = fieldName;
			    	topLevelMessage = String.format("Invalid data provided for field '%s'.", fieldName);
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
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleDataIntegrityViolationException(
	    DataIntegrityViolationException e,
	    HttpServletRequest request
	){
	    Map<String, Object> body = new LinkedHashMap<>();
	    body.put(TIME_STAMP, LocalDateTime.now());
	    body.put(STATUS, HttpStatus.CONFLICT.value());
	    body.put(ERROR, HttpStatus.CONFLICT.getReasonPhrase());
	    String userFriendlyMessage = "There's an integrity error with the received values";
	    String detailedMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
	    if (detailedMessage == null) detailedMessage = "";
	    if (detailedMessage.contains("Unique index or primary key violation") || detailedMessage.contains("duplicate key")) {
	        String duplicatedValue = null;
	        int lastQuoteIndex = detailedMessage.lastIndexOf("'");
	        if (lastQuoteIndex != -1) {
	            int secondLastQuoteIndex = detailedMessage.lastIndexOf("'", lastQuoteIndex - 1);
	            if (secondLastQuoteIndex != -1 && secondLastQuoteIndex < lastQuoteIndex) {
	                duplicatedValue = detailedMessage.substring(secondLastQuoteIndex + 1, lastQuoteIndex);
	            }
	        }
	        if (duplicatedValue != null && !duplicatedValue.isEmpty()) {
	            userFriendlyMessage = "Duplicated unique resource with value: "+duplicatedValue;
	        } else {
	            userFriendlyMessage = "Duplicated unique resource";
	        }
	    }
	    else if (detailedMessage.contains("NULL not allowed") || detailedMessage.contains("not null")) {
	        userFriendlyMessage = "A 'not null' field is null";
	    }
	    body.put(MESSAGE, userFriendlyMessage);
	    body.put(PATH, request.getRequestURI());
	    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
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
        body.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
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
        body.put(ERROR, HttpStatus.CONFLICT.getReasonPhrase());
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
        body.put(ERROR, HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
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
        body.put(ERROR, HttpStatus.CONFLICT.getReasonPhrase());
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
 	
 	@ExceptionHandler(MissingRequiredFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMissingRequiredFieldException(
    	MissingRequiredFieldException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
 	
 	@ExceptionHandler(IncompatibleTypeOfVehicleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleIncompatibleTypeOfVehicleException(
    	IncompatibleTypeOfVehicleException e,
    	HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
        body.put(MESSAGE, e.getMessage());
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }
 	
 	@ExceptionHandler(ForbiddenFieldModificationException.class)
 	@ResponseStatus(HttpStatus.BAD_REQUEST)
 	public ResponseEntity<Object> handleForbiddenFieldModificationException(
 	    ForbiddenFieldModificationException e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.BAD_REQUEST.value());
 	    body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
 	    body.put(MESSAGE, e.getMessage());
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
 	}
 	
 	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
 	@ResponseStatus(HttpStatus.BAD_REQUEST)
 	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
 	    MethodArgumentTypeMismatchException e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.BAD_REQUEST.value());
 	    body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
 	    Class<?> requiredType = e.getRequiredType();
 	    String requiredTypeName = (requiredType != null) ? requiredType.getSimpleName() : "Unknow type";
 	    String receivedValue = (e.getValue() != null) ? "'" + e.getValue() + "'" : "valor nulo";
 	    body.put(MESSAGE, "Invalid url paramether. "+e.getName()+" should be the type "+requiredTypeName+", but received "+receivedValue);
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
 	}
 	
 	@ExceptionHandler(IllegalStateException.class)
 	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 	public ResponseEntity<Object> handleIllegalStateException(
 	    IllegalStateException e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
 	    body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
 	    body.put(MESSAGE, "Intern server error: "+e.getMessage());
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
 	}
 	
 	@ExceptionHandler(NoResourceFoundException.class)
 	@ResponseStatus(HttpStatus.NOT_FOUND)
 	public ResponseEntity<Object> handleNoResourceFoundException(
 		NoResourceFoundException e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.NOT_FOUND);
 	    body.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
 	    body.put(MESSAGE, "Intern server error: "+e.getMessage());
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
 	}
 	
 	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
 	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
 	public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(
 	    HttpRequestMethodNotSupportedException e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.METHOD_NOT_ALLOWED.value()); 
 	    body.put(ERROR, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
 	    body.put(MESSAGE, "The HTTP method used is not supported for this endpoint. "+e.getMessage());
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
 	}
}
