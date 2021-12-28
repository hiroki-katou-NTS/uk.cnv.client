package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

@AllArgsConstructor
@Getter
public class ScheduleTimeSheetCommand {
	private Integer workNo;

	private Integer attendance;

	private Integer leaveWork;

	public static ScheduleTimeSheet toDomain(ScheduleTimeSheetCommand st) {

		return new ScheduleTimeSheet(new WorkNo(st.getWorkNo()),
				new TimeWithDayAttr(st.getAttendance()),
				new TimeWithDayAttr(st.getLeaveWork()));
	}
}
