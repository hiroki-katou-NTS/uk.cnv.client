package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.app.command.application.overtime.WorkInformationCommand;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;

@AllArgsConstructor
@Getter
public class WorkInfoOfDailyAttendanceCommand {
	// 勤務実績の勤務情報
	private WorkInformationCommand recordInfoDto;

	// 計算状態
	private Integer calculationState;

	// 直行区分
	private Integer goStraightAtr;

	// 直帰区分
	private Integer backStraightAtr;

	// 曜日
	private Integer dayOfWeek;

	// 始業終業時間帯
	private List<ScheduleTimeSheetCommand> scheduleTimeSheets;

	// 振休振出として扱う日数
	private NumberOfDaySuspensionCommand numberDaySuspension;
	
	private long ver;

	public WorkInfoOfDailyAttendance toDomain() {
		
		WorkInfoOfDailyAttendance result =	new WorkInfoOfDailyAttendance(this.recordInfoDto.toDomain(),
				EnumAdaptor.valueOf(this.calculationState, CalculationState.class),
				EnumAdaptor.valueOf(this.goStraightAtr, NotUseAttribute.class),
				EnumAdaptor.valueOf(this.backStraightAtr, NotUseAttribute.class),
				EnumAdaptor.valueOf(this.dayOfWeek, DayOfWeek.class), 
				this.scheduleTimeSheets.stream().map(st -> ScheduleTimeSheetCommand.toDomain(st)).collect(Collectors.toList()),
				Optional.ofNullable(this.numberDaySuspension == null ? null : this.numberDaySuspension.toDomain()));
		
		result.setVer(this.getVer());

		return result;
	}
}
