package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TimezoneToUseHourlyHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.時間休暇を取得する
 * @author lan_lt
 *
 */
public class GettingTimeVacactionService {
	/**
	 * 取得する
	 * @param optTimeLeaving 出退勤
	 * @param optAttendanceTime 勤怠時間
	 * @param outingTime 外出時間帯
	 * @return
	 */
	public static Map<TimezoneToUseHourlyHoliday, TimeVacation> get(Optional<TimeLeavingOfDailyAttd> optTimeLeaving
			,	Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime
			,	Optional<OutingTimeOfDailyAttd> outingTime){
		
		/** 日別勤怠の遅刻時間を取得 */
		val lateTimeMap = getLateTimes(optTimeLeaving, optAttendanceTime);
		
		/** 日別勤怠の早退時間を取得*/
		val earlyTimeMap = getEarlyTimes(optTimeLeaving, optAttendanceTime);
		
		/** 日別勤怠の外出時間を取得 **/
		val outingTimeMap = getOutingTimes(optAttendanceTime, outingTime);
		
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = new HashMap<>();
		result.putAll( lateTimeMap );
		result.putAll( earlyTimeMap );
		result.putAll( outingTimeMap );
		
		return result;	
	}
	
	/**
	 * ［時間休暇を取得する］の［日別勤怠の遅刻時間を取得］
	 * @return
	 */
	private static Map<TimezoneToUseHourlyHoliday, TimeVacation> getLateTimes(Optional<TimeLeavingOfDailyAttd> optTimeLeaving
			,	Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime) {
		
		// 勤怠時間 or 出退勤 empty
		if ( !optAttendanceTime.isPresent() || !optTimeLeaving.isPresent() ) {
			return Collections.emptyMap();
		}
		
		// 遅刻時間を取得する
		val lateTimes = optAttendanceTime.get().getLateTimeOfDaily();

		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = new HashMap<>();
		
		for (LateTimeOfDaily lateTime : lateTimes) {
			
			// 出勤の勤務NOを指定
			val lateType = TimezoneToUseHourlyHoliday.getBeforeWorking(lateTime.getWorkNo());

			// @出退勤.勤務開始の休暇時間帯を取得する
			Optional<TimeSpanForCalc> leavingTimeSpan = optTimeLeaving.get().getStartTimeVacations(lateTime.getWorkNo());
			if ( !leavingTimeSpan.isPresent() ) {
				continue;
			}
			
			//時間休暇
			val timeVacation = new TimeVacation( new ArrayList<>( Arrays.asList(leavingTimeSpan.get()) ), lateTime.getTimePaidUseTime() );

			result.put(lateType, timeVacation);
		}
		
		return result;
	}

	/**
	 * ［時間休暇を取得する］の［日別勤怠の早退時間を取得］
	 * @return
	 */
	private static Map<TimezoneToUseHourlyHoliday, TimeVacation> getEarlyTimes(Optional<TimeLeavingOfDailyAttd> optTimeLeaving
			,	Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime) {
		
		if ( !optAttendanceTime.isPresent() || !optTimeLeaving.isPresent() ) {
			return Collections.emptyMap();
		}
		
		/** 日別勤怠の早退時間を取得*/
		List<LeaveEarlyTimeOfDaily> earlyTimes = optAttendanceTime.get().getLeaveEarlyTimeOfDaily();
		
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = new HashMap<>();
		
		for(LeaveEarlyTimeOfDaily earlyTime : earlyTimes) {
			
			// 退勤の勤務NOを指定
			val earlyType = TimezoneToUseHourlyHoliday.getAfterWorking(earlyTime.getWorkNo());
			
			// @出退勤.勤務終了の休暇時間帯を取得する ($.勤務NO)
			Optional<TimeSpanForCalc> leavingTimeSpan = optTimeLeaving.get().getEndTimeVacations(earlyTime.getWorkNo());
			if ( !leavingTimeSpan.isPresent() ) {
				continue;
			}
			
			//時間休暇
			val timeVacation = new TimeVacation( new ArrayList<>( Arrays.asList(leavingTimeSpan.get())), earlyTime.getTimePaidUseTime() );
			
			result.put(earlyType, timeVacation);
		}
		
		return result;
		
	}
	
	/**
	 * ［時間休暇を取得する］の［【日別勤怠の外出時間を取得］
	 * @return
	 */
	private static Map<TimezoneToUseHourlyHoliday, TimeVacation> getOutingTimes(Optional<AttendanceTimeOfDailyAttendance> optAttendanceTime
			,	Optional<OutingTimeOfDailyAttd> outingTimeOpt) {
		
		if ( !optAttendanceTime.isPresent() || !outingTimeOpt.isPresent() ) {
			return Collections.emptyMap();
		}
		
		//$外出時間 = @勤怠時間.外出時間を取得する () : filter $.外出理由 in (私用, 組合)
		List<OutingTimeOfDaily> outingTimes = optAttendanceTime.get().getOutingTimeOfDaily().stream()
				.filter(c -> c.getReason() == GoingOutReason.PRIVATE || c.getReason() == GoingOutReason.UNION)
				.collect(Collectors.toList());
		
		Map<TimezoneToUseHourlyHoliday, TimeVacation> result = new HashMap<>();
		
		for(OutingTimeOfDaily outingTime : outingTimes) {
			
			val type = TimezoneToUseHourlyHoliday.getDuringWorking(outingTime.getReason());
			
			// 外出理由を指定して時間帯を取得する
			val timeZones = outingTimeOpt.get().getTimeZoneByGoOutReason(outingTime.getReason());
			val timeLeave = new TimeVacation( timeZones, outingTime.getTimeVacationUseOfDaily() );
			
			result.put(type, timeLeave);
		}
		
		return result;
	}
}
