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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Academic Program", description = "API endpoints that are related to Academin Program Entity")
public class AcademicProgramController {

	@Autowired
	private AcademicProgramService academicsService;
	
	@Operation(description = "**Add Academic Program- ** "
			+ "the API endpoint is used to add Program", 
			responses = {
					@ApiResponse(responseCode = "201", description = "program created", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to add program", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(@PathVariable int schoolId,@RequestBody AcademicProgramRequest request){
		return academicsService.addAcademicProgram(schoolId,request);
	}
	
	@Operation(description = "**Fetch all Academic Programs- ** "
			+ "the API endpoint is used to fetch list of Program", 
			responses = {
					@ApiResponse(responseCode = "302", description = "programs founf", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "no program found", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(@PathVariable int schoolId){
		return academicsService.findAllAcademicsPrograms(schoolId);
	}
	
	@Operation(description = "**Update Subject List- ** "
			+ "the API endpoint is used to update Subject List of Programs", 
			responses = {
					@ApiResponse(responseCode = "200", description = "updated subject list to Program", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to update subject list to program", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectList(@PathVariable int programId, @RequestBody SubjectRequest request){
		return academicsService.updateSubjectList(programId,request);
	}
	
	@Operation(description = "**Delete Academic Program by ID- ** "
			+ "the API endpoint is used to delete Program by Id", 
			responses = {
					@ApiResponse(responseCode = "200", description = "program deleted", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to delete program", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<String>> deleteAcademicProgram(@PathVariable int programId){
		return academicsService.deleteAcademicProgram(programId);
	}
	
	@Operation(description = "**Set auto-repeat schedule ON/OFF- ** "
			+ "the API endpoint is used to set auto-repeat classhours for the Program", 
			responses = {
					@ApiResponse(responseCode = "200", description = "auto-repeat set ON/OFF", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to set auto-repeat", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-program/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> setAutoRepeatSchedule(@PathVariable int programId,
																			@RequestParam("auto-repeat-schedule") boolean autoRepeatSchedule){
		return academicsService.setAutoRepeatSchedule(programId,autoRepeatSchedule);
	}
}
