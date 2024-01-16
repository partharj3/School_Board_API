package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.service.SchoolService;

@RestController
public class SchoolController {

	@Autowired
	private SchoolService service;
	
//	@PostMapping("/schools")
//	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(@RequestBody SchoolRequest school){
//		return service.addSchool(school);
//	}
	
	@PutMapping("/schools/{schoolId}")
	public ResponseEntity<String> updateSchoolById(@RequestBody School school, @PathVariable int schoolId){
		return service.updateSchoolById(school, schoolId);
	}
	
	@GetMapping("/schools/{schoolId}")
	public ResponseEntity<String> findSchoolById(@PathVariable int schoolId){
		return service.findSchoolById(schoolId);
	}
	
	@DeleteMapping("/schools/{schoolId}")
	public ResponseEntity<String> deleteSchool(@PathVariable int schoolId){
		return service.deleteSchool(schoolId);
	}
	
}
