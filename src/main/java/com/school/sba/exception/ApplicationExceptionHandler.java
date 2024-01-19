package com.school.sba.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
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

	@ExceptionHandler(DataIntegrityViolationException.class)
	private ResponseEntity<Object> handleDataDuplicationException(DataIntegrityViolationException exp){
		return structure(HttpStatus.NOT_ACCEPTABLE,"Unable to save the data","Key or Email duplication is Prohibited");
	}
	
	@ExceptionHandler(UnauthorizedRoleException.class)
	private ResponseEntity<Object> handleUnauthorizedUserRole(UnauthorizedRoleException exp){
		return structure(HttpStatus.NOT_ACCEPTABLE,exp.getMessage(),"Unauthorized User Role. ADMIN already Exists");
	}
	
	@ExceptionHandler(UserNotFoundByIdException.class)
	private ResponseEntity<Object> handleUserNotFoundById(UserNotFoundByIdException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"User ID not exists");
	}
	
	@ExceptionHandler(UnauthorizedUserException.class)
	private ResponseEntity<Object> handleUnauthorizedUserException(UnauthorizedUserException exp){
		return structure(HttpStatus.UNAUTHORIZED,exp.getMessage(),"Unauthorized User to Proceed");
	}
	
	@ExceptionHandler(UserDataNotExistsException.class)
	private ResponseEntity<Object> handleUserDataNotExistsException(UserDataNotExistsException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"User Data Not Exists");
	}
	
	@ExceptionHandler(DataAlreadyExistsException.class)
	private ResponseEntity<Object> handleDataAlreadyExistsException(DataAlreadyExistsException exp){
		return structure(HttpStatus.FOUND,exp.getMessage(),"Data Already Exists");
	}
	
	@ExceptionHandler(SchoolNotFoundByIdException.class)
	private ResponseEntity<Object> handleSchoolNotFoundByIdException(SchoolNotFoundByIdException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"School Data Not FOUND By ID");
	}
	
	@ExceptionHandler(ScheduleNotExistsException.class)
	private ResponseEntity<Object> handleScheduleNotExistsException(ScheduleNotExistsException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"Schedule Not Created Yet");
	}
	
	@ExceptionHandler(AcademicProgramNotExistsByIdException.class)
	private ResponseEntity<Object> handleAcademicProgramNotFoundById(AcademicProgramNotExistsByIdException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"No Program Exist by this ID");
	}
	
	@ExceptionHandler(IllegalRequestException.class)
	private ResponseEntity<Object> handleIllegalArgumentException(IllegalRequestException exp){
		return structure(HttpStatus.BAD_REQUEST,exp.getMessage(),"Illegal Request to Proceed");
	}
	
	@ExceptionHandler(EmptyListException.class)
	private ResponseEntity<Object> handleEmptyListException(EmptyListException exp){
		return structure(HttpStatus.NO_CONTENT,exp.getMessage(),"List is Empty");
	}
	
	@ExceptionHandler(SubjectNotFoundByIdException.class)
	private ResponseEntity<Object> handleSubjectNotFoundByIdException(SubjectNotFoundByIdException exp){
		return structure(HttpStatus.NOT_FOUND,exp.getMessage(),"No Subject Exist by this ID");
	}
}