package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.EmptyListException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService{

	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private AcademicProgramRepository academicsRepo;
	
	private SubjectResponse mapToSubjectResponse(Subject subject) {
		return SubjectResponse.builder()
				   .subjectId(subject.getSubjectId())
				   .subjectName(subject.getSubjectName())
				   .build();
	}	
	
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
	
	private static String removeUpperCamelCaseAndExtraSpace(String str) {
	    return str
	    		.replaceAll("(\\p{Lu})", " $1") // removes camel casing
	    		.replaceAll("\\s+", " ")        // removes extra white spaces
	    		.trim().toLowerCase();          // removes leading, trailing white spaces
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjectList(int programId,SubjectRequest request) {
		
		return academicsRepo.findById(programId)
				.map(program -> {
					
					if(program.isDeleted()) throw new IllegalRequestException("Program Already Deleted");
					
					List<Subject> subjects = new ArrayList<>();
					List<String> sb = request.getSubjectNames();
					sb.forEach(name -> {

						Subject subj = subjectRepo.findBySubjectName(name).map(subject -> {
										return subject;
										})
										.orElseGet(() -> {
										Subject subject = new Subject();
										subject.setSubjectName(name.toLowerCase());
										subjectRepo.save(subject);
										return subject;
										});
					
						subjects.add(subj);
					});
					
					program.setSubjectList(subjects);
					academicsRepo.save(program);
					
					ResponseStructure<AcademicProgramResponse> structure = new ResponseStructure<>();
					
					structure.setStatusCode(HttpStatus.CREATED.value());
					structure.setMessage("Subject List added to the Program : "+program.getProgramName());
					structure.setData(mapToAcademicProgramResponse(program));
					
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
				})
				.orElseThrow(()-> new AcademicProgramNotExistsByIdException("Failed to ADD Subject List to this PROGRAM"));
		
//		return academicsRepo.findById(programId)
//				.map(program -> {
//					
//					List<String> list = mapToSubject(request);
//					
//					List<Subject> subjectList = new ArrayList<>();
//					
//					subjectList = checkSubject(list, subjectList);
//					
//					Subject sub = new Subject();
//					for(String subject: list) {
//						sub.setSubjectName(subject);
//						subjectList.add(subjectRepo.save(sub));
//					}
//					
//					program.setSubjectList(subjectList);
//					academicsRepo.save(program);
//					
//					structure.setStatusCode(HttpStatus.CREATED.value());
//					structure.setMessage("Subject List added to the Program : "+program.getProgramName());
//					structure.setData(mapToAcademicProgramResponse(program));
//					
//					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
//				})
//				.orElseThrow(()-> new AcademicProgramNotExistsByIdException("Failed to ADD Subject List to this PROGRAM"));
	}

	
	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubject() {
		List<Subject> list =  subjectRepo.findAll();
		if(list.isEmpty()) 
			throw new EmptyListException("Failed to FETCH Subject List");
		else {
			List<SubjectResponse> responseList = new ArrayList<>();
			list.forEach(subject -> responseList.add(mapToSubjectResponse(subject)));
			
			ResponseStructure<List<SubjectResponse>> structure = new ResponseStructure<>();
			
			structure.setStatusCode(HttpStatus.FOUND.value());
			structure.setMessage("Subject List Found");
			structure.setData(responseList);
			
			return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(structure, HttpStatus.FOUND);
		}
	}
}
