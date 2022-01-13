package nts.uk.screen.at.app.kdw013.e;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenAttendanceTimeEachTimeSheet;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class OuenAttendanceTimeEachTimeSheetCommand {

	/** 総労働時間: 勤怠時間 */
	public Integer totalTime;

	public OuenAttendanceTimeEachTimeSheet toDomain() {
		return OuenAttendanceTimeEachTimeSheet.create(new AttendanceTime(this.totalTime), null, null, AttendanceAmountDaily.ZERO, new ArrayList<>(),
				PremiumTimeOfDailyPerformance.createEmpty());
	}
}
