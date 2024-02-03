package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Subject", description = "API endpoints that are related to Subject Entity")
public class SubjectController {

	@Autowired
	private SubjectService subjectservice;
	
	@Operation(description = "**Add List of Subjects -** "
			+ "the API endpoint is used to add list of Subjects", responses = {
					@ApiResponse(responseCode = "201", description = "subjects added", content = {
							@Content(schema = @Schema(implementation = SubjectResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to add subjects", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/academic-programs/{programId}/subjects")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjectList(@PathVariable int programId, @RequestBody SubjectRequest request){
		return subjectservice.addSubjectList(programId,request);
	}
	
	@Operation(description = "**Fetch All subjects -** "
			+ "the API endpoint is used to fetch all the subjects", responses = {
					@ApiResponse(responseCode = "302", description = "subjects found", content = {
							@Content(schema = @Schema(implementation = SubjectResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to fetch subjects", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@GetMapping("/subjects")
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects(){
		return subjectservice.findAllSubject();
	}
	
}
