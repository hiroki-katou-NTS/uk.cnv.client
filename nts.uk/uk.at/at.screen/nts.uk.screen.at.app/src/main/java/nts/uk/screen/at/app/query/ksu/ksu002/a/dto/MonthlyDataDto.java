package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MonthlyDataDto {

	//・当月の労働時間
	public Double workingHoursMonth = 0.0;
	
	//・当月の出勤日数
	public Double numberWorkingDaysCurrentMonth = 0.0;
	
	//・当月の休日日数
	public Double numberHolidaysCurrentMonth = 0.0;
}
