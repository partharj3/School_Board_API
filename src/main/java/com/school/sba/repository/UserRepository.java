package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Integer>{
	List<User> findUserByUserRole(UserRole role);
	
	boolean existsByUserRole(UserRole role);
	
	Optional<User> findUserByUsername(String username);
	
	List<User> findByUserRoleAndAcademicprograms_ProgramId(UserRole userRole, int programId);
	
	List<User> findByIsDeletedTrue();
	
	List<User> findByUserRoleNotAndUserSchool(UserRole role, School school);
	
	List<User> findByUserRoleNotAndUserSchoolIn(UserRole userRole, List<School> schools);
}
