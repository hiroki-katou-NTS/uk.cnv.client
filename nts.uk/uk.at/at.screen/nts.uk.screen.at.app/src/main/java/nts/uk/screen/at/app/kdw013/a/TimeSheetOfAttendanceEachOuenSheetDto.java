package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkTimeInformationDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;

@AllArgsConstructor
@Getter
public class TimeSheetOfAttendanceEachOuenSheetDto {
	/** 勤務枠No: 勤務NO */
	private int workNo;

	/** 開始: 勤務時刻情報 */
	private WorkTimeInformationDto start;

	/** 終了: 勤務時刻情報 */
	private WorkTimeInformationDto end;

	public static TimeSheetOfAttendanceEachOuenSheetDto fromDomain(TimeSheetOfAttendanceEachOuenSheet domain) {
		return new TimeSheetOfAttendanceEachOuenSheetDto(
				domain.getWorkNo().v(),
				domain.getStart().map(start -> WorkTimeInformationDto.fromDomain(start)).orElse(null),
				domain.getEnd().map(end -> WorkTimeInformationDto.fromDomain(end)).orElse(null));
	}
}
