package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;

@AllArgsConstructor
@Getter
public class HolidayWorkTimeOfDailyCommand {
	//休出枠時間帯
	private List<HolidayWorkFrameTimeSheetCommand> holidayWorkFrameTimeSheet;
	//休出枠時間
	private List<HolidayWorkFrameTime> holidayWorkFrameTime;
	//休出深夜
	private Finally<HolidayMidnightWork> holidayMidNightWork;
	//休出拘束時間
	private AttendanceTime holidayTimeSpentAtWork;
}
