package nts.uk.ctx.at.aggregation.dom.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;

public class PremiumTimeHelperInAggregation {
	
	/**
	 * 日別勤怠の割増時間を作成する
	 * @param premiumTimeMap
	 * @return
	 */
	public static PremiumTimeOfDailyPerformance create(Map<Integer, AttendanceTime> premiumTimeMap) {
		
		return new PremiumTimeOfDailyPerformance( 
				createPremiumTime(premiumTimeMap),
				new AttendanceAmountDaily( 0 ),
				new AttendanceTime( 0 ));
	}
	
	private static List<PremiumTime> createPremiumTime(Map<Integer, AttendanceTime> premiumTimeMap) {
		
		List<PremiumTime> result = new ArrayList<>();
		premiumTimeMap.forEach(
				(number, value) -> result.add(
						createPremiumTime(number, value)));
		
		return result;
	}
	
	private static PremiumTime createPremiumTime(Integer premiumTimeNo, AttendanceTime premitumTime) {
		
		return new PremiumTime(premiumTimeNo, premitumTime, new AttendanceAmountDaily(0));
	}

}
