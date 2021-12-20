package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/*
 * 戻り値：月別実績の勤怠時間を集計
 */
@Getter
public class AggregateAttendanceTimeValue {

	/** 月別実績の勤怠時間 */
	@Setter
	private AttendanceTimeOfMonthly attendanceTime;
	/** 週別実績の勤怠時間 */
	private List<AttendanceTimeOfWeekly> attendanceTimeWeeks;
	
	public AggregateAttendanceTimeValue(String employeeId, YearMonth yearMonth, ClosureId closureId, 
			ClosureDate closureDate, DatePeriod datePeriod) {
		
		this.attendanceTime = new AttendanceTimeOfMonthly(employeeId, yearMonth, closureId, closureDate, datePeriod);;
		this.attendanceTimeWeeks = new ArrayList<>();
	}
	
	public void sum(AggregateAttendanceTimeValue target) {
		
		this.attendanceTime.sum(target.getAttendanceTime());
		this.attendanceTimeWeeks.addAll(target.getAttendanceTimeWeeks());
	}
}
