package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.AdminAlreadyExistsException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UnauthorizedRoleException;
import com.school.sba.exception.UserDataNotExistsException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepo;
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
	private SchoolRepo schoolRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ClassHourRepository classhourRepo;
	
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
				   .userRole(UserRole.valueOf(request.getUserRole()))
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
	public ResponseEntity<ResponseStructure<UserResponse>> addAdmin(UserRequest request) {
		String role = request.getUserRole();
		if(role.equals(UserRole.ADMIN.name())) {
			if(!userRepo.existsByUserRole(UserRole.valueOf(role)))  {
				User user = mapToUser(request);
				user = userRepo.save(user);
				
				structure.setStatusCode(HttpStatus.CREATED.value());
				structure.setMessage("ADMIN registered Successfully");
				structure.setData(mapToUserResponse(user));
				
				return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
			}
			else
				throw new AdminAlreadyExistsException("Failed to register ADMIN");
		}
		else {
			throw new UnauthorizedRoleException("Failed to register ADMIN");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUser(UserRequest request) {
		String admin = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findUserByUsername(admin).map(
				userAdmin -> {
					if(userAdmin.getUserSchool()!=null && !request.getUserRole().equals(UserRole.ADMIN.name())) {
						User user = mapToUser(request);
						user.setUserSchool(userAdmin.getUserSchool());
						schoolRepo.save(user.getUserSchool());
						userRepo.save(user);
						
						structure.setStatusCode(HttpStatus.CREATED.value());
						structure.setMessage(user.getUserRole()+" Created Successfully");
						structure.setData(mapToUserResponse(user));
						
						return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
					}
					else
						throw new IllegalRequestException("Failed to ADD the User");
					
				}				
			).orElseThrow(() -> new UsernameNotFoundException("Admin Not Found from the Session"));
		}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(int userid) {
		User user = userRepo.findById(userid).orElseThrow(()-> new UserNotFoundByIdException("Failed to FETCH the user"));
		
		// Logic to check is that already deleted or not
		if(!user.isDeleted()) {
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
		if(!user.isDeleted() && !user.getUserRole().equals(UserRole.ADMIN)) {
			user.setDeleted(true);
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
				
				if(user.isDeleted()) throw new IllegalRequestException("User Already Deleted");
				
				AcademicProgram pro = null;
				if(user.getUserRole().equals(UserRole.ADMIN))
					throw new IllegalRequestException("Failed to SET user to THIS PROGRAM");
				else{
					pro = academicRepo.findById(programId)
						.map(program -> {
							
							if(program.isDeleted()) throw new IllegalRequestException("Program Already Deleted");
							
							List<User> userList = program.getUsers();
							if(userList.contains(user))
								throw new IllegalRequestException("User Record already present in this Program");
							
							if(user.getUserRole().equals(UserRole.TEACHER)) {
								
								if(user.getSubject()==null){ 
									throw new IllegalRequestException("Teacher should assigned to a SUBJECT");}
								
								if(program.getSubjectList()==null || program.getSubjectList().isEmpty()){ 
									throw new IllegalRequestException("Program should assigned with SUBJECTS to Add TEACHER");}
								
								if(!program.getSubjectList().contains(user.getSubject())){
									throw new IllegalRequestException("Irrelevant TEACHER to the Academic Program");
								}
							}
							
							user.getAcademicprograms().add(program);
							userRepo.save(user);
							program.getUsers().add(user);
							program = academicRepo.save(program);
							return program;
						}
						)
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
					if(user.isDeleted()) throw new IllegalRequestException("User Already Deleted");
					if(user.getUserRole().equals(UserRole.TEACHER)) {
						subjectRepo.findById(subjectId)
						.map(subject -> {
							if(user.getSubject()==null) {
								user.setSubject(subject);
								return userRepo.save(user);
							}else {
								throw new IllegalRequestException("TEACHER already engaged with the "
							                                      +user.getSubject().getSubjectName()+" Subject");
							}
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

	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findAllUsers() {
		List<User> list = userRepo.findAll();
		List<UserResponse> responseList = new ArrayList<UserResponse>();
		ResponseStructure<List<UserResponse>> structure = new ResponseStructure<>();
		if(!list.isEmpty()) {
			
			for(User user : list) {
				if(!user.isDeleted())
					responseList.add(mapToUserResponse(user));
			}
			structure.setMessage("List of Users");
		}
		else {
			structure.setMessage("List is EMPTY");
		}
		structure.setStatusCode(HttpStatus.FOUND.value());
		structure.setData(responseList);
		
		return new ResponseEntity<ResponseStructure<List<UserResponse>>>(structure, HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> fetchUsersByRole(int programId, UserRole userRole) {
		return academicRepo.findById(programId)
				.map(program -> {
					
					if(program.isDeleted()) throw new IllegalRequestException("Program Already Deleted");
					
					if(userRole.equals(UserRole.ADMIN))
						throw new IllegalRequestException("NOT PERMITED TO FETCH ADMIN");
					
	/** STREAM **/
//					List<User> userList = program.getUsers().stream().filter(user -> user.getUserRole().equals(role)).toList();
					
	/** REPOSITORY METHOD: **/
					
					List<User> userList= userRepo.findByUserRoleAndAcademicprograms_ProgramId(userRole, programId);
	
					if(!userList.isEmpty()) {
						
						ResponseStructure<List<UserResponse>> structure = new ResponseStructure<>();
						
						List<UserResponse> response = userList.stream()
								                              .filter(user -> !user.isDeleted()) // Non deleted Users
								                              .map(user->mapToUserResponse(user))
								                              .toList();
						
						/*for(User user : userList) {
							if(user.getUserRole().equals(role)) {
								response.add(mapToUserResponse(user));
							}
						}*/
						structure.setStatusCode(HttpStatus.FOUND.value());
						structure.setMessage("List of "+userRole+" belongs to Program "+program.getProgramId());
						structure.setData(response);
						
						return new ResponseEntity<ResponseStructure<List<UserResponse>>>(structure, HttpStatus.FOUND);
					}
					else 
						throw new UserDataNotExistsException("No users associated with the academic program "+programId+" with role "+userRole);
					
				})
				.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to FETCH "+userRole+" List"));
	}
	
	public void permanentlyDeleteUsers() {
		List<User> usersToBeDeleted = userRepo.findByIsDeletedTrue();
		if(!usersToBeDeleted.isEmpty()) {
			usersToBeDeleted.forEach( user -> {
				
				// User's Academic becomes NULL
				user.getAcademicprograms()
					.forEach( program -> {
					
						// Teacher's classhour becomes NULL
						program.getClasshourList().forEach(classhour ->{
							if(classhour.getUser()==user) {
								classhour.setUser(null);
								classhourRepo.save(classhour);
							}
						});
						
						int index = program.getUsers().indexOf(user);
						program.getUsers().set(index, null);
						
						academicRepo.save(program);
					});
//				Updated & Deleted			
				userRepo.delete(userRepo.save(user));
			});
			System.out.println("Users DATA Cleared Permanently");
		}else
			System.out.println("Nothing to DELETE :: User");	
	}	
}
