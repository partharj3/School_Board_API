package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.util.ResponseStructure;

public interface SchoolService {
//	public ResponseEntity<String> updateSchoolById(School school, int schoolId);
//	public ResponseEntity<String> findSchoolById(int schoolId);
//	public ResponseEntity<String> deleteSchool(int schoolId);
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest school,  int userid);
}
