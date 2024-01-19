package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> addUser(@RequestBody @Valid UserRequest request){
		return userService.addUser(request);
	}
	
	@GetMapping("/users/{userid}")
	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(@PathVariable int userid){
		return userService.findUser(userid);
	}
	
	@DeleteMapping("/users/{userid}")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(@PathVariable int userid){
		return userService.deleteUser(userid);
	}
	
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademics(@PathVariable int userId, @PathVariable int programId){
		return userService.setUserToAcademics(userId,programId);
	}
	
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(@PathVariable int userId, @PathVariable int subjectId){
		return userService.addSubjectToTeacher(userId,subjectId);
	}
	
}
