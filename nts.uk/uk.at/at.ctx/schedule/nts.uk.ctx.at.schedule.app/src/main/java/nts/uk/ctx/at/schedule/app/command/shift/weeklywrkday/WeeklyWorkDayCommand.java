package nts.uk.ctx.at.schedule.app.command.shift.weeklywrkday;

import java.util.List;

import lombok.Value;

@Value
public class WeeklyWorkDayCommand {
	
	//曜日勤務設定リスト
	private List<WorkdayPatternItemCommand> workdayPatternItemDtoList;
}
