package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.school.sba.entity.AcademicProgram;

public interface AcademicProgramRepository extends JpaRepository<AcademicProgram, Integer>{
	
//	@Query("SELECT u FROM User u JOIN u.academicprograms ap WHERE u.userRole = :userRole AND ap.programId = :programId")
//    List<User> findUsersByUserRoleAndAcademicProgram_ProgramId(@Param("userRole") UserRole userRole, @Param("programId") int programId);
	
	List<AcademicProgram> findByIsDeletedTrue(); 
	
}
