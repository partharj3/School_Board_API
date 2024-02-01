package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class SchoolController {

	@Autowired
	private SchoolService schoolService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users/{adminId}/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(@RequestBody @Valid SchoolRequest school,@PathVariable int adminId){
		return schoolService.addSchool(school, adminId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/schools/{schoolId}")
	public ResponseEntity<ResponseStructure<String>> deleteSchool(@PathVariable int schoolId){
		return schoolService.deleteSchool(schoolId);
	}
	
//	@PutMapping("/schools/{schoolId}")
//	public ResponseEntity<String> updateSchoolById(@RequestBody School school, @PathVariable int schoolId){
//		return service.updateSchoolById(school, schoolId);
//	}
//	
//	@GetMapping("/schools/{schoolId}")
//	public ResponseEntity<String> findSchoolById(@PathVariable int schoolId){
//		return service.findSchoolById(schoolId);
//	}
//	
//	@DeleteMapping("/schools/{schoolId}")
//	public ResponseEntity<String> deleteSchool(@PathVariable int schoolId){
//		return service.deleteSchool(schoolId);
//	}
	
}
