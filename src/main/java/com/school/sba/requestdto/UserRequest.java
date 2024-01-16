package com.school.sba.requestdto;

import com.school.sba.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private long contactNo;
	private String email;
	private UserRole userRole;
	
}
