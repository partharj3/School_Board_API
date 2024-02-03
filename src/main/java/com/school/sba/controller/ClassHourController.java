package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequest;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {

	@Autowired
	private ClassHourService classhourService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> generateClassHour(@PathVariable int programId){
		return classhourService.generateClassHour(programId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/class-hours")
	public ResponseEntity<ResponseStructure<List<String>>> updateClasshourList(@RequestBody List<ClassHourRequest> request){
		return classhourService.updateClasshourList(request);
	}
	
	@PostMapping("/academic-program/{programId}/class-hours/write-excel")
	public ResponseEntity<ResponseStructure<String>> createExcelSheet(@PathVariable int programId,@RequestBody ExcelRequest request) {
		return classhourService.createExcelSheet(programId,request);
	}
	
	@PostMapping("/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?> writeToExcel(@PathVariable int programId,@PathVariable LocalDate fromDate,
			                              @PathVariable LocalDate toDate, @RequestParam MultipartFile file) throws IOException{
		return classhourService.writeToExcel(programId,fromDate,toDate,file);
	}
}
