package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.service.SubjectService;

@RestController
public class SubjectController {

	@Autowired
	private SubjectService subjectservice;
	
}
