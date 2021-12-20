package nts.uk.screen.at.app.ksus01.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.NumberOfDaySuspensionDto;
import nts.uk.ctx.at.request.app.find.application.gobackdirectly.WorkInformationDto;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.ScheduleTimeSheetDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkInfoOfDailyAttendanceDto {

	//	勤務実績の勤務情報
	private WorkInformationDto recordInfo;
	//	計算状態
	private int calculationState;
	//	直行区分
	private int goStraightAtr;
	//	直帰区分
	private int backStraightAtr;
	//	曜日
	private int dayOfWeek;
	//	始業終業時間帯
	private List<ScheduleTimeSheetDto> scheduleTimeSheets;
	//	振休振出として扱う日数
	private NumberOfDaySuspensionDto numberDaySuspension;
	
	public static WorkInfoOfDailyAttendanceDto fromDomain(WorkInfoOfDailyAttendance domain) {
		if (domain == null) {
			return null;
		}
		return new WorkInfoOfDailyAttendanceDto(
				WorkInformationDto.fromDomain(domain.getRecordInfo()),
				domain.getCalculationState().value,
				domain.getGoStraightAtr().value,
				domain.getBackStraightAtr().value,
				domain.getDayOfWeek().value,
				domain.getScheduleTimeSheets().stream().map(el -> {
					return new ScheduleTimeSheetDto(el.getWorkNo().v(), el.getAttendance(), el.getLeaveWork());
				}).collect(Collectors.toList()),
				domain.getNumberDaySuspension().isPresent() ? NumberOfDaySuspensionDto.from(domain.getNumberDaySuspension().get()) : null
				);
	}
	
	public WorkInfoOfDailyAttendance toDomain() {
		return new WorkInfoOfDailyAttendance(
				this.recordInfo.toDomain(),
				EnumAdaptor.valueOf(this.calculationState, CalculationState.class),
				EnumAdaptor.valueOf(this.goStraightAtr, NotUseAttribute.class),
				EnumAdaptor.valueOf(this.backStraightAtr, NotUseAttribute.class),
				EnumAdaptor.valueOf(this.dayOfWeek, DayOfWeek.class),
				this.scheduleTimeSheets.stream().map(el -> {
					return new ScheduleTimeSheet(el.getWorkNo(), el.getAttendance().v(), el.getLeaveWork().v());
				}).collect(Collectors.toList()),
				Optional.ofNullable(this.numberDaySuspension == null ? null : this.numberDaySuspension.toDomain())
				);
	}
}
