package com.school.sba.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer>{
	
//	List<ClassHour> findByBeginsAt(LocalDateTime datetime);
	
	/*** 
	 * Method will check is there any class engaged 
	 * with the room no, class beginning time and the class ending time
	 **/
	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo( LocalDateTime beginsAt, LocalDateTime endsAt, int roomNo);
	
}
