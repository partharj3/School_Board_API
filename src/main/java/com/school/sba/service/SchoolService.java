package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.School;

public interface SchoolService {
	public ResponseEntity<String> updateSchoolById(School school, int schoolId);
	public ResponseEntity<String> findSchoolById(int schoolId);
	public ResponseEntity<String> deleteSchool(int schoolId);
}
