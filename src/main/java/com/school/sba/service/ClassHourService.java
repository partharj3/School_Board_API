package com.school.sba.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequest;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHour(int programId);

	ResponseEntity<ResponseStructure<List<String>>> updateClasshourList(List<ClassHourRequest> request);
	
	void autoGenerateWeeklyClassHours();

	ResponseEntity<ResponseStructure<String>> createExcelSheet(int programId, ExcelRequest request);

	ResponseEntity<?> writeToExcel(int programId, LocalDate fromDate, LocalDate toDate, MultipartFile file) throws IOException;

}
