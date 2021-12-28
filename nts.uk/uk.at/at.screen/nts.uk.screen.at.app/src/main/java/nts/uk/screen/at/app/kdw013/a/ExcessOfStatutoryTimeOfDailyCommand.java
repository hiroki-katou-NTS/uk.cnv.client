package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;

@AllArgsConstructor
@Getter
public class ExcessOfStatutoryTimeOfDailyCommand {
	// 所定外深夜時間
	@Setter
	private ExcessOfStatutoryMidNightTimeCommand excessOfStatutoryMidNightTime;
	// 残業時間
	private OverTimeOfDailyCommand overTimeWork;
	// 休出時間
	private HolidayWorkTimeOfDailyCommand workHolidayTime;
}
