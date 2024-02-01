package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHour(int programId);

	ResponseEntity<ResponseStructure<List<String>>> updateClasshourList(List<ClassHourRequest> request);
	
	void autoGenerateWeeklyClassHours();

}
