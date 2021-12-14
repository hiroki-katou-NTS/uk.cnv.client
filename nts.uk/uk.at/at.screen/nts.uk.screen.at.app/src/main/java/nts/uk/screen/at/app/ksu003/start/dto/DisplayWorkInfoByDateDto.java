package nts.uk.screen.at.app.ksu003.start.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DisplayWorkInfoByDateDto {
	
		private String empId;
	
		/** 社員勤務情報　dto */
		private EmployeeWorkInfoDto workInfoDto;
		
		/** 社員勤務予定　dto */
		private EmployeeWorkScheduleDto workScheduleDto;
		
		/** 勤務固定情報　dto */
		private FixedWorkInforDto fixedWorkInforDto;
}
