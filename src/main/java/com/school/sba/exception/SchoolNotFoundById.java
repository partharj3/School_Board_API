package com.school.sba.exception;

public class SchoolNotFoundById extends RuntimeException{

	private String message;
	
	public SchoolNotFoundById(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return super.getMessage();
	}
	
}
