package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolNotFoundById;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.service.SchoolService;

@Service
public class SchoolServiceImpl implements SchoolService{

	@Autowired
	private SchoolRepo schoolrepo;

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

	@Override
	public ResponseEntity<String> updateSchoolById(School school, int schoolId) {
		Optional<School> object = schoolrepo.findById(schoolId);
		if(object.isPresent()) {
			School existingObject = object.get();
			School newObject = new School();
			newObject.setSchoolId(existingObject.getSchoolId());
			newObject.setSchoolName(school.getSchoolName());
			newObject.setSchoolAddress(school.getSchoolAddress());
			newObject.setSchoolContact(school.getSchoolContact());
			newObject.setSchoolEmail(school.getSchoolEmail());
			
			schoolrepo.save(newObject);
			return new ResponseEntity<String>("Data updated Successfully", HttpStatus.OK);
		}
		else
			throw new SchoolNotFoundById("No School found by this ID to UPDATE");
	}

	@Override
	public ResponseEntity<String> findSchoolById(int schoolId) {
		Optional<School> obj = schoolrepo.findById(schoolId);
		if(obj.isPresent()) {
			return new ResponseEntity<String>("School Record found for ID "+schoolId,HttpStatus.FOUND);
		}
		else
			throw new SchoolNotFoundById("No School found for ID: "+schoolId);
	}

	@Override
	public ResponseEntity<String> deleteSchool(int schoolId) {
		Optional<School> obj = schoolrepo.findById(schoolId);
		if(obj.isPresent()) {
			School present = obj.get();
			schoolrepo.delete(present);
			return new ResponseEntity<String>("School Record DELETED Successfully",HttpStatus.OK);
		}
		else
			throw new SchoolNotFoundById("No School found by this ID to DELETE");
	}

}
