package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.SchoolService;
import com.school.sba.service.UserService;

@Component
public class ScheduledJobs {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AcademicProgramService academicService;
	
	@Autowired
	private SchoolService schoolService;
	
	@Scheduled(fixedDelay = 10000l) // 5 minute (5*60*1000)
	public void test() {
		userService.permanentlyDeleteUsers();
		academicService.permanentlyDeleteAcademicPrograms();
		schoolService.permanentlyDeleteSchool();
		System.out.println("Job Scheduled");
	}
	
}
