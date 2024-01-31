package com.school.sba.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ClassHourRequest {
	
	private int classhourId;
	private int userId;
	private int subjectId;
	private int roomNo;
	
	@NotBlank(message = "Please provide your Role")
	@NotNull(message = "Please provide your Role")
	@Pattern(regexp="^[A-Z]+$", message="Role should be in Upper Case")	
	private String status;
}
