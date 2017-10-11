package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.workscheduletimezone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.enumcommon.DayAttr;
import nts.uk.shr.com.time.AttendanceClock;

/**
 * 勤務予定時間帯
 * 
 * @author sonnh1
 *
 */
@AllArgsConstructor
@Getter
public class WorkScheduleTimeZone {
	private String sId;
	private GeneralDate ymd;
	private int scheduleCnt;
	private BounceAtr bounceAtr;
	private AttendanceClock scheduleStartClock;
	private DayAttr scheduleStartDayAtr;
	private AttendanceClock scheduleEndClock;
	private DayAttr scheduleEndDayAtr;
}
