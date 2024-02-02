package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.DataAlreadyExistsException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UnauthorizedRoleException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ScheduleRepository;
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
	private AcademicProgramRepository academicsRepo;
	
	@Autowired
	private ScheduleRepository scheduleRepo;
	
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
	}

	
	@Override
	public ResponseEntity<ResponseStructure<String>> deleteSchool(int schoolId) {
		return schoolrepo.findById(schoolId)
				.map(school ->{
					if(!school.isDeleted()) {
						school.setDeleted(true);
						schoolrepo.save(school);
					
						ResponseStructure<String> structure = new ResponseStructure<>();
						
						structure.setStatusCode(HttpStatus.OK.value());
						structure.setMessage("School: "+schoolId+" DELETED");
						structure.setData("Deleted Successfully");
					
						return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
					}
					else 
						throw new IllegalRequestException("Failed to DELETE School");
				})
				.orElseThrow(() -> new SchoolNotFoundByIdException("Failed to DELETE School"));
	}

	
	@Override
	public void permanentlyDeleteSchool() {
	    List<School> schools = schoolrepo.findByIsDeletedTrue();
	    if (!schools.isEmpty()) {
	        schools.forEach(school -> {
	        	
	            academicsRepo.deleteAll(school.getAcademicPrograms());
	            userRepo.deleteAll(userRepo.findByUserRoleNotAndUserSchool(UserRole.ADMIN, school));
	            
	            // Admin's Foreign key relation with the school has to be removed !
	            userRepo.findUserByUserRole(UserRole.ADMIN).forEach(user -> {
	            	if(user.getUserSchool().getSchoolId()==school.getSchoolId()) {
	            		user.setUserSchool(null);
	            		userRepo.save(user);
	            	}
	            });
	        });
	        
            schoolrepo.deleteAll();

            System.out.println("School Deleted Permanently");
	    } else {
	        System.out.println("Nothing to DELETE :: School");
	    }
	}
}
