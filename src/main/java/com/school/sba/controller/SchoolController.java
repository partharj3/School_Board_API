package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "School", description = "API endpoints that are related to School Entity")
public class SchoolController {

	@Autowired
	private SchoolService schoolService;
	
	@Operation(description = "**Add User -** "
			+ "the API endpoint is used to register the school by ADMIN", 
			responses = {
					@ApiResponse(responseCode = "201", description = "school added by ADMIN", content = {
							@Content(schema = @Schema(implementation = SchoolResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to add school", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users/{adminId}/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(@RequestBody @Valid SchoolRequest school,@PathVariable int adminId){
		return schoolService.addSchool(school, adminId);
	}
	
	@Operation(description = "**Delete School by ID -** "
			+ "the API endpoint is used to delete the school data based on the Id", responses = {
					@ApiResponse(responseCode = "200", description = "school deleted", content = {
							@Content(schema = @Schema(implementation = SchoolResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to delete school", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/schools/{schoolId}")
	public ResponseEntity<ResponseStructure<String>> deleteSchool(@PathVariable int schoolId){
		return schoolService.deleteSchool(schoolId);
	}
	
}
