package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.screen.at.app.kdw013.e.TimeSheetOfAttendanceEachOuenSheetCommand;

@AllArgsConstructor
@Getter
public class OuenWorkTimeSheetOfDailyAttendanceCommand {
	/** 応援勤務枠No: 応援勤務枠No supportNo */
	private Integer workNo;

	/** 作業内容: 作業内容 */
	private WorkContentCommand workContent;

	/** 時間帯: 時間帯別勤怠の時間帯 */
	private TimeSheetOfAttendanceEachOuenSheetCommand timeSheet;

	/** 作業時間入力フラグ */
	private Boolean inputFlag;

	public OuenWorkTimeSheetOfDailyAttendance toDomain() {

		return OuenWorkTimeSheetOfDailyAttendance.create(
				new SupportFrameNo(this.getWorkNo()),
				this.getWorkContent().toDomain(),
				this.getTimeSheet().toDomain(),
				Optional.ofNullable(this.getInputFlag())) ;
	}
}
