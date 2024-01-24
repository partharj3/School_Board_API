package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.DataAlreadyExistsException;
import com.school.sba.exception.UnauthorizedRoleException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService{

	@Autowired
	private SchoolRepo schoolrepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ResponseStructure<SchoolResponse> structure;

	private School mapToSchool(SchoolRequest request) {
		return School.builder()
					 .schoolName(request.getSchoolName())
					 .schoolContact(request.getSchoolContact())
					 .schoolEmail(request.getSchoolEmail())
					 .schoolAddress(request.getSchoolAddress())
					 .build();
	}
	
	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder()
							 .schoolId(school.getSchoolId())
							 .schoolName(school.getSchoolName())
							 .schoolContact(school.getSchoolContact())
							 .schoolEmail(school.getSchoolEmail())
							 .schoolAddress(school.getSchoolAddress())
							 .build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest request, int adminId) {
		
		return userRepo.findById(adminId)
				.map(user -> {
					if(user.getUserRole().equals(UserRole.ADMIN)) {
						if(user.getUserSchool()==null) {
							School school = schoolrepo.save(mapToSchool(request));
							user.setUserSchool(school);
							userRepo.save(user);
							
							structure.setStatusCode(HttpStatus.CREATED.value());
							structure.setMessage("School Created Successfully By the ADMIN");
							structure.setData(mapToSchoolResponse(school));
							
							return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.CREATED);
						}
						else 
							throw new DataAlreadyExistsException("Failed to CREATE a new School Data");
					}
					else
						throw new UnauthorizedRoleException("Failed to Create School !");
				})
				.orElseThrow(()->new UserNotFoundByIdException("Failed to Create School !"));
		
//		User user = userRepo.findById(userId).orElseThrow(()->new UserNotFoundByIdException("Failed to Create School !"));
//		if(user.getUserRole()==UserRole.ADMIN) {
//			if(schoolrepo.count()!=0)
//					throw new DataAlreadyExistsException("Failed to CREATE a new School Data");
//			
//			School saved = schoolrepo.save(mapToSchool(request));
//			
//			structure.setStatusCode(HttpStatus.CREATED.value());
//			structure.setMessage("School Created Successfully By the ADMIN");
//			structure.setData(mapToSchoolResponse(saved));
//			
//			return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.CREATED);
//		}
//		else
//			throw new UnauthorizedUserException("Failed to Create School !");
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Override
//	public ResponseEntity<ResponseStructure<SchoolResponse>> addSchool(SchoolRequest schoolreq) {
//
//		School school = new School();
//		school.setSchoolName(schoolreq.getSchoolName());
//		school.setSchoolEmail(schoolreq.getSchoolEmail());
//		school.setSchoolContact(schoolreq.getSchoolContact());
//		school.setSchoolAddress(schoolreq.getSchoolAddress());
//		
//		schoolrepo.save(school);
//		
//		SchoolResponse response = new SchoolResponse();
//		response.setSchoolName(school.getSchoolName());
//		response.setSchoolContact(school.getSchoolContact());
//		response.setSchoolAddress(school.getSchoolAddress());
//		
//		ResponseStructure<SchoolResponse> structure = new ResponseStructure<>();
//		structure.setStatusCode(HttpStatus.CREATED.value());
//		structure.setMessage("Data inserted Successfully");
//		structure.setData(response);
//		
//		return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.CREATED);
//	}

//	@Override
//	public ResponseEntity<String> updateSchoolById(School school, int schoolId) {
//		Optional<School> object = schoolrepo.findById(schoolId);
//		if(object.isPresent()) {
//			School existingObject = object.get();
//			School newObject = new School();
//			newObject.setSchoolId(existingObject.getSchoolId());
//			newObject.setSchoolName(school.getSchoolName());
//			newObject.setSchoolAddress(school.getSchoolAddress());
//			newObject.setSchoolContact(school.getSchoolContact());
//			newObject.setSchoolEmail(school.getSchoolEmail());
//			
//			schoolrepo.save(newObject);
//			return new ResponseEntity<String>("Data updated Successfully", HttpStatus.OK);
//		}
//		else
//			throw new SchoolNotFoundById("No School found by this ID to UPDATE");
//	}
//
//	@Override
//	public ResponseEntity<String> findSchoolById(int schoolId) {
//		Optional<School> obj = schoolrepo.findById(schoolId);
//		if(obj.isPresent()) {
//			return new ResponseEntity<String>("School Record found for ID "+schoolId,HttpStatus.FOUND);
//		}
//		else
//			throw new SchoolNotFoundById("No School found for ID: "+schoolId);
//	}
//
//	@Override
//	public ResponseEntity<String> deleteSchool(int schoolId) {
//		Optional<School> obj = schoolrepo.findById(schoolId);
//		if(obj.isPresent()) {
//			School present = obj.get();
//			schoolrepo.delete(present);
//			return new ResponseEntity<String>("School Record DELETED Successfully",HttpStatus.OK);
//		}
//		else
//			throw new SchoolNotFoundById("No School found by this ID to DELETE");
//	}

}
