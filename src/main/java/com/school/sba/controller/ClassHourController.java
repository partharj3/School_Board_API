package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.exception.ApplicationExceptionHandler;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "ClassHour", description = "API endpoints that are related to Classhour Entity")
public class ClassHourController {

	@Autowired
	private ClassHourService classhourService;
	
	@Operation(description = "**Auto-Generate ClassHour- ** "
			+ "the API endpoint is used to auto-generate classhours for the Program", 
			responses = {
					@ApiResponse(responseCode = "201", description = "Classhour generated", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to auto-generate classhour", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> generateClassHour(@PathVariable int programId){
		return classhourService.generateClassHour(programId);
	}
	
	@Operation(description = "**Setting ClassHour Data- ** "
			+ "the API endpoint is used to set data to classhours", 
			responses = {
					@ApiResponse(responseCode = "200", description = "classhour updated", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to update classhour", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/class-hours")
	public ResponseEntity<ResponseStructure<List<String>>> updateClasshourList(@RequestBody List<ClassHourRequest> request){
		return classhourService.updateClasshourList(request);
	}
	
	@Operation(description = "**Write Excel of ClassHour(To Local Machine)- ** "
			+ "the API endpoint is used to create Excel data of classhour by taking Local Machine's Folder", 
			responses = {
					@ApiResponse(responseCode = "201", description = "Excel generated", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to generate excel", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PostMapping("/academic-program/{programId}/class-hours/write-excel")
	public ResponseEntity<ResponseStructure<String>> createExcelSheet(@PathVariable int programId,@RequestBody ExcelRequest request) {
		return classhourService.createExcelSheet(programId,request);
	}
	
	@Operation(description = "**Write Excel of ClassHour(To Presentation Layer)- ** "
			+ "the API endpoint is used to create Excel data of classhour by taking MultipartFile", 
			responses = {
					@ApiResponse(responseCode = "201", description = "Excel generated", content = {
							@Content(schema = @Schema(implementation = ClassHourResponse.class)) }),
					@ApiResponse(responseCode = "400", description = "failed to generate excel", content = {
							@Content(schema = @Schema(implementation = ApplicationExceptionHandler.class, 
									                  description = "Method: structure")) }) 
					    })
	@PostMapping("/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?> writeToExcel(@PathVariable int programId,@PathVariable LocalDate fromDate,
			                              @PathVariable LocalDate toDate, @RequestParam MultipartFile file) throws IOException{
		return classhourService.writeToExcel(programId,fromDate,toDate,file);
	}
}
