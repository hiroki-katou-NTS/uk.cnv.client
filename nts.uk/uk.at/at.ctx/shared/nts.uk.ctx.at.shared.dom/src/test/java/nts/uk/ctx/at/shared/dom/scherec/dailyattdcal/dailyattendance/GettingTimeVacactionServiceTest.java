package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.OutingTimeOfDailyHelper;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TimezoneToUseHourlyHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.GettingTimeVacactionService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimeVacation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class GettingTimeVacactionServiceTest {
	
	@Injectable 
	TimeLeavingOfDailyAttd mockedTimeLeaving;
	
	@Injectable 
	AttendanceTimeOfDailyAttendance mockedAttendanceTime;
	
	@Injectable
	OutingTimeOfDailyAttd mockedOutingTime;
	/**
	 * 勤怠時間 (attendanceTime) is empty
	 * 出退勤(timeLeaving) is mocked 
	 */
	@Test
	public void testGetTimeVacation_empty_case1() {
		
		// Arrange
		val optTimeLeaving = Optional.of(mockedTimeLeaving);// 出退勤 mocked
		val optAttendanceTime = Optional.empty();// 勤怠時間 empty
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> lateTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> earlyTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		
		// Assert
		assertThat(lateTimes).isEmpty();
		assertThat(earlyTimes).isEmpty();
		
	}
	
	/** 
	 * 勤怠時間(attendanceTime) is mocked
	 * 出退勤 (timeLeaving) is empty
	 * 外出時間帯(outingTime) empty
	 */
	@Test
	public void testGetTimeVacation_empty_case2() {
		
		// Arrange		
		val optTimeLeaving = Optional.empty();// 出退勤 empty 
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間 mocked
		val optOutingTime = Optional.empty();// 外出時間帯 empty
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> lateTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> earlyTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> outingTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, optOutingTime);

		// Assert
		assertThat(lateTimes).isEmpty();
		assertThat(earlyTimes).isEmpty();
		assertThat(outingTimes).isEmpty();
		
	}
	
	/**
	 * 勤怠時間 (attendanceTime) is empty
	 * 外出時間帯(outingTime) is mocked
	 */
	@Test
	public void testGetTimeVacation_empty_case3() {
		
		// Arrange		
		val optAttendanceTime = Optional.empty();// 勤怠時間 empty
		val optOutingTime = Optional.of(mockedOutingTime);// 外出時間帯mocked
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, optOutingTime);
		
		// Assert
		assertThat(result).isEmpty();
		
	}
	
	/**
	 * 勤怠時間 (attendanceTime) is empty
	 * 出退勤 (timeLeaving) is empty
	 * 外出時間帯(outingTime) empty
	 */
	@Test
	public void testGetTimeVacation_empty_case5() {
		
		// Arrange		
		val optTimeLeaving = Optional.empty();// 出退勤 empty 
		val optAttendanceTime = Optional.empty();// 勤怠時間 empty
		val optOutingTime = Optional.empty();// 外出時間帯 empty
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> lateTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> earlyTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> outtingTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, optOutingTime);
		
		// Assert
		assertThat(lateTimes).isEmpty();
		assertThat(earlyTimes).isEmpty();
		assertThat(outtingTimes).isEmpty();
		
	}
	
	/**
	 * 遅刻時間を取得する getLateTimeOfDaily == empty List
	 * 早退時間を取得	getLeaveEarlyTimeOfDaily == empty list
	 * 外出時間を取得する getOutingTimeOfDaily == emptyList
	 */
	@Test
	public void testGetTimeVacation_empty_case6() {
		
		// Arrange
		val optTimeLeaving = Optional.of(mockedTimeLeaving);// 出退勤mocked 
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間mocked
		val optOutingTime = Optional.of(mockedOutingTime);// 外出時間帯 mocked
		
		new Expectations() {{
			
			mockedAttendanceTime.getLateTimeOfDaily();
			// result = empty
			
			mockedAttendanceTime.getLeaveEarlyTimeOfDaily();
			// result = empty
			
			mockedAttendanceTime.getOutingTimeOfDaily();
			// result = empty
			
		}};
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> lateTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> earlyTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		Map<TimezoneToUseHourlyHoliday, TimeVacation> outingTimes = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, optOutingTime);
		
		// Assert
		assertThat(lateTimes).isEmpty();
		assertThat(earlyTimes).isEmpty();
		assertThat(outingTimes).isEmpty();
		
	}
	/**
	 * result has one item
	 */
	@Test
	public void testGetLateTimes_successfully_single(
			@Injectable LateTimeOfDaily lateTime1,
			@Injectable LateTimeOfDaily lateTime2,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime2) {
		
		new Expectations() {{
			
			mockedAttendanceTime.getLateTimeOfDaily();
			result = Arrays.asList( lateTime1, lateTime2 );
			
			lateTime1.getWorkNo();
			result = new WorkNo(1);
			lateTime2.getWorkNo();
			result = new WorkNo(2);
			
			mockedTimeLeaving.getStartTimeVacations(new WorkNo(1));
			// result = empty
			
			mockedTimeLeaving.getStartTimeVacations(new WorkNo(2));
			result = Optional.of(new TimeSpanForCalc(
									new TimeWithDayAttr(100),
									new TimeWithDayAttr(200)));
			
			lateTime2.getTimePaidUseTime();
			// result = timePaidUseTime2;
		}};
		
		val optTimeLeaving = Optional.of(mockedTimeLeaving);
		val optAttendanceTime = Optional.of(mockedAttendanceTime);
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		
		// Assert
		assertThat(result).hasSize(1);
		TimeVacation value = result.get(TimezoneToUseHourlyHoliday.WORK_NO2_BEFORE);
		assertThat(value.getUseTime()).isEqualTo(timePaidUseTime2);
		assertThat(value.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 100, 200 ));
		
	}
	
	/**
	 * result has two item
	 */
	@Test
	public void testGetLateTimes_successfully_multi(
			@Injectable LateTimeOfDaily lateTime1,
			@Injectable LateTimeOfDaily lateTime2,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime1,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime2) {
		
		new Expectations() {{
			
			mockedAttendanceTime.getLateTimeOfDaily();
			result = Arrays.asList( lateTime1, lateTime2 );
			
			lateTime1.getWorkNo();
			result = new WorkNo(1);
			lateTime2.getWorkNo();
			result = new WorkNo(2);
			
			mockedTimeLeaving.getStartTimeVacations(new WorkNo(1));
			result = Optional.of(new TimeSpanForCalc(
					new TimeWithDayAttr(100),
					new TimeWithDayAttr(200)));
			
			mockedTimeLeaving.getStartTimeVacations(new WorkNo(2));
			result = Optional.of(new TimeSpanForCalc(
									new TimeWithDayAttr(300),
									new TimeWithDayAttr(400)));

			lateTime1.getTimePaidUseTime();
			result = timePaidUseTime1;
			
			lateTime2.getTimePaidUseTime();
			result = timePaidUseTime2;
		}};
		
		val optTimeLeaving = Optional.of(mockedTimeLeaving);
		val optAttendanceTime = Optional.of(mockedAttendanceTime);
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getLateTimes", optTimeLeaving, optAttendanceTime);
		
		// Assert
		assertThat(result).hasSize(2);
		
			// value1
		TimeVacation value1 = result.get(TimezoneToUseHourlyHoliday.WORK_NO1_BEFORE);
		assertThat(value1.getUseTime()).isEqualTo(timePaidUseTime1);
		assertThat(value1.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 100, 200 ));
		
			// value2
		TimeVacation value2 = result.get(TimezoneToUseHourlyHoliday.WORK_NO2_BEFORE);
		assertThat(value2.getUseTime()).isEqualTo(timePaidUseTime2);
		assertThat(value2.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 300, 400 ));
		
	}
	
	@Test
	public void testGetEarlyTimes_successfully_single(
			@Injectable LeaveEarlyTimeOfDaily earlyTime1,
			@Injectable LeaveEarlyTimeOfDaily earlyTime2,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime2) {
		
		new Expectations() {{
			
			mockedAttendanceTime.getLeaveEarlyTimeOfDaily();
			result = Arrays.asList( earlyTime1, earlyTime2 );
			
			earlyTime1.getWorkNo();
			result = new WorkNo(1);
			earlyTime2.getWorkNo();
			result = new WorkNo(2);
			
			mockedTimeLeaving.getEndTimeVacations(new WorkNo(1));
			// result = empty
			mockedTimeLeaving.getEndTimeVacations(new WorkNo(2));
			result = Optional.of(new TimeSpanForCalc(
									new TimeWithDayAttr(100),
									new TimeWithDayAttr(200)));
			
			earlyTime2.getTimePaidUseTime();
			// result = timePaidUseTime2;
		}};
		
		val optTimeLeaving = Optional.of(mockedTimeLeaving);// 出退勤
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		
		// Assert
		assertThat(result).hasSize(1);
		TimeVacation value = result.get(TimezoneToUseHourlyHoliday.WORK_NO2_AFTER);
		assertThat(value.getUseTime()).isEqualTo(timePaidUseTime2);
		assertThat(value.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 100, 200 ));
		
	}
	
	@Test
	public void testGetEarlyTimes_successfully_multi(
			@Injectable LeaveEarlyTimeOfDaily earlyTime1,
			@Injectable LeaveEarlyTimeOfDaily earlyTime2,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime1,
			@Injectable TimevacationUseTimeOfDaily timePaidUseTime2) {
		
		new Expectations() {{
			
			mockedAttendanceTime.getLeaveEarlyTimeOfDaily();
			result = Arrays.asList( earlyTime1, earlyTime2 );
			
			earlyTime1.getWorkNo();
			result = new WorkNo(1);
			earlyTime2.getWorkNo();
			result = new WorkNo(2);
			
			mockedTimeLeaving.getEndTimeVacations(new WorkNo(1));
			result = Optional.of(new TimeSpanForCalc(
									new TimeWithDayAttr(100),
									new TimeWithDayAttr(200)));
			
			mockedTimeLeaving.getEndTimeVacations(new WorkNo(2));
			result = Optional.of(new TimeSpanForCalc(
									new TimeWithDayAttr(300),
									new TimeWithDayAttr(400)));
			
			earlyTime1.getTimePaidUseTime();
			result = timePaidUseTime1;
			
			earlyTime2.getTimePaidUseTime();
			result = timePaidUseTime2;
		}};
		
		val optTimeLeaving = Optional.of(mockedTimeLeaving);// 出退勤
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getEarlyTimes", optTimeLeaving, optAttendanceTime);
		
		// Assert
		assertThat(result).hasSize(2);
		TimeVacation value1 = result.get(TimezoneToUseHourlyHoliday.WORK_NO1_AFTER);
		assertThat(value1.getUseTime()).isEqualTo(timePaidUseTime1);
		assertThat(value1.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 100, 200 ));
		
		TimeVacation value2 = result.get(TimezoneToUseHourlyHoliday.WORK_NO2_AFTER);
		assertThat(value2.getUseTime()).isEqualTo(timePaidUseTime2);
		assertThat(value2.getTimeList())
			.extracting(
				e -> e.start(),
				e -> e.end())
			.containsExactly( tuple ( 300, 400 ));
		
	}
	
	/**
	 * 外出時間を取得する getOutingTimeOfDaily 
	 * 「私用, 組合」と合ってない
	 */
	@Test
	public void getOutingTimes_empty() {
		
		// Arrange
		val outingTimeOpt = Optional.of(mockedOutingTime);
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間
		new Expectations() {{
			
			mockedAttendanceTime.getOutingTimeOfDaily();
			result = Arrays.asList(
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithReason(GoingOutReason.COMPENSATION),
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithReason(GoingOutReason.PUBLIC)
					);
			
		}};
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, outingTimeOpt);
		
		// Assert
		assertThat(result).isEmpty();
		
	}
	
	@Test
	public void getOutingTimes_successfully(
			@Injectable TimevacationUseTimeOfDaily timevacationUseTimeOfDaily1,
			@Injectable TimevacationUseTimeOfDaily timevacationUseTimeOfDaily2,
			@Injectable TimevacationUseTimeOfDaily timevacationUseTimeOfDaily3,
			@Injectable TimevacationUseTimeOfDaily timevacationUseTimeOfDaily4
			) {
		
		// Arrange
		val outingTimeOpt = Optional.of(mockedOutingTime);// 外出時間帯
		val optAttendanceTime = Optional.of(mockedAttendanceTime);// 勤怠時間
		
		new Expectations() {{
			
			mockedAttendanceTime.getOutingTimeOfDaily();
			result = Arrays.asList(
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithParams(GoingOutReason.PRIVATE, timevacationUseTimeOfDaily1),
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithParams(GoingOutReason.PUBLIC, timevacationUseTimeOfDaily2),
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithParams(GoingOutReason.COMPENSATION, timevacationUseTimeOfDaily3),
					OutingTimeOfDailyHelper.createOutingTimeOfDailyWithParams(GoingOutReason.UNION, timevacationUseTimeOfDaily4));
			
			mockedOutingTime.getTimeZoneByGoOutReason((GoingOutReason) any);
			returns(
					Arrays.asList(
							new TimeSpanForCalc(new TimeWithDayAttr(100), new TimeWithDayAttr(200)),
							new TimeSpanForCalc(new TimeWithDayAttr(300), new TimeWithDayAttr(400))),
					Arrays.asList(new TimeSpanForCalc(new TimeWithDayAttr(500), new TimeWithDayAttr(600)))
					);
		}};
		
		// Action
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = NtsAssert.Invoke.staticMethod(GettingTimeVacactionService.class
				,	"getOutingTimes", optAttendanceTime, outingTimeOpt);
		
		// Assert
		assertThat(result).hasSize(2);
		TimeVacation value1 = result.get(TimezoneToUseHourlyHoliday.GOINGOUT_PRIVATE);
		TimeVacation value2 = result.get(TimezoneToUseHourlyHoliday.GOINGOUT_UNION);
		
		assertThat(value1.getUseTime()).isEqualTo(timevacationUseTimeOfDaily1);
		assertThat(value1.getTimeList())
			.extracting(
				d -> d.getStart().v(),
				d -> d.getEnd().v())
			.containsExactly(
					tuple(100, 200),
					tuple(300, 400));
		
		assertThat(value2.getUseTime()).isEqualTo(timevacationUseTimeOfDaily4);
		assertThat(value2.getTimeList())
			.extracting(
				d -> d.getStart().v(),
				d -> d.getEnd().v())
			.containsExactly(
				tuple(500, 600));
		
	}
}
