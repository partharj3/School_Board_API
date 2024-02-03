package com.school.sba.serviceimpl;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.DataAlreadyExistsException;
import com.school.sba.exception.IllegalRequestException;
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
				.map(school -> {
					if(school.getSchedule()==null) 
					{						
						LocalTime startTime = request.getOpensAt(); 
						LocalTime endTime  =request.getClosesAt();
						LocalTime breakAt = request.getBreakTime();
						LocalTime lunchAt = request.getLunchTime();
						int classDuration = request.getClassHoursLengthInMinutes();
						int breakDuration = request.getBreakLengthInMinutes();
						int lunchDuration = request.getLunchLengthInMinutes();
						
						if(startTime.plusMinutes(classDuration).isBefore(breakAt) && 
						     breakAt.plusMinutes(breakDuration+classDuration).isBefore(lunchAt) && 
						     lunchAt.plusMinutes(lunchDuration+classDuration).isBefore(endTime)) {
								
								Schedule schedule=null;
								
								int totalTime = (int)(Duration.between(startTime, endTime).toMinutes());
								int actualTime = (request.getClassHoursPerDay()*classDuration + lunchDuration+breakDuration);
								if(totalTime == actualTime) {
									String message = "";
										
										message = returnMessage(startTime,breakAt,classDuration);

										if(message.equals(null)) {
											startTime = breakAt.plusMinutes(breakDuration);
											message=returnMessage(startTime,lunchAt,classDuration);
											
											if(message.equals(null)) {
												
											schedule = mapToSchedule(request);
											schedule.setSchool(school);
											schedulerepo.save(schedule);
											school.setSchedule(schedule);
											schoolrepo.save(school);
											
											structure.setMessage(message+school.getSchoolName());
											structure.setStatusCode(HttpStatus.CREATED.value());
											structure.setData(mapToScheduleResponse(schedule));
										
											return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.CREATED);
											}
											throw new IllegalRequestException(message);
										}
										throw new IllegalRequestException(message);
									}	
									throw new IllegalRequestException("Actual Time Not Ending with Closing Time");
								}
								throw new IllegalRequestException("Mention times Expected to be CLOSER or NON-SEQUENTIAL.");	
						}
						throw new DataAlreadyExistsException("Failed to CREATE a new Schedule");
				})
				.orElseThrow(()-> new SchoolNotFoundByIdException("Failed to CREATE Schedule"));
		
	}
	
	private String returnMessage(LocalTime start, LocalTime end, int duration) {
		
		int balanceTime=(int)(Duration.between(start,end).toMinutes()) % duration;
		if(balanceTime!=0) {
			return "Classhour timing exceeding "+end+". SUGESSTION: "+end.minusMinutes(balanceTime)
					+" OR "+(end.plusMinutes(duration-balanceTime)+" is EXPECTED");
		}
		return null;
	}
	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) {
		return schoolrepo.findById(schoolId)
				.map(school ->{
					
					if(!school.isDeleted()) throw new IllegalRequestException("School is ALREADY Deleted");
					
					Schedule schedule = school.getSchedule(); 
					if(schedule != null && !schedule.isDeleted()) {
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
					
					if(schedule.isDeleted()) throw new IllegalRequestException("Schedule is Already DELETED");
					
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

	public boolean isScheduleDeleted(Schedule schedule) {
		schedule.setSchool(null);
		schedulerepo.delete(schedule);
		return true;
	}
}
