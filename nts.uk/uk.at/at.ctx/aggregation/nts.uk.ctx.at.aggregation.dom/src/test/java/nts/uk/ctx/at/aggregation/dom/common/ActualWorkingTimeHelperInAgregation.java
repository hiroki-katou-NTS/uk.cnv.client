package nts.uk.ctx.at.aggregation.dom.common;

import java.util.ArrayList;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;

public class ActualWorkingTimeHelperInAgregation {
	
	/**
	 * 総労働時間と割増時間を使って日別勤怠の勤務時間を作成する
	 * @param totalWorkingTime 総労働時間
	 * @param premiumTime 割増時間
	 * @return
	 */
	public static ActualWorkingTimeOfDaily create(
			TotalWorkingTime totalWorkingTime,
			PremiumTimeOfDailyPerformance premiumTime) {
		
		return new ActualWorkingTimeOfDaily(
				AttendanceTime.ZERO, 
				ConstraintTime.defaultValue(), 
				AttendanceTime.ZERO, 
				totalWorkingTime, 
				new DivergenceTimeOfDaily(new ArrayList<>()), 
				premiumTime);
	}

}
