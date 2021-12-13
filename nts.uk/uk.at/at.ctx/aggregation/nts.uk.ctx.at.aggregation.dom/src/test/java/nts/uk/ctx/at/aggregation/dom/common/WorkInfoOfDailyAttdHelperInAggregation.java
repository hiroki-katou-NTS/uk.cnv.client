package nts.uk.ctx.at.aggregation.dom.common;

import java.util.ArrayList;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;

public class WorkInfoOfDailyAttdHelperInAggregation {

	/**
	 * 勤務情報を指定して日別勤怠の勤務情報を作る
	 * @param recordInfo 勤務情報
	 * @return 日別勤怠の勤務情報
	 */
	public static WorkInfoOfDailyAttendance createByWorkInformation(WorkInformation recordInfo) {
		
		return new WorkInfoOfDailyAttendance(
				recordInfo, 
				CalculationState.Calculated, 
				NotUseAttribute.Not_use, 
				NotUseAttribute.Not_use,
				DayOfWeek.FRIDAY, 
				new ArrayList<>(), 
				Optional.empty());
	}
	
}
