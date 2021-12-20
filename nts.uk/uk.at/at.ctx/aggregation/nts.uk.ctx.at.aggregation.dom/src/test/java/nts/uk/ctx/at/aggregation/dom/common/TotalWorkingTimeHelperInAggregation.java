package nts.uk.ctx.at.aggregation.dom.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.temporarytime.TemporaryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AbsenceOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AnnualOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.OverSalaryOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SpecialHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SubstituteHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TimeDigestOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TransferHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.YearlyReservedOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.IntervalExemptionTime;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

public class TotalWorkingTimeHelperInAggregation {
	
	/**
	 * 労働時間と実働時間と就業時間を使って総労働時間を作る
	 * @param totalTime 総労働時間
	 * @param actualTime 実働時間
	 * @param workTime 就業時間
	 * @return
	 */
	public static TotalWorkingTime create(
			AttendanceTime totalTime,
			AttendanceTime actualTime,
			AttendanceTime workTime) {
		
		return new TotalWorkingTime(
				totalTime,
				new AttendanceTime(0),
				actualTime,
				createWithinStatutoryTimeOfDaily(workTime),
				createExcessOfStatutoryTimeOfDaily(),
				createLateTime(),
				createEarlyTime(),
				createBreakTime(),
				Collections.emptyList(),
				crateRaiseSalary(),
				new WorkTimes(0),
				createTemporaryTime(),
				createShortTime(),
				createHolidayTime(),
				IntervalTimeOfDaily.empty());
	}
	
	private static WithinStatutoryTimeOfDaily createWithinStatutoryTimeOfDaily(AttendanceTime workTime) {
		
		return WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
				workTime, 
				new AttendanceTime(0), 
				new AttendanceTime(0), 
				new WithinStatutoryMidNightTime(
						TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))));
	}
	
	private static ExcessOfStatutoryTimeOfDaily createExcessOfStatutoryTimeOfDaily() {
		
		List<HolidayWorkFrameTime> lstHolidayWorkFrameTime = new ArrayList<>();
		for(int i = 1; i<=10; i++) {
			
			TimeDivergenceWithCalculation holidayWorkTime = TimeDivergenceWithCalculation.createTimeWithCalculation(
					new AttendanceTime(0), new AttendanceTime(0));		
			
			HolidayWorkFrameTime frameTime = new HolidayWorkFrameTime(
					new HolidayWorkFrameNo(i),
					Finally.of(holidayWorkTime), 
					Finally.of(holidayWorkTime), 
					Finally.of(new AttendanceTime(0)));
			
			lstHolidayWorkFrameTime.add(frameTime);
		}
		
		return new ExcessOfStatutoryTimeOfDaily(
				new ExcessOfStatutoryMidNightTime(
						TimeDivergenceWithCalculation.sameTime(
								new AttendanceTime(0)),
								new AttendanceTime(0)),
				Optional.of(new OverTimeOfDaily(
						Collections.emptyList(), 
						Collections.emptyList(),
						Finally.of(new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation
								.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0)))),
						new AttendanceTime(0),
						new FlexTime(
								TimeDivergenceWithCalculationMinusExist.createTimeWithCalculation(
										new AttendanceTimeOfExistMinus(0),
										new AttendanceTimeOfExistMinus(0)),
								new AttendanceTime(0)),
						new AttendanceTime(0))),
				Optional.of(new HolidayWorkTimeOfDaily(
						Collections.emptyList(), 
						lstHolidayWorkFrameTime,
						Finally.of(new HolidayMidnightWork(Collections.emptyList())), 
						new AttendanceTime(0))));
	}
	
	private static List<LateTimeOfDaily> createLateTime() {

		return Arrays.asList(
				new LateTimeOfDaily(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)), 
						new WorkNo(0),
						TimevacationUseTimeOfDaily.defaultValue(), 
						IntervalExemptionTime.defaultValue()));
	}
	
	private static List<LeaveEarlyTimeOfDaily> createEarlyTime() {

		return Arrays.asList(
				new LeaveEarlyTimeOfDaily(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)), 
						new WorkNo(0),
						TimevacationUseTimeOfDaily.defaultValue(), 
						IntervalExemptionTime.defaultValue()));
	}
	
	private static BreakTimeOfDaily createBreakTime() {
		
		return new BreakTimeOfDaily(
				DeductionTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				DeductionTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				new BreakTimeGoOutTimes(0), 
				new AttendanceTime(0), 
				Collections.emptyList());
	}
	
	private static RaiseSalaryTimeOfDailyPerfor crateRaiseSalary() {
		
		return new RaiseSalaryTimeOfDailyPerfor(new ArrayList<>(),new ArrayList<>());
	}
	
	private static TemporaryTimeOfDaily createTemporaryTime() {
		
		return new TemporaryTimeOfDaily(Collections.emptyList());
	}
	
	private static ShortWorkTimeOfDaily createShortTime() {
		
		return new ShortWorkTimeOfDaily(
				new WorkTimes(0),
				DeductionTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				DeductionTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				ChildCareAtr.CARE);
	}
	
	private static HolidayOfDaily createHolidayTime() {
		
		return new HolidayOfDaily(
				new AbsenceOfDaily(new AttendanceTime(0)),
				new TimeDigestOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new YearlyReservedOfDaily(new AttendanceTime(0)),
				new SubstituteHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new OverSalaryOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new SpecialHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new AnnualOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new TransferHolidayOfDaily(new AttendanceTime(0)));
	}
	
}
