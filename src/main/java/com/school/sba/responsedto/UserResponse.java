package com.school.sba.responsedto;

import com.school.sba.enums.UserRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

	private int userId;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private UserRole userRole;
	
}
