package com.school.sba.requestdto;

import java.time.LocalDate;

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
public class ExcelRequest {

	private LocalDate fromDate;
	private LocalDate toDate;
	private String filePath;
	
}
