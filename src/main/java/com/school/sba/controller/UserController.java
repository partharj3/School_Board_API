package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.enums.UserRole;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class UserController {

	@Autowired
	private AcademicProgramRepository arepo;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> addAdmin(@RequestBody @Valid UserRequest request){
		return userService.addAdmin(request);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponse>> addUserByAdmin(@RequestBody @Valid UserRequest request){
		return userService.addUser(request);
	}
	
	@GetMapping("/users/{userid}")
	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(@PathVariable int userid){
		return userService.findUser(userid);
	}
	
	/**
	 *  for multiple authorized people use:  
	 * 	@PreAuthorize("hasAuthority('ROLE 1') OR hasAuthority('ROLE 2')")
	 * 
	 * @param userid
	 * @return
	 */
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/{userid}")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(@PathVariable int userid){
		return userService.deleteUser(userid);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademics(@PathVariable int userId, @PathVariable int programId){
		return userService.setUserToAcademics(userId,programId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(@PathVariable int userId, @PathVariable int subjectId){
		return userService.addSubjectToTeacher(userId,subjectId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findAllUsers(){
		return userService.findAllUsers();
	}

	@GetMapping("/academic-programs/{programId}/user-roles/{userRole}/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> fetchUsersByRole(@PathVariable int programId,@PathVariable String userRole){
		return userService.fetchUsersByRole(programId, userRole);
	}
	
}
