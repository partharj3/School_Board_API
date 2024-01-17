package com.school.sba.serviceimpl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.DataAlreadyExistsException;
import com.school.sba.exception.ScheduleNotExistsException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepo;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleServiceImpl implements ScheduleService{

	@Autowired
	private SchoolRepo schoolrepo;
	
	@Autowired
	private ScheduleRepository schedulerepo;
	
	@Autowired
	private ResponseStructure<ScheduleResponse> structure;
	
	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
		return ScheduleResponse.builder()
				   .scheduleId(schedule.getScheduleId())
			       .opensAt(schedule.getOpensAt())
			       .closesAt(schedule.getClosesAt())
			       .classHoursPerDay(schedule.getClassHoursPerDay())
			       .classHoursLengthInMinutes((int)schedule.getClassHoursLengthInMinutes().toMinutes())
			       .breakTime(schedule.getBreakTime())
			       .breakLengthInMinutes((int)schedule.getBreakLengthInMinutes().toMinutes())
	    		   .lunchTime(schedule.getLunchTime())
			       .lunchLengthInMinutes((int)schedule.getLunchLengthInMinutes().toMinutes())
			       .build();
	}

	private Schedule mapToSchedule(ScheduleRequest request) {
		return Schedule.builder()
				       .opensAt(request.getOpensAt())
				       .closesAt(request.getClosesAt())
				       .classHoursPerDay(request.getClassHoursPerDay())
				       .classHoursLengthInMinutes(Duration.ofMinutes(request.getClassHoursLengthInMinutes()))
				       .breakTime(request.getBreakTime())
				       .breakLengthInMinutes(Duration.ofMinutes(request.getBreakLengthInMinutes()))
		    		   .lunchTime(request.getLunchTime())
				       .lunchLengthInMinutes(Duration.ofMinutes(request.getLunchLengthInMinutes()))
				       .build();	   
	}
	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> addSchedule(ScheduleRequest request, int schoolId) {
		return schoolrepo.findById(schoolId)
				.map(schoolObj -> {
					if(schoolObj.getSchedule()==null) 
					{
						Schedule schedule = mapToSchedule(request);
						schedule.setSchool(schoolObj);
						schedulerepo.save(schedule);
						schoolObj.setSchedule(schedule);
						schoolrepo.save(schoolObj);
						
						structure.setStatusCode(HttpStatus.CREATED.value());
						structure.setMessage("Schedule Created for the School "+schoolObj.getSchoolName());
						structure.setData(mapToScheduleResponse(schedule));
					
						return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.CREATED);
					}
					else
						throw new DataAlreadyExistsException("Failed to CREATE Schedule a new Schedule");
				})
				.orElseThrow(()-> new SchoolNotFoundByIdException("Failed to CREATE Schedule"));
		
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) {
		return schoolrepo.findById(schoolId)
				.map(school ->{
					Schedule schedule = school.getSchedule(); 
					if(schedule != null) {
						structure.setStatusCode(HttpStatus.FOUND.value());
						structure.setMessage(school.getSchoolName()+" Schedule Found");
						structure.setData(mapToScheduleResponse(schedule));
						
						return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.FOUND);
					}
					else
						throw new ScheduleNotExistsException("Failed to FIND the Schedule of "+school.getSchoolName());
				})
				.orElseThrow(()-> new SchoolNotFoundByIdException("Failed to FIND the Schedule"));	
	}

	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId, ScheduleRequest request) {
		return schedulerepo.findById(scheduleId)
				.map(schedule ->{
					Schedule updated = mapToSchedule(request);
					updated.setScheduleId(schedule.getScheduleId());
					updated.setSchool(schedule.getSchool());
					updated = schedulerepo.save(updated);
					
					structure.setStatusCode(HttpStatus.OK.value());
					structure.setMessage(updated.getSchool().getSchoolName()+" Schedule got UPDATED Successfully !!");
					structure.setData(mapToScheduleResponse(updated));
					
					return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.OK);
				})
				.orElseThrow(()->new ScheduleNotExistsException("Failed to UPDATE the Schedule"));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
