package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.UnauthorizedRoleException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ResponseStructure<UserResponse> structure;
	
	private User mapToUser(UserRequest request) {
		return User.builder()
				   .username(request.getUsername())
				   .password(request.getPassword())
				   .firstname(request.getFirstname())
				   .lastname(request.getLastname())
				   .contactNo(request.getContactNo())
				   .email(request.getEmail())
				   .userRole(request.getUserRole())
				   .build();
	}
	
	private UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				           .userId(user.getUserId())
				           .username(user.getUsername())
				           .firstname(user.getFirstname())
						   .lastname(user.getLastname())
						   .email(user.getEmail())
						   .userRole(user.getUserRole())
						   .build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUser(UserRequest request) {
				
		List<User> exist = userRepo.findUserByUserRole(request.getUserRole());
		if(exist.size()==0 && request.getUserRole()==UserRole.ADMIN || request.getUserRole()!=UserRole.ADMIN) {
			User user = userRepo.save(mapToUser(request));
			
			structure.setStatusCode(HttpStatus.CREATED.value());
			structure.setMessage("User Created Successfully");
			structure.setData(mapToUserResponse(user));
			
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
		}
		else
			throw new UnauthorizedRoleException("Cannot Proceed Further");
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(int userid) {
		User user = userRepo.findById(userid).orElseThrow(()-> new UserNotFoundByIdException("Failed to FETCH the user"));
		
		structure.setStatusCode(HttpStatus.FOUND.value());
		structure.setMessage("User Data Found");
		structure.setData(mapToUserResponse(user));
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.FOUND);
	}

}
