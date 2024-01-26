package com.school.sba.serviceimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.ScheduleNotExistsException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService{

	@Autowired
	private AcademicProgramRepository academicsRepo;
	
	@Autowired
	private ScheduleRepository scheduleRepo;
	
	@Autowired
	private ClassHourRepository classHourRepo;	

	@Autowired
	private ResponseStructure<String> structure;
	
	private ClassHourResponse mapToClassHourResponse(ClassHour classhour) {
		return ClassHourResponse.builder()
								.classhourid(classhour.getClasshourId())
								.beginsAt(classhour.getBeginsAt())
								.endsAt(classhour.getEndsAt())
								.roomNo(classhour.getRoomNo())
								.status(classhour.getStatus())
								.academics(classhour.getProgram().getProgramName())
								.build();
	}
	
	private LocalDateTime dateToDateTime(LocalDate date, LocalTime time){
		return LocalDateTime.of(date,time);
	}
	
	int timeDiff(LocalTime time1,LocalTime time2) {
		System.out.println((int)Duration.between(time1, time2).toMinutes());
		return (int)Duration.between(time1, time2).toMinutes();
	}
	
	private boolean isBreakTime(LocalTime start, LocalTime end, Schedule schedule) {
		LocalTime breakTime = schedule.getBreakTime();
		return (breakTime.isAfter(start) && breakTime.isBefore(end) || breakTime.equals(start));
	}
	
	private boolean isLunchTime(LocalTime start, LocalTime end, Schedule schedule) {
		LocalTime lunchTime = schedule.getLunchTime().plusHours(12);
		return (lunchTime.isAfter(start) && lunchTime.isBefore(end) || lunchTime.equals(start));
	}
	
	
	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHour(int programId,ClassHourRequest request) {
		return academicsRepo.findById(programId)
				.map(program ->{
					Schedule schedule = program.getAcademicSchool().getSchedule();
					
					if(schedule == null) { throw new ScheduleNotExistsException("Failed to GENERATE Class Hour"); }
										
					int periodTime = (int)schedule.getClassHoursLengthInMinutes().toMinutes();

					if(program.getClasshourList()==null || program.getClasshourList().isEmpty())
					{
						List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
						LocalDate date = program.getBeginsAt();
						
						// for generating day
						for(int day=1; day<=6; day++) {  // From Monday to Saturday (6 Working Days)
							LocalTime currentTime = schedule.getOpensAt();
							LocalDateTime lasthour = null;
							
							// for generating class hours per day
							for(int entry=1; entry<=schedule.getClassHoursPerDay(); entry++) { 
								
								ClassHour classhour = new ClassHour();
								if(currentTime.equals(schedule.getOpensAt())) { // first class hour of the day
									classhour.setBeginsAt(dateToDateTime(date,currentTime));
								}
								 else if(isBreakTime(currentTime, currentTime.plusMinutes(periodTime), schedule)) {  // after break time
							    	lasthour = lasthour.plus(schedule.getBreakLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								
								else if(isLunchTime(currentTime, currentTime.plusMinutes(periodTime), schedule)) {  // after lunch time
									lasthour = lasthour.plus(schedule.getLunchLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								else { // rest class hours of that day
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}

								classhour.setStatus(ClassStatus.NOT_SCHEDULED);
								classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));
								
								classhour.setProgram(program);
								
								/** if the class hour exceeding school closing time.
								 *  checking difference of expected ending time of the class hour 
								 *  and the closing time of the school. 
								 *  
								 *  If the time exceeds, the class hour endsAt school closing time.
								 * 
								 */
								
								if(classhour.getEndsAt().toLocalTime().compareTo(schedule.getClosesAt())==1 || 
										currentTime.equals(schedule.getClosesAt())) { // school closing time
									
									classhour.setEndsAt(dateToDateTime(date, schedule.getClosesAt()));
									perDayClasshour.add(classHourRepo.save(classhour));
									break;
								}
								
								perDayClasshour.add(classHourRepo.save(classhour));
								
								lasthour = perDayClasshour.get(entry-1).getEndsAt();
								currentTime = lasthour.toLocalTime();
							}
							date = date.plusDays(1);
					}
					program.setClasshourList(perDayClasshour);
					academicsRepo.save(program);
					
					structure.setStatusCode(HttpStatus.CREATED.value());
					structure.setMessage("Classhour GENERATED for Program: "+program.getProgramName());
					structure.setData("Completed Successfully");
					
					return new ResponseEntity<ResponseStructure<String>> (structure, HttpStatus.CREATED);
					}
					else
						throw new IllegalRequestException("Classhours Already Generated for :: "+program.getProgramName()+" of ID: "+program.getProgramId());
						
				})
				.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to GENERATE Class Hour"));
	}
}
