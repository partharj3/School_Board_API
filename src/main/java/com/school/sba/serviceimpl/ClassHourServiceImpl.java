package com.school.sba.serviceimpl;

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
	
//	private ClassHourResponse mapToClassHourResponse(ClassHour classhour) {
//		return ClassHourResponse.builder()
//								.classhourid(classhour.getClasshourId())
//								.beginsAt(classhour.getBeginsAt())
//								.endsAt(classhour.getEndsAt())
//								.roomNo(classhour.getRoomNo())
//								.status(classhour.getStatus())
//								.academics(classhour.getProgram().getProgramName())
//								.build();
//	}
	
	private LocalDateTime dateToDateTime(LocalDate date, LocalTime time){
		return LocalDateTime.of(date,time);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHour(int programId,ClassHourRequest request) {
		return academicsRepo.findById(programId)
				.map(program ->{
					Schedule schedule = program.getAcademicSchool().getSchedule();
					
					if(schedule == null) { throw new ScheduleNotExistsException("Failed to GENERATE Class Hour"); }
					
					if(program.getClasshourList()==null || program.getClasshourList().isEmpty())
					{
						List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
						
						LocalDateTime lasthour = null;
						LocalDate date = program.getBeginsAt();
						
						// for generating day
						for(int day=1; day<=6; day++) { 
							 
							// for generating class hours per day
							for(int nthClasshourOfTheDay=1; nthClasshourOfTheDay<=schedule.getClassHoursPerDay(); nthClasshourOfTheDay++) { 
								ClassHour classhour = new ClassHour();
								
								if(nthClasshourOfTheDay==1) { // first class hour of the day
									classhour.setBeginsAt(dateToDateTime(date,schedule.getOpensAt()));
								}
							    else if(nthClasshourOfTheDay==3) {  // after break time
							    	lasthour = lasthour.plus(schedule.getBreakLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								else if(nthClasshourOfTheDay==5) {  // after lunch time
									lasthour = lasthour.plus(schedule.getLunchLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								else { // rest class hours of that day
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));
								classhour.setStatus(ClassStatus.NOTSCHEDULED);
								classhour.setProgram(program);

								perDayClasshour.add(classHourRepo.save(classhour));
								
								lasthour = perDayClasshour.get(nthClasshourOfTheDay-1).getEndsAt();
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
						
						
				
		/** OLDER
		 * 
					    if(program.getClasshourList().size() < schedule.getClassHoursPerDay()*6) {
							List<ClassHour> perDayClassHour = (program.getClasshourList()!=null) 
									                           ? program.getClasshourList()  
									                           : new ArrayList<ClassHour>();
									
						ClassHour classhour = new ClassHour();
						int nthClasshourOfTheDay = perDayClassHour.size()%schedule.getClassHoursPerDay();
						
						if(perDayClassHour.isEmpty() || nthClasshourOfTheDay == 0) {
							classhour.setBeginsAt(LocalDateTime.of(program.getBeginsAt()
									                              ,schedule.getOpensAt()));
														
						}else {
							ClassHour lasthour = perDayClassHour.get(perDayClassHour.size()-1);
							LocalDateTime temp=null;
							if(nthClasshourOfTheDay==2) { // Class hour after Break time
								temp = lasthour.getEndsAt().plus(schedule.getBreakLengthInMinutes());
								classhour.setBeginsAt(temp);
							}
							else if(nthClasshourOfTheDay==4) { // Class hour after Lunch time
								temp = lasthour.getEndsAt().plus(schedule.getLunchLengthInMinutes());
								classhour.setBeginsAt(temp);
							}
							else {
								classhour.setBeginsAt(lasthour.getEndsAt());
							}
						}
						
						classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHoursLengthInMinutes()));	
		
						
//						classhour.setRoomNo(request.getRoomNo());
						classhour.setStatus(ClassStatus.NOTSCHEDULED);
						classhour.setProgram(program);
						classHourRepo.save(classhour);
						
						program.getClasshourList().add(classhour);
						academicsRepo.save(program);
						
						structure.setStatusCode(HttpStatus.CREATED.value());
						structure.setMessage("Classhour "+program.getClasshourList().size()+" GENERATED Successfully");
						structure.setData(mapToClassHourResponse(classhour));
						
						return new ResponseEntity<ResponseStructure<ClassHourResponse>>(structure, HttpStatus.CREATED);
					}
					else
						throw new IllegalRequestException("Classhour Generation running out of End :: "+program.getProgramName());
				})
				.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to GENERATE Class Hour"));
	}
	 **/
}
