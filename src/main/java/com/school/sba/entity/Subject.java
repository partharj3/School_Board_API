package com.school.sba.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int subjectId;
	private String subjectName;

//	@OneToOne
//	private ClassHour classhour;
	
}
