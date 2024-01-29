package com.school.sba.requestdto;

import com.school.sba.enums.ClassStatus;

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
	private ClassStatus status;
}
