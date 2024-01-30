package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;

public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> addUser(UserRequest request);

	ResponseEntity<ResponseStructure<UserResponse>> findUser(int userid);

	ResponseEntity<ResponseStructure<UserResponse>> deleteUser(int userid);

	ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademics(int userId, int programId);

	ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int userId, int subjectId);

	ResponseEntity<ResponseStructure<UserResponse>> addAdmin(UserRequest request);

	ResponseEntity<ResponseStructure<List<UserResponse>>> findAllUsers();

	ResponseEntity<ResponseStructure<List<UserResponse>>> fetchUsersByRole(int programId, String userRole);	
}

