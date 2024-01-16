package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClassRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomId;
	private int roomNo;
	private int roomLength;
	
//	@ManyToOne
//	private School school;
//	
//	@OneToMany(mappedBy="classroom")
//	private List<ClassHour> classhour;	
}
