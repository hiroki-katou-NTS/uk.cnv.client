package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.secondorder.medical.MedicalCareTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;

public class AttendanceTimeOfDailyAttendanceHelper {
	
	/**
	 * 「日別勤怠の勤務時間」を使って「日別勤怠の勤怠時間」を作成する
	 * @param actualWorkingTimeOfDaily 勤務時間
	 * @return 日別勤怠の勤怠時間
	 */
	public static AttendanceTimeOfDailyAttendance createWithActualWorkingTimeOfDaily(
			ActualWorkingTimeOfDaily actualWorkingTimeOfDaily ) {
		
		return new AttendanceTimeOfDailyAttendance(
				WorkScheduleTimeOfDaily.defaultValue(), 
				actualWorkingTimeOfDaily, 
				StayingTimeOfDaily.defaultValue(), 
				AttendanceTimeOfExistMinus.ZERO, 
				AttendanceTimeOfExistMinus.ZERO,
				MedicalCareTimeOfDaily.defaultValue());
	}

}
