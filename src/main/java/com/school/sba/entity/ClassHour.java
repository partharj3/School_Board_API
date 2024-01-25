package com.school.sba.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ClassHour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int classhourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	
	@ManyToOne
	private AcademicProgram program;
	
//	@ManyToOne
//	private ClassRoom classroom;
//	
//	@OneToMany(mappedBy="classhour")
//	private List<User> user;
//	
//	@OneToOne
//	private Subject subject;
	
}
