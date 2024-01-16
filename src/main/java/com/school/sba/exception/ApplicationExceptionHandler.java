package com.school.sba.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		List<ObjectError> allErrors = ex.getAllErrors(); // To get the list of errors
		
		Map<String, String> errors = new HashMap<>();    //  To display in the form of Key Value Pair
		
		allErrors.forEach(error ->{						 // To iterate each error 
			FieldError fieldError =(FieldError) error;   // Field Error is nothing but an error occurred to the Field.  
			errors.put(fieldError.getField(),fieldError.getDefaultMessage());
		});
		return structure(HttpStatus.BAD_REQUEST, "Failed to save the Data", errors);
	}
	
	private ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause){
		return new ResponseEntity<Object>(
				Map.of(
					   "rootCause",rootCause
					   ,"status",status.value()
					   ,"message",message
					   ),
				status
				);
		// ResponseEntity<Object>(ResponseStructure OBJECT in the form of MAP, HttpStatus);
	}

	@ExceptionHandler(UnauthorizedRoleException.class)
	private ResponseEntity<Object> handleUnauthorizedUserRole(UnauthorizedRoleException exp){
		return structure(HttpStatus.NOT_ACCEPTABLE,exp.getMessage(),"Unauthorized User Role. ADMIN already Exists");
	}
	
	@ExceptionHandler(UserNotFoundByIdException.class)
	private ResponseEntity<Object> handleUserNotFoundById(UserNotFoundByIdException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"User ID not exists");
	}
}