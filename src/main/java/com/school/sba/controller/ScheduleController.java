package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Schedule", description = "API endpoints that are related to Schedule Entity")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;
	
	@Operation(description = "**Add Schedule -** "
			+ "the API endpoint is used to add proper schedule for School", responses = {
					@ApiResponse(responseCode = "201", description = "schedule created", content = {
							@Content(schema = @Schema(implementation = ScheduleResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to add schedule", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/schedules")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> addSchedule(@RequestBody @Valid ScheduleRequest request,@PathVariable int schoolId){
		return scheduleService.addSchedule(request,schoolId);
	}
	
	@Operation(description = "**Find Schedule -** "
			+ "the API endpoint is used to fetch schedule", responses = {
					@ApiResponse(responseCode = "302", description = "schedule found", content = {
							@Content(schema = @Schema(implementation = SubjectResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to fetch schedule", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@GetMapping("/schools/{schoolId}/schedules")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(@PathVariable int schoolId){
		return scheduleService.findSchedule(schoolId);
	}
	
	@Operation(description = "**Update Schedule -** "
			+ "the API endpoint is used to update the schedule", responses = {
					@ApiResponse(responseCode = "200", description = "schedule updated", content = {
							@Content(schema = @Schema(implementation = ScheduleResponse.class)) }),
					@ApiResponse(responseCode = "404", description = "failed to update schedule", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class)) }) })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/schedules/{scheduleId}")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(@PathVariable int scheduleId, @RequestBody @Valid ScheduleRequest request){
		return scheduleService.updateSchedule(scheduleId,request);
	}
	
}
