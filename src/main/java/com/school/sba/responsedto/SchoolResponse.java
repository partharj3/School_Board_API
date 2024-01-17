package com.school.sba.responsedto;

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
public class SchoolResponse {
	private int schoolId;
	private String schoolName;
	private long schoolContact;
	private String schoolEmail;
	private String schoolAddress;
}
