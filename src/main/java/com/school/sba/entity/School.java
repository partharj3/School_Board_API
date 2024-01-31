package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schoolId;
	private String schoolName;
	private long schoolContact;
	private String schoolEmail;
	private String schoolAddress;
	
	private boolean isDeleted = false;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Schedule schedule;

	@OneToMany(mappedBy="userSchool", fetch = FetchType.EAGER)
	private List<User> users;
	
	@OneToMany(mappedBy = "academicSchool", fetch = FetchType.EAGER)
	private List<AcademicProgram> academicPrograms;
	
//	@OneToMany(mappedBy="school")
//	private List<ClassRoom> classroom;

}
