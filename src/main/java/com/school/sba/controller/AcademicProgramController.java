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

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramController {

	@Autowired
	private AcademicProgramService academicsService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(@PathVariable int schoolId,@RequestBody AcademicProgramRequest request){
		return academicsService.addAcademicProgram(schoolId,request);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(@PathVariable int schoolId){
		return academicsService.findAllAcademicsPrograms(schoolId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectList(@PathVariable int programId, @RequestBody SubjectRequest request){
		return academicsService.updateSubjectList(programId,request);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<String>> deleteAcademicProgram(@PathVariable int programId){
		return academicsService.deleteAcademicProgram(programId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-program/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> setAutoRepeatSchedule(@PathVariable int programId,
																			@RequestParam("auto-repeat-schedule") boolean autoRepeatSchedule){
		return academicsService.setAutoRepeatSchedule(programId,autoRepeatSchedule);
	}
}
