package com.example.carpark.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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

import com.example.carpark.customexception.ClosedRentalServiceException;
import com.example.carpark.customexception.ForbiddenActionException;
import com.example.carpark.customexception.ForbiddenFieldModificationException;
import com.example.carpark.customexception.IncompatibleParkingSpaceException;
import com.example.carpark.customexception.IncompatibleTypeOfVehicleException;
import com.example.carpark.customexception.MissingRequiredFieldException;
import com.example.carpark.customexception.OccupiedParkingSpaceException;
import com.example.carpark.customexception.ParkingSpaceAlreadyExistsException;
import com.example.carpark.customexception.ResourceAlreadyExistsException;
import com.example.carpark.customexception.ResourceNotFoundException;
import com.example.carpark.customexception.VehicleOwnershipAlreadyInUseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionHandlerController {
	
	private static final String TIME_STAMP = "timestamp";
	private static final String STATUS = "satatus";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";
	private static final String PATH = "path";
	private static final String DETAILS = "details";
	
	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleInvalidDataAccessApiUsageException(
	    InvalidDataAccessApiUsageException e,
	    HttpServletRequest request
	){
	    Map<String, Object> body = new LinkedHashMap<>();
	    body.put(TIME_STAMP, LocalDateTime.now());
	    body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
	    body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	    String message = "Erro interno: Uma operação de banco de dados que requer transação foi executada fora de um contexto transacional.";
	    if (e.getMessage() != null && !e.getMessage().isEmpty()) message+=" Detalhes: "+e.getMessage();
	    body.put(MESSAGE, message);
	    body.put(PATH, request.getRequestURI());
	    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleNullPointerException(
        NullPointerException e,
        HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        String errorMessage = "Ocorreu um erro interno inesperado: um valor nulo foi encontrado onde não deveria";
        if (e.getMessage() != null)  errorMessage += " Detalhes: "+e.getMessage();
        body.put(MESSAGE, errorMessage);
        body.put(PATH, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, String> details = new HashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, "Um ou mais campos no corpo da requisição falharam na validação.");
        body.put(PATH, request.getRequestURI());
        e.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String errorMessage = error.getDefaultMessage();
                details.put(fieldName, errorMessage);
            } else {
                details.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        body.put(DETAILS, details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
 	
	@ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, String> details = new HashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, "Falha na validação para parâmetros de método ou de URL.");
        body.put(PATH, request.getRequestURI());
        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            int lastDotIndex = propertyPath.lastIndexOf('.');
            String fieldName = lastDotIndex != -1 ?
            	propertyPath.substring(lastDotIndex + 1) : propertyPath;
            details.put(fieldName, violation.getMessage());
        });
        body.put(DETAILS, details);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
 	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ){
        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, String> details = new HashMap<>();
        body.put(TIME_STAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(PATH, request.getRequestURI());
        String message = "Falha ao processar o corpo da requisição. Por favor, verifique seus dados.";
        String detailValue = "Ocorreu um erro inesperado durante a análise do corpo da requisição.";
        String detailKey = "requestBody";
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
                    message = String.format("Dado inválido fornecido para o campo '%s'.", fieldName);
                } else {
                    message = "Erro de análise JSON: Estrutura malformada.";
                }
            } else {
                message = "Erro de análise JSON: Estrutura malformada.";
            }
        } else {
            detailValue = e.getMessage();
            message = "Corpo da requisição ausente ou ilegível.";
        }
        body.put(MESSAGE, message);
        details.put(detailKey, detailValue);
        body.put(DETAILS, details);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleDataIntegrityViolationException(
	        DataIntegrityViolationException e,
	        HttpServletRequest request
	){
	    Map<String, Object> body = new LinkedHashMap<>();
	    Map<String, Object> details = new LinkedHashMap<>();
	    body.put(TIME_STAMP, LocalDateTime.now());
	    body.put(STATUS, HttpStatus.CONFLICT.value());
	    body.put(ERROR, HttpStatus.CONFLICT.getReasonPhrase());
	    body.put(PATH, request.getRequestURI());
	    String message = "Há um erro de integridade com os valores recebidos.";
	    String detailedMessage = e.getRootCause() != null ? 
	    	e.getRootCause().getMessage() : e.getMessage();
	    if (detailedMessage == null) detailedMessage = "Nenhuma mensagem detalhada disponível.";
	    boolean containsMsgsIndexOrDuplicated = detailedMessage.contains("duplicate key") ||
	    	detailedMessage.contains("Unique index or primary key violation");
	    if (containsMsgsIndexOrDuplicated) {
	        String duplicatedValue = null;
	        int lastQuoteIndex = detailedMessage.lastIndexOf("'");
	        if (lastQuoteIndex != -1) {
	            int secondLastQuoteIndex = detailedMessage.lastIndexOf("'", lastQuoteIndex - 1);
	            if (secondLastQuoteIndex != -1 && secondLastQuoteIndex < lastQuoteIndex) {
	                duplicatedValue = detailedMessage
	                	.substring(secondLastQuoteIndex + 1, lastQuoteIndex);
	            }
	        }
	        if (duplicatedValue != null && !duplicatedValue.isEmpty()) {
	            message = "Recurso único duplicado com valor: "+duplicatedValue;
	        } else {
	            message = "Recurso único duplicado.";
	        }
	    }
	    else if (detailedMessage.contains("NULL not allowed") || detailedMessage.contains("not null")) {
	        message = "Um campo obrigatório (não nulo) está ausente ou vazio.";
	    }
	    body.put(MESSAGE, message);
	    details.put("originalDetail", detailedMessage);
	    body.put(DETAILS, details);
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
 	
 	@ExceptionHandler(Error.class)
 	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 	public ResponseEntity<Object> handleError(
 		Error e,
 	    HttpServletRequest request
 	){
 	    Map<String, Object> body = new LinkedHashMap<>();
 	    body.put(TIME_STAMP, LocalDateTime.now());
 	    body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value()); 
 	    body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
 	    body.put(MESSAGE, "An unexpected internal server error occurred: "+e.getMessage());
 	    body.put(PATH, request.getRequestURI());
 	    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
 	}
 	
 	@ExceptionHandler(ClosedRentalServiceException.class)
 	@ResponseStatus(HttpStatus.CONFLICT)
 	public ResponseEntity<Object> handleClosedRentalServiceException(
 		ClosedRentalServiceException e,
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
 	
 	@ExceptionHandler(VehicleOwnershipAlreadyInUseException.class)
 	@ResponseStatus(HttpStatus.CONFLICT)
 	public ResponseEntity<Object> handleVehicleOwnershipAlreadyInUseException(
 		VehicleOwnershipAlreadyInUseException e,
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
 	
 	@ExceptionHandler(ParkingSpaceAlreadyExistsException.class)
 	@ResponseStatus(HttpStatus.CONFLICT)
 	public ResponseEntity<Object> handleParkingSpaceAlreadyExistsException(
 		ParkingSpaceAlreadyExistsException e,
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
 	
 	 @ExceptionHandler(ForbiddenActionException.class)
     @ResponseStatus(HttpStatus.FORBIDDEN)
     public ResponseEntity<Object> handleForbiddenActionException(
         ForbiddenActionException e,
         HttpServletRequest request
     ){
         Map<String, Object> body = new LinkedHashMap<>();
         body.put(TIME_STAMP, LocalDateTime.now());
         body.put(STATUS, HttpStatus.FORBIDDEN.value());
         body.put(ERROR, HttpStatus.FORBIDDEN.getReasonPhrase());
         body.put(MESSAGE, e.getMessage());
         body.put(PATH, request.getRequestURI());
         return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
     }
}
