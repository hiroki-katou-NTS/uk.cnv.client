package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

@AllArgsConstructor
@Getter
public class OuenWorkTimeSheetOfDailyAttendanceDto {
	/** 応援勤務枠No: 応援勤務枠No supportNo */
	private Integer workNo;

	/** 作業内容: 作業内容 */
	private WorkContentDto workContent;

	/** 時間帯: 時間帯別勤怠の時間帯 */
	private TimeSheetOfAttendanceEachOuenSheetDto timeSheet;

	/** 作業時間入力フラグ */
	private boolean inputFlag;

	public static OuenWorkTimeSheetOfDailyAttendanceDto fromDomain(OuenWorkTimeSheetOfDailyAttendance domain) {

		return new OuenWorkTimeSheetOfDailyAttendanceDto(domain.getWorkNo().v(),
				WorkContentDto.fromDomain(domain.getWorkContent()),
				TimeSheetOfAttendanceEachOuenSheetDto.fromDomain(domain.getTimeSheet()),
				domain.getInputFlag().map(x -> x.booleanValue()).orElse(false));
	}

}