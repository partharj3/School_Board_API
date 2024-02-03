package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.ClassHourService;
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
	
	@Autowired
	private ClassHourService classhourService;
	
	@Scheduled(fixedDelay = 300000l) // 5 minute (5*60*1000)
	public void test() {
		userService.permanentlyDeleteUsers();
		academicService.permanentlyDeleteAcademicPrograms();
		schoolService.permanentlyDeleteSchool();
		System.out.println("Job Scheduled");
	}
	
	/**
	 *  0  : Second (0-59)
	 *	0  : Minute (0-59)
	 *	0  : Hour (0-23)
	 *	?  : Day of the month (no specific value)
	 *  *  : Month (any)
	 *	MON: Day of the week (Monday)
	 */
	
	@Scheduled(cron = "0 0 0 ? * MON")
	public void autoRepeatSchedule() {
	    classhourService.autoGenerateWeeklyClassHours();
	}
}
