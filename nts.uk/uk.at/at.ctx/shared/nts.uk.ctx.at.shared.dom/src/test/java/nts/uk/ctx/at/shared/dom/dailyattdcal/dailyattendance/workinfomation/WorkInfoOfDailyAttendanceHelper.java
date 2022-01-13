package nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.workinfomation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

public class WorkInfoOfDailyAttendanceHelper {
	
	public static WorkInfoOfDailyAttendance getWorkInfoOfDailyAttendanceDefault() {
		WorkInformation recordInfo1 = new WorkInformation("ty1", "ti1");
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = new WorkInfoOfDailyAttendance(recordInfo1, CalculationState.Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use,
				DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty());
		return workInfoOfDailyAttendance;
	}
	
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
	
	public static WorkInfoAndTimeZone createWorkInfoAndTimeZone(List<TimeZone> listTimeZone) {
		return WorkInfoAndTimeZone.create(new WorkType(), new WorkTimeSetting(), listTimeZone);
	}
}
