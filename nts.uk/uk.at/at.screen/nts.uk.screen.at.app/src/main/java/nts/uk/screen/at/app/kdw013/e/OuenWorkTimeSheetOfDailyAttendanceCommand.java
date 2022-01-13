package nts.uk.screen.at.app.kdw013.e;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OuenWorkTimeSheetOfDailyAttendanceCommand {

	/** 応援勤務枠No: 応援勤務枠No */
	public Integer workNo;

	/** 作業内容: 作業内容 */
	public WorkContentCommand workContent;

	/** 時間帯: 時間帯別勤怠の時間帯 */
	public TimeSheetOfAttendanceEachOuenSheetCommand timeSheet;

	public OuenWorkTimeSheetOfDailyAttendance toDomain() {
		return OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(this.workNo), 
				this.workContent.toDomain(),
				this.timeSheet.toDomain(), Optional.empty());
	}
}
