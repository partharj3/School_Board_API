package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.enums.ProgramType;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService{

	@Autowired
	private AcademicProgramRepository academicRepo;
	
	@Autowired
	private SchoolRepo schoolrepo;
	
	@Autowired
	private SubjectRepository subjectrepo;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;
	
	private AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academics) {
		List<String> subjects = new ArrayList<>();
		academics.getSubjectList().forEach(subject -> {
			subjects.add(subject.getSubjectName());
		});
		
		return AcademicProgramResponse.builder()
				.programId(academics.getProgramId())
				.programType(academics.getProgramType())
				.programName(academics.getProgramName())
				.beginsAt(academics.getBeginsAt())
				.endsAt(academics.getEndsAt())
				.subjects(subjects)
				.build();
	}

	private AcademicProgram mapToAcademiProgram(AcademicProgramRequest request) {
		return AcademicProgram.builder()
				.programType(request.getProgramType())
				.programName(request.getProgramName())
				.beginsAt(request.getBeginsAt())
				.endsAt(request.getEndsAt())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,AcademicProgramRequest request) {
		return schoolrepo.findById(schoolId)
				.map(school -> {
					AcademicProgram academics = mapToAcademiProgram(request);
					academics.setAcademicSchool(school);
					academicRepo.save(academics);
					school.setSchoolId(schoolId);
					school.setAcademicProgram(academicRepo.findAll());
					schoolrepo.save(school);
					
					structure.setStatusCode(HttpStatus.CREATED.value());
					structure.setMessage("Academic Program Created Successfully!!");
					structure.setData(mapToAcademicProgramResponse(academics));
					
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
				})
				.orElseThrow(()-> new SchoolNotFoundByIdException("Failed to CREATE Academics"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicsPrograms(int schoolId) {
		return schoolrepo.findById(schoolId)
				.map(school -> {
					List<AcademicProgram> list = school.getAcademicProgram();
					
					List<AcademicProgramResponse> responseList = new ArrayList<>();
					
					ResponseStructure<List<AcademicProgramResponse>> structure = new ResponseStructure<>();
					if(!list.isEmpty()) {
						for(AcademicProgram program : list) {
							responseList.add(mapToAcademicProgramResponse(program));
						}
						structure.setStatusCode(HttpStatus.FOUND.value());
						structure.setMessage("Academic Program List found for the "+school.getSchoolName());
					}else {
						structure.setStatusCode(HttpStatus.FOUND.value());
						structure.setMessage("Academic Program List is EMPTY for the "+school.getSchoolName());
					}
					
					structure.setData(responseList);
					return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(structure,HttpStatus.FOUND);
				})
				.orElseThrow(()-> new SchoolNotFoundByIdException("Failed to FETCH All Academics"));
	}


	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjectList(int programId, SubjectRequest request) {
		System.out.println("************UPDATION******************");
		return academicRepo.findById(programId)
				.map(program ->{
					List<Subject> existing = (program.getSubjectList()!=null ? program.getSubjectList() : new ArrayList<>());
					
					request.getSubjectNames().forEach(subjectName -> {
						boolean isPresent = false;
						for(Subject subject : existing) {
							isPresent = (subjectName.equalsIgnoreCase(subject.getSubjectName()) ? true : false);
							if(isPresent)
								break;
							}
						if(!isPresent) {
							existing.add(subjectrepo.findBySubjectName(subjectName)
									.orElseGet(() -> 
										subjectrepo.save(Subject.builder().subjectName(subjectName).build())));
						}
					});
					
					List<Subject> toBeRemoved =  new ArrayList<>();
					existing.forEach(subject -> {
						boolean isPresent = false;
						for(String name: request.getSubjectNames()) {
							isPresent = (subject.getSubjectName().equalsIgnoreCase(name) ? true : false);
							if(isPresent)
								break;
						}
						if(!isPresent) toBeRemoved.add(subject);
					});
					
					existing.removeAll(toBeRemoved);
					 program.setSubjectList(existing);
				        academicRepo.save(program);

				        structure.setStatusCode(HttpStatus.OK.value());
				        structure.setMessage("Subject List updated to PROGRAM " + program.getProgramName());
				        structure.setData(mapToAcademicProgramResponse(program));

				        return new ResponseEntity<>(structure, HttpStatus.OK);
				    })
				    .orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to UPDATE Subject List to this Program ID"));		
	}	

	private List<Subject> mapToSubjectList(List<String> subjectNames) {
		List<Subject> list = new ArrayList<>();
		subjectNames.forEach(subName -> {
			Subject s = new Subject();
			s.setSubjectName(subName);
			list.add(s);
		});
		return list;
	}

	private static String removeUpperCamelCaseAndExtraSpace(String str) {
	    return str
	    		.replaceAll("(\\p{Lu})", " $1") // removes camel casing
	    		.replaceAll("\\s+", " ")        // removes extra white spaces
	    		.trim().toLowerCase();          // removes leading, trailing white spaces
	}
}
