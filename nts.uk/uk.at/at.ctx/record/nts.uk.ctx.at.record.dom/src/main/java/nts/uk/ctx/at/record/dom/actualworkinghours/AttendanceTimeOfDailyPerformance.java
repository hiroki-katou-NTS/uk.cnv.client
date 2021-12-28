package nts.uk.ctx.at.record.dom.actualworkinghours;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.StayingTimeOfDaily;

/**
 * 
 * @author nampt
 * 日別実績の勤怠時間 - root
 */
@Getter
public class AttendanceTimeOfDailyPerformance extends AggregateRoot {

	//社員ID
	private String employeeId;
	
	//年月日
	private GeneralDate ymd;
	
	//勤務予定時間 - 日別実績の勤務予定時間
	private AttendanceTimeOfDailyAttendance time;
	
	public AttendanceTimeOfDailyPerformance(String employeeId, GeneralDate ymd,
			WorkScheduleTimeOfDaily workScheduleTimeOfDaily, ActualWorkingTimeOfDaily actualWorkingTimeOfDaily,
			StayingTimeOfDaily stayingTime, AttendanceTimeOfExistMinus unEmployedTime, AttendanceTimeOfExistMinus budgetTimeVariance) {
		super();
		this.employeeId = employeeId;
		this.ymd = ymd;
		this.time = new AttendanceTimeOfDailyAttendance(workScheduleTimeOfDaily, actualWorkingTimeOfDaily, 
				stayingTime, budgetTimeVariance, unEmployedTime);
	}
	
	public AttendanceTimeOfDailyPerformance (String employeeId,
											 GeneralDate ymd,
											 AttendanceTimeOfDailyAttendance time) {
		this.employeeId = employeeId;
		this.ymd = ymd;
		this.time = time;
	}
	
	public AttendanceTimeOfDailyPerformance inssertActualWorkingTimeOfDaily(ActualWorkingTimeOfDaily time) {
		return new AttendanceTimeOfDailyPerformance(this.employeeId, this.ymd, 
				new AttendanceTimeOfDailyAttendance (
						this.time.getWorkScheduleTimeOfDaily(), time, this.time.getStayingTime(), this.time.getBudgetTimeVariance(),
						this.time.getUnEmployedTime()) 
				); 
	}
	
	/**
	 * 時間・回数・乖離系(計算で求める全ての値)が全て０
	 */
	public static AttendanceTimeOfDailyPerformance allZeroValue(String empId, GeneralDate ymd) {
		return new AttendanceTimeOfDailyPerformance(empId, 
													ymd,
													AttendanceTimeOfDailyAttendance.createDefault()
													);
	}
}
