package com.school.sba.entity;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ClassHour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int classhourId;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	
//	@ManyToOne
//	private ClassRoom classroom;
//	
//	@OneToMany(mappedBy="classhour")
//	private List<User> user;
//	
//	@OneToOne
//	private Subject subject;
	
}
