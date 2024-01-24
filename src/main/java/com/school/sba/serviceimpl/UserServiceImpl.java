package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UnauthorizedRoleException;
import com.school.sba.exception.UserDataNotExistsException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
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
	private AcademicProgramRepository academicRepo;
	
	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ResponseStructure<UserResponse> structure;
	
	private User mapToUser(UserRequest request) {
		return User.builder()
				   .username(request.getUsername())
				   .password(passwordEncoder.encode(request.getPassword()))
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
		
//		List<User> exist = userRepo.findUserByUserRole(request.getUserRole());
//		if(exist.size()==0 && request.getUserRole()==UserRole.ADMIN || request.getUserRole()!=UserRole.ADMIN) {
//			User user1 = mapToUser(request);
//			user1.setIsDeleted(false);
//			
//			User user = userRepo.save(user1);
//			
//			structure.setStatusCode(HttpStatus.CREATED.value());
//			structure.setMessage("User Created Successfully");
//			structure.setData(mapToUserResponse(user));
//			
//			return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
//		}
//		else
//			throw new UnauthorizedRoleException("Cannot Proceed Further");
		
		UserRole role = request.getUserRole();
		if(role!=UserRole.ADMIN || role==UserRole.ADMIN && !userRepo.existsByUserRole(role) ) {
			User user1 = mapToUser(request);
			user1.setIsDeleted(false);
			
			User user = userRepo.save(user1);
			
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
		
		// Logic to check is that already deleted or not
		boolean deleted = user.getIsDeleted();
		if(!deleted) {
			structure.setStatusCode(HttpStatus.FOUND.value());
			structure.setMessage("User Data Found");
			structure.setData(mapToUserResponse(user));
			
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.FOUND);
		}
		throw new UserNotFoundByIdException("Failed to FETCH the user");
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUser(int userid) {
		User user = userRepo.findById(userid).orElseThrow(()-> new UserNotFoundByIdException("Failed to DELETE the user"));
		if(!user.getIsDeleted()) {
			user.setIsDeleted(true);
			userRepo.save(user);
			
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setMessage("User Data Deleted");
			structure.setData(mapToUserResponse(user));
		}
		else 
			throw new UserDataNotExistsException("Failed to DELETE the user");
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> setUserToAcademics(int userId, int programId) {
		return userRepo.findById(userId)
				.map(user -> {
					AcademicProgram pro = null;
					if(user.getUserRole().equals(UserRole.ADMIN))
						throw new IllegalRequestException("Failed to SET user to THIS PROGRAM");
					else{
						pro = academicRepo.findById(programId)
							.map(program -> {
								user.getAcademicprograms().add(program);
								userRepo.save(user);
								program.getUsers().add(user);
								program = academicRepo.save(program);
								return program;
								})
							.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to SET user to THIS PROGRAM"));
						}
					structure.setStatusCode(HttpStatus.OK.value());
					structure.setMessage(user.getUserRole()+" assigned with the Program "+pro.getProgramName());
					structure.setData(mapToUserResponse(user));
					
					return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK); 
				})
				.orElseThrow(()-> new UserNotFoundByIdException("Failed to SET user to THIS PROGRAM"));
	}

	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int userId, int subjectId) {
		return userRepo.findById(userId)
				.map( user -> {
					if(user.getUserRole().equals(UserRole.TEACHER)) {
						subjectRepo.findById(subjectId)
						.map(subject -> {
							user.setSubject(subject);
							return userRepo.save(user);
						})
						.orElseThrow(() -> new SubjectNotFoundByIdException("Failed to ADD Subject to user"));
					
					structure.setStatusCode(HttpStatus.OK.value());
					structure.setMessage("Subject set to TEACHER");
					structure.setData(mapToUserResponse(user));
					
					return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.OK);
					}
					throw new IllegalRequestException("Failed to ADD, Invalid User");
				})
				.orElseThrow(() -> new UserNotFoundByIdException("Failed to ADD Subject to user"));
	}

}
