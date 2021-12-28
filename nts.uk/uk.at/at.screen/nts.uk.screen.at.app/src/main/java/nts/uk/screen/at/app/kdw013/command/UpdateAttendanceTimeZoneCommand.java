package nts.uk.screen.at.app.kdw013.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAttendanceTimeZoneCommand {
	/** 社員ID */
	private String employeeId;

	/** 年月日 */
	private GeneralDate refDate;

	/** 応援時間帯 */
	private List<OuenWorkTimeSheetOfDailyAttendance> ouenSheet;

	/** 応援時間 */
	private List<OuenWorkTimeOfDailyAttendance> ouenTimes;
}
