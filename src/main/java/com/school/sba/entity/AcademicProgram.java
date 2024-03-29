package com.school.sba.entity;

import java.time.LocalDate;
import java.util.List;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class AcademicProgram {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int programId;
	
	@Enumerated(EnumType.STRING)
	private ProgramType programType;
	
	@Column(unique= true)
	private String programName;
	
	private LocalDate beginsAt;
	private LocalDate endsAt;
	
	private boolean isDeleted =  false;
	
	@ManyToOne
	private School academicSchool;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<User> users;
	
	@ManyToMany
	private List<Subject> subjectList;
	
	@OneToMany(mappedBy="program", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ClassHour> classhourList;
	
	private boolean autoRepeatScheduled = false;
}
