package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
/**
 * Unit Test: 日別勤怠の勤怠時間 
 * @author lan_lt
 *
 */
@RunWith(JMockit.class)	
public class AttendanceTimeOfDailyAttendanceTest {
	
	@Injectable
	ActualWorkingTimeOfDaily actualWorkingTimeDaily;
	
	@Test
	public void getters(@Injectable WorkScheduleTimeOfDaily schedule,
			@Injectable StayingTimeOfDaily stay,
			@Injectable AttendanceTimeOfExistMinus budget,
			@Injectable AttendanceTimeOfExistMinus unEmploy) {
		
		AttendanceTimeOfDailyAttendance target = 
				new AttendanceTimeOfDailyAttendance(schedule, actualWorkingTimeDaily, stay, budget, unEmploy);
		NtsAssert.invokeGetters(target);
	}
	
	/**
	 * 遅刻時間を取得する = empty
	 * 遅刻時間リスト = empty
	 */
	@Test
	public void getLateTime_empty() {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getLateTimeOfDaily();
			// result = empty;
			
		}};
		
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		assertThat(target.getLateTimeOfDaily()).isEmpty();
	}
	
	/**
	 * 遅刻時間を取得する not empty
	 */
	@Test
	public void getLateTime_not_empty( 
			@Injectable LateTimeOfDaily lateTime1,
			@Injectable LateTimeOfDaily lateTime2) {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getLateTimeOfDaily();
			result = Arrays.asList(lateTime1, lateTime2);
			
		}};
		
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		// Action
		List<LateTimeOfDaily> result = target.getLateTimeOfDaily();
		
		assertThat(result).extracting( d -> d)
							.containsExactly( lateTime1, lateTime2 );
	}
	
	/**
	 * 早退時間を取得する = empty
	 * 早退時間リスト = empty
	 */
	@Test
	public void getEarlyTime_empty() {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getLeaveEarlyTimeOfDaily();
			// result = empty;
			
		}};
		
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		assertThat(target.getLeaveEarlyTimeOfDaily()).isEmpty();
	}
	
	
	/**
	 * 早退時間を取得する not empty
	 */
	
	@Test
	public void getEarlyTime_not_empty(@Injectable LeaveEarlyTimeOfDaily leaveEarlyTime1,
			@Injectable LeaveEarlyTimeOfDaily leaveEarlyTime2) {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getLeaveEarlyTimeOfDaily();
			result = Arrays.asList(leaveEarlyTime1, leaveEarlyTime2);
			
		}};
				
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		// Action
		List<LeaveEarlyTimeOfDaily> result = target.getLeaveEarlyTimeOfDaily();
		
		assertThat(result).extracting( d -> d )
							.containsExactly( leaveEarlyTime1, leaveEarlyTime2);
	}
	
	/**
	 * 外出時間を取得する = empty
	 * 外出時間リスト = empty
	 */
	@Test
	public void getOutingTime_empty() {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getOutingTimeOfDailyPerformance();
			// result = empty;
			
		}};
		
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		assertThat(target.getOutingTimeOfDaily()).isEmpty();
	}
	
	/**
	 * 外出時間を取得する not empty
	 */
	
	@Test
	public void getOutingTime_not_empty(@Injectable OutingTimeOfDaily outingTime1,
			@Injectable OutingTimeOfDaily outingTime2) {
		
		new Expectations() {{
			
			actualWorkingTimeDaily.getTotalWorkingTime().getOutingTimeOfDailyPerformance();
			result = Arrays.asList(outingTime1, outingTime2);
			
		}};
		
		// 日別勤怠の勤怠時間 
		AttendanceTimeOfDailyAttendance target = Helper.createWithActualWorkingTimeOfDaily(actualWorkingTimeDaily);
		
		// Action
		List<OutingTimeOfDaily> result = target.getOutingTimeOfDaily();
		
		assertThat(result).extracting( d -> d)
							.containsExactly( outingTime1, outingTime2 );
	}	
	
	static class Helper {
		
		@Injectable 
		static WorkScheduleTimeOfDaily timeOfDaily;
		
		@Injectable
		static StayingTimeOfDaily stayingTimeOfDaily;
		
		@Injectable 
		static AttendanceTimeOfExistMinus budget;
		
		@Injectable 
		static AttendanceTimeOfExistMinus unEmploy;
		
		public static AttendanceTimeOfDailyAttendance createWithActualWorkingTimeOfDaily(
				ActualWorkingTimeOfDaily actualWorkingTimeOfDaily ) {
			
			return new AttendanceTimeOfDailyAttendance(timeOfDaily, actualWorkingTimeOfDaily, stayingTimeOfDaily, budget, unEmploy);
		}
	}

}
