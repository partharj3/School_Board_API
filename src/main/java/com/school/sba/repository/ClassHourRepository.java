package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer>{
	
//	List<ClassHour> findByBeginsAt(LocalDateTime datetime);
	
	/*** 
	 * Method will check is there any class engaged 
	 * with the room no, class beginning time and the class ending time
	 **/
	boolean existsByBeginsAtIsLessThanEqualAndEndsAtIsGreaterThanEqualAndRoomNo( LocalDateTime beginsAt, LocalDateTime endsAt, int roomNo);
	
//	List<ClassHour> findTopNByProgramProgramIdOrderByEndsAtDesc(int programId, int n);
	
//	@Query(value = "SELECT * FROM class_hour ch WHERE ch.program_program_id = :programId ORDER BY ch.ends_at DESC LIMIT :n", nativeQuery = true)
//	List<ClassHour> findLastNClassHoursByProgramId(@Param("programId") int programId, @Param("n") int n);
	
	@Query("SELECT ch FROM ClassHour ch WHERE ch.program = :program " +
		       "ORDER BY ch.classhourId DESC " +
		       "LIMIT :lastNrecords")
		List<ClassHour> findLastNRecordsByProgram(AcademicProgram program,int lastNrecords);
	
}
