package nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance;

import java.util.Collections;

import mockit.Injectable;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTotalTime;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

public class OutingTimeOfDailyHelper {
	
	@Injectable
	private static BreakTimeGoOutTimes workTime;
	
	@Injectable
	private static TimevacationUseTimeOfDaily timeVacationUseOfDaily;
	
	@Injectable
	private static OutingTotalTime recordTotalTime;
	
	@Injectable
	private static OutingTotalTime deductionTotalTime;
	
	public static OutingTimeOfDaily createOutingTimeOfDailyWithReason(GoingOutReason reason) {
		return new OutingTimeOfDaily(
				workTime,
				reason,
				timeVacationUseOfDaily,
				recordTotalTime,
				deductionTotalTime,
				Collections.emptyList());
		
	}
	
	public static OutingTimeOfDaily createOutingTimeOfDailyWithParams(GoingOutReason reason, TimevacationUseTimeOfDaily timeVacationUseOfDaily) {
		return new OutingTimeOfDaily(
				workTime,
				reason,
				timeVacationUseOfDaily,
				recordTotalTime,
				deductionTotalTime,
				Collections.emptyList());
		
	}

}
