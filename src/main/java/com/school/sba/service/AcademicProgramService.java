package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;

public interface AcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,AcademicProgramRequest request);

	ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicsPrograms(int schoolId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectList(int programId, SubjectRequest request);
}
