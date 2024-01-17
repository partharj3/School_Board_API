package com.school.sba.entity;

import com.school.sba.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	@Column(unique = true) // It will not allow the repetitive usernames
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private long contactNo;
	
	@Column(unique = true)
	private String email;
	
    @Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	private Boolean isDeleted;
	
//	@ManyToOne
//	private School userSchool;
//	
//	@ManyToOne
//	private ClassHour classhour;
	
}
