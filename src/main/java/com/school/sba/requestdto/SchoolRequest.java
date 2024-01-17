package com.school.sba.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
public class SchoolRequest {
	
	@NotEmpty(message="School name should not be Empty")
	@Pattern(regexp = "^([A-Z][a-zA-Z]*)(?:\\s[A-Z][a-zA-Z]*){0,99}$", message="First Character should be upper & limited upto length 100")
	private String schoolName;
	
	@Min(value = 6000000000l, message = "Phone Number should not start below '6' !!")
	@Max(value = 9999999999l, message = "Phone Number cannot be above 10 Digit !!")
	private long schoolContact;
	
	@NotBlank(message = "Email field Should not be BLANK")
	@Email(regexp = "[a-z0-9+_.-]+@[g][m][a][i][l]+.[c][o][m]", 
	message = "invalid email--Should be in the extension of '@gmail.com' ")
	private String schoolEmail;
	
	@NotEmpty(message = "Address is Required")
	@Pattern(regexp="^[A-Z][a-z]*$")
	private String schoolAddress;
}
