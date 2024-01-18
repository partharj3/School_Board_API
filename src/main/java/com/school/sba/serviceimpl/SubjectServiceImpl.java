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
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService{

	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private AcademicProgramRepository academicsRepo;
	
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
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjectList(int programId,SubjectRequest request) {
		
		return academicsRepo.findById(programId)
				.map(program -> {
					List<Subject> subjects = new ArrayList<>();
					request.getSubjectNames().forEach(name -> {
						subjects.add(
								subjectRepo.findBySubjectName(name).map(subject -> {
							return subject;
						}).orElseGet(() -> {
							Subject subject = new Subject();
							subject.setSubjectName(name.toLowerCase());
							subjectRepo.save(subject);
							
							return subject;
						})
								);
						
					});
					
					program.setSubjectList(subjects);
					academicsRepo.save(program);
					
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
}
