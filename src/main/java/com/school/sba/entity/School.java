package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class School {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schoolId;
	private String schoolName;
	private long schoolContact;
	private String schoolEmail;
	private String schoolAddress;
	
//	@OneToOne(mappedBy="school")
//	private Schedule schedule;
//	
//	@OneToMany(mappedBy="userSchool")
//	private List<User> user;
//	
//	@OneToMany(mappedBy="school")
//	private List<ClassRoom> classroom;

}
