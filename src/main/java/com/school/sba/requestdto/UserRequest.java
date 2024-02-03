package com.school.sba.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

	@Pattern(regexp="^[a-zA-Z][a-zA-Z0-9]*[0-9]$")
	@NotBlank(message = "Username should not be BLANK")
	@NotNull(message = "Username should not be NULL")
	private String username;
	
	@NotBlank(message = "Password is required")
	@NotNull(message = "Password is required")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must"
			+ " contain at least one upper case, one lower case, one number, one special character")
	private String password;
	
	@NotEmpty(message = "First name is Required")
	@Pattern(regexp="^[A-Z][a-z]*$")
	private String firstname;
	
	@Pattern(regexp="^[A-Z][a-z]*$")
	private String lastname;
	
	@Min(value = 6000000000l, message = "Phone Number should not start below '6' !!")
	@Max(value = 9999999999l, message = "Phone Number cannot be above 10 Digit !!")
	private long contactNo;
	
	@NotBlank(message = "Student Email field Should not be BLANK")
	@Email(regexp = "[a-z0-9+_.-]+@[g][m][a][i][l]+.[c][o][m]", 
	message = "invalid email--Should be in the extension of '@gmail.com' ")
	private String email;
	
	@NotBlank(message = "Please provide your Role")
	@NotNull(message = "Please provide your Role")
	@Pattern(regexp="^[A-Z]+$", message="Role should be in Upper Case")	
	private String userRole;
}
