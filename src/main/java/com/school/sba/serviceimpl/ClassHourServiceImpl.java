package com.school.sba.serviceimpl;

import java.time.DayOfWeek;
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

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.ScheduleNotExistsException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService{

	@Autowired
	private AcademicProgramRepository academicsRepo;
	
	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ClassHourRepository classHourRepo;	

	@Autowired
	private ResponseStructure<String> structure;
	
	private ClassHour newClassHour(ClassHour classhour) {
		return ClassHour.builder()
						.beginsAt(classhour.getBeginsAt().plusDays(7))
						.endsAt(classhour.getEndsAt().plusDays(7))
						.roomNo(classhour.getRoomNo())
						.status(ClassStatus.SCHEDULED)
						.program(classhour.getProgram())
						.user(classhour.getUser())
						.subject(classhour.getSubject())
						.build();
	}
	
	private ClassHour mapToClasshour(ClassHourRequest request) {
		User user = userRepo.findById(request.getUserId()).orElseThrow(null);
		Subject subject = subjectRepo.findById(request.getSubjectId()).orElseThrow(null);
		ClassHour ch = classHourRepo.findById(request.getClasshourId()).orElseThrow(null);

		return ClassHour.builder()
						.user(user)
						.program(ch.getProgram())
						.status(ClassStatus.valueOf(request.getStatus()))
						.subject(subject)
						.roomNo(request.getRoomNo())
						.build();
	}
	
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
		LocalTime lunchTime = schedule.getLunchTime();
		return (lunchTime.isAfter(start) && lunchTime.isBefore(end) || lunchTime.equals(start));
	}
	
	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHour(int programId) {
		return academicsRepo.findById(programId)
				.map(program ->{
					Schedule schedule = program.getAcademicSchool().getSchedule();
					
					if(schedule == null) { throw new ScheduleNotExistsException("Failed to GENERATE Class Hour"); }
										
					int periodTime = (int)schedule.getClassHoursLengthInMinutes().toMinutes();

					if(program.getClasshourList()==null || program.getClasshourList().isEmpty())
					{
						List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
						LocalDate date = program.getBeginsAt();
						
						DayOfWeek dayOfWeek = date.getDayOfWeek();
						int end=6;
									
						System.out.println("OPENING DAY: "+ dayOfWeek);
						
						if(!dayOfWeek.equals(DayOfWeek.MONDAY))
							end = end+(7-dayOfWeek.getValue());
						
						// for generating day
						for(int day=1; day<=end; day++) {  // From Monday to Saturday (6 Working Days)
							LocalTime currentTime = schedule.getOpensAt();
							LocalDateTime lasthour = null;
							
							if(date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
								date=date.plusDays(1);
							
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
								
								ClassHour savedObject = classHourRepo.save(classhour);
								perDayClasshour.add(savedObject);
								
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
	
	/***
	 * 
	 * It takes the ClassHourRequest, during the progress if it fails
	 * it will send the reason for rejection to the called method i.e, updateClasshourList()	 
	 *  
	 * @param request
	 * @param isValid
	 * @return String result of Operation
	 */
	public String updateClasshour(ClassHourRequest request, List<ClassHour> isValid) {
		return classHourRepo.findById(request.getClasshourId())
			.map(classhour -> {
			
				AcademicProgram program = classhour.getProgram();
				if(program.isDeleted()) throw new IllegalRequestException("Program Already Deleted");
				
				List<Subject> subjectList = program.getSubjectList();
				
				if(subjectList.isEmpty()) 
				   return  "ID:"+request.getClasshourId()+", UPDATION Failed ::: Program's Subject List is Empty";
				
				return subjectRepo.findById(request.getSubjectId())
				.map(subject ->{
					if(!subjectList.contains(subject)) return "ID:"+request.getClasshourId()+", UPDATION FAILED ::: Irrelevant Subject to this Program";
					else {
						return userRepo.findById(request.getUserId())
						.map(user ->{
							
							if(user.isDeleted()) throw new IllegalRequestException("User Already Deleted");
							
						if(user.getUserRole().equals(UserRole.TEACHER)) {
							
							if(user.getSubject().getSubjectName().equals(subject.getSubjectName()) 
							&& program.getUsers().contains(user)) {
								
								int roomNo = request.getRoomNo();
								boolean isPresent = 
									classHourRepo.existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo
											      (classhour.getBeginsAt(), classhour.getEndsAt(), roomNo);
								
								if(!isPresent) {
									classhour.setRoomNo(roomNo);
									classhour.setSubject(subject);
									classhour.setUser(user);
									classhour.setStatus(ClassStatus.valueOf(request.getStatus()));
									isValid.add(classHourRepo.save(classhour));
									return "CLASS HOUR "+classhour.getClasshourId()+" -> UPDATED with Room No.: "+roomNo+" Successfully !!!";
								}
								return "ID:"+request.getClasshourId()+", Classroom already engaged with another program";
							}
							return "ID:"+request.getClasshourId()+", UPDATION FAILED ::: Irrelevant TEACHER to this Program";
						}
						return "ID:"+request.getClasshourId()+", UPDATION FAILED ::: Only TEACHERS are ALLOWED";
						})
						.orElse("ID:"+request.getClasshourId()+", UPDATION Failed ::: Invalid User ID");
					}
				})
				.orElse("ID:"+request.getClasshourId()+", UPDATION FAILED ::: Invalid Subject ID");
			})
			.orElse("ID:"+request.getClasshourId()+", UPDATION FAILED ::: NO CLASS HOUR FOUND");
	}
	
	/** 
	 *  To update the ClassHour from the list of ClassHourRequest. it returns the list of String 
	 *  which tracks each request's result to give response to client.
	 *  
	 *  If the updation got failed, then it will return the reason.
	 *  Each request will be getting the result individually by method
	 *  **/
	
	@Override
	public ResponseEntity<ResponseStructure<List<String>>> updateClasshourList(List<ClassHourRequest> requestList){
		if(!requestList.isEmpty()) {
			ResponseStructure<List<String>> structure = new ResponseStructure<>();
			List<String> responseList = new ArrayList<>(); 
			List<ClassHour> isValid = new ArrayList<ClassHour>(); // ONLY VALID DATA
			
			for(ClassHourRequest request : requestList) {
				responseList.add(updateClasshour(request, isValid));
			}
			
			if(isValid.size() == requestList.size()) // ALL DATA VALIDATED
				structure.setMessage("All requests have been SUCCESSFULLY UPDATED. !!!!");
			else if(isValid.size() == 0)			 // NO VALID DATA
				structure.setMessage("NO requests have been UPDATED. Check the RECORDS");
			else  									 // PARTIAL DATA GOT VALIDATED
				structure.setMessage("Requests UPDATED Partially !!");
			
			structure.setStatusCode(HttpStatus.OK.value());
			structure.setData(responseList);
			
			return new ResponseEntity<ResponseStructure<List<String>>> (structure , HttpStatus.OK);
		}
		throw new IllegalRequestException("List is EMPTY !!");
	}
	
	public boolean isClassHoursDeleted(List<ClassHour> classhours) {
		for(ClassHour classhour : classhours) {
			classhour.setProgram(null);
			classHourRepo.delete(classHourRepo.save(classhour));
		}
		return true;
	}

	@Override
	public void autoGenerateWeeklyClassHours() {
		List<AcademicProgram> programsToAutoRepeat = academicsRepo.findByAutoRepeatScheduledTrue();	
	
		if(!programsToAutoRepeat.isEmpty()) {
			
			List<ClassHour> toBeSaved = new ArrayList<>();
			
			programsToAutoRepeat.forEach( program ->{
			int recordsNeeded = (program.getAcademicSchool().getSchedule().getClassHoursPerDay()) * 6;
			List<ClassHour> classhours = classHourRepo.findLastNRecordsByProgram(program, recordsNeeded);
			
				if(!classhours.isEmpty()) {
					for(int i=classhours.size()-1; i>=0 ; i--) {
						toBeSaved.add(newClassHour(classhours.get(i)));
					}
					classHourRepo.saveAll(toBeSaved);
				}
				else
					System.out.println("No RECORDS Found to Auto Repeat");
			});
			System.out.println("Schedule Successfully Auto Repeated for the Upcoming WEEK.");
		}else {
			System.out.println("Auto Repeat Schedule : OFF");
		}
	}
}
