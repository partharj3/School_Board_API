package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.school.sba.entity.AcademicProgram;
import com.school.sba.enums.UserRole;

public interface AcademicProgramRepository extends JpaRepository<AcademicProgram, Integer>{
}
