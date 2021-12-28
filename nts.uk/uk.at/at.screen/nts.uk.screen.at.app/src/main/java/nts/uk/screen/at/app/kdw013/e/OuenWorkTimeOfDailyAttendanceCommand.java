package nts.uk.screen.at.app.kdw013.e;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OuenWorkTimeOfDailyAttendanceCommand {

	/** 応援勤務枠No: 応援勤務枠No */
	public Integer workNo;

	/** 勤務時間: 時間帯別勤怠の時間 */
	public OuenAttendanceTimeEachTimeSheetCommand workTime;

	public OuenWorkTimeOfDailyAttendance toDomain() {
		return OuenWorkTimeOfDailyAttendance.create(SupportFrameNo.of(this.workNo),
				this.workTime.toDomain(), null, null);
	}
}
