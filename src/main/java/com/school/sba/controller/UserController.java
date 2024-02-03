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
import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "User", description = "API endpoints that are related to User Entity")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Operation(description = "**Add Admin -** "
			+ "the API endpoint is used to register the ADMIN", 
			responses = {
					@ApiResponse(responseCode = "201", description = "Admin added", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to add admin", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> addAdmin(@RequestBody @Valid UserRequest request){
		return userService.addAdmin(request);
	}
	
	@Operation(description = "**Add User -** "
			+ "the API endpoint is used to register the user by ADMIN", 
			responses = {
					@ApiResponse(responseCode = "201", description = "user added", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to add user", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponse>> addUserByAdmin(@RequestBody @Valid UserRequest request){
		return userService.addUser(request);
	}
	
	@Operation(description = "**Find User by ID -** "
			+ "the API endpoint is used to fetch user", responses = {
					@ApiResponse(responseCode = "302", description = "user found", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "user not found", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
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
	
	@Operation(description = "**Delete User by ID -** "
			+ "the API endpoint is used to delete the user data based on the Id", responses = {
					@ApiResponse(responseCode = "200", description = "user deleted", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to delete user", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/{userid}")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(@PathVariable int userid){
		return userService.deleteUser(userid);
	}
	
	@Operation(description = "**Set User to an Academic Program -** "
			+ "the API endpoint is used to update the user to the program by Id", responses = {
					@ApiResponse(responseCode = "200", description = "user assigned to a program", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to assign a user", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademics(@PathVariable int userId, @PathVariable int programId){
		return userService.setUserToAcademics(userId,programId);
	}
	
	@Operation(description = "**Set Subject to Teacher -** "
			+ "the API endpoint is used to assign subject to the Teacher by Id", responses = {
					@ApiResponse(responseCode = "200", description = "subject assignet to a Teacher", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to assign a subject", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(@PathVariable int userId, @PathVariable int subjectId){
		return userService.addSubjectToTeacher(userId,subjectId);
	}
	
	@Operation(description = "**Find all Users -** "
			+ "the API endpoint is used to list of users", responses = {
					@ApiResponse(responseCode = "302", description = "user found", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "no user found", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findAllUsers(){
		return userService.findAllUsers();
	}
	
	@Operation(description = "**Find User by ROLE -** "
			+ "the API endpoint is used to fetch the user data based on the Role", responses = {
					@ApiResponse(responseCode = "302", description = "user found", content = {
							@Content(schema = @Schema(implementation = UserResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "user not found", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@GetMapping("/academic-programs/{programId}/user-roles/{userRole}/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> fetchUsersByRole(@PathVariable int programId,@PathVariable UserRole userRole){
		return userService.fetchUsersByRole(programId, userRole);
	}
	
}
