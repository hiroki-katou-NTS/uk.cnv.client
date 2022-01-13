package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.NumberOfDaySuspensionDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.WorkInformationDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkInfoOfDailyAttendanceDto {

	// 勤務実績の勤務情報
	private WorkInformationDto recordInfoDto;

	// 計算状態
	private int calculationState;

	// 直行区分
	private int goStraightAtr;

	// 直帰区分
	private int backStraightAtr;

	// 曜日
	private int dayOfWeek;

	// 始業終業時間帯
	private List<ScheduleTimeSheetDto> scheduleTimeSheets;

	// 振休振出として扱う日数
	private NumberOfDaySuspensionDto numberDaySuspension;

	public static WorkInfoOfDailyAttendanceDto toDto(WorkInfoOfDailyAttendance domain) {
		return new WorkInfoOfDailyAttendanceDto(WorkInformationDto.fromDomain(domain.getRecordInfo()),
				domain.getCalculationState().value, domain.getGoStraightAtr().value, domain.getBackStraightAtr().value,
				domain.getDayOfWeek().value,
				domain.getScheduleTimeSheets().stream().map(m -> ScheduleTimeSheetDto.toDto(m))
						.collect(Collectors.toList()),
				domain.getNumberDaySuspension().isPresent()
						? NumberOfDaySuspensionDto.from(domain.getNumberDaySuspension().get())
						: null);
	}

}