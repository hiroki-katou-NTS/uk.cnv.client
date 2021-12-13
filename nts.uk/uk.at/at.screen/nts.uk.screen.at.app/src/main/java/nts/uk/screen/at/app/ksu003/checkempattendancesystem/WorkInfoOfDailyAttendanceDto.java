package nts.uk.screen.at.app.ksu003.checkempattendancesystem;

import java.util.List;

import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.NumberOfDaySuspensionDto;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkInformationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.ScheduleTimeSheetDto;

@Value
@Getter
public class WorkInfoOfDailyAttendanceDto {
	// 勤務実績の勤務情報
	public WorkInformationDto recordInfo;
	// 計算状態
	public int calculationState;
	// 直行区分
	public int goStraightAtr;
	// 直帰区分
	public int backStraightAtr;
	// 曜日
	public int dayOfWeek;
	// 始業終業時間帯
	public List<ScheduleTimeSheetDto> scheduleTimeSheets;
	//振休振出として扱う日数
	public NumberOfDaySuspensionDto numberDaySuspension;
}
