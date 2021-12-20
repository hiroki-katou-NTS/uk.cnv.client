package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PersonalSchedulesDataDto extends MonthlyDataDto{

	//・OrderedList<労働時間, 休日日数>
	@Setter
	@Getter
	public List<WeeklyDataDto> weeklyData;

	public PersonalSchedulesDataDto(MonthlyDataDto monthlyData, List<WeeklyDataDto> weeklyData) {
		super(monthlyData.workingHoursMonth, monthlyData.numberWorkingDaysCurrentMonth, monthlyData.numberHolidaysCurrentMonth);
		this.weeklyData = weeklyData;
	}
}
