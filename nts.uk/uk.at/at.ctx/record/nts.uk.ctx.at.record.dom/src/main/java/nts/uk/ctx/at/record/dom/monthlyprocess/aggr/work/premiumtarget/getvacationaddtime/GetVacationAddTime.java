package nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.premiumtarget.getvacationaddtime;

import lombok.val;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.vacationusetime.VacationUseTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 休暇加算時間を取得する
 * @author shuichu_ishida
 */
public class GetVacationAddTime {

	/**
	 * 休暇加算時間を取得する
	 * @param datePeriod 期間
	 * @param vacationUseTime 休暇使用時間
	 * @param addSet 加算設定
	 * @return 休暇加算時間
	 */
	public static AttendanceTimeMonth getTime(DatePeriod datePeriod,
			VacationUseTimeOfMonthly vacationUseTime, AddSet addSet){
		
		AttendanceTimeMonth vacationAddTime = new AttendanceTimeMonth(0);
		
		if (addSet.isAnnualLeave()){
			
			// 年休使用時間を取得する
			//*****（未）　仮に0設定。日次側ドメインの作成待ち。
			val annualLeaveUseTime = new AttendanceTimeMonth(0);
			
			// 休暇加算時間に年休使用時間を加算する
			vacationAddTime = vacationAddTime.addMinutes(annualLeaveUseTime.v());
		}
		
		if (addSet.isRetentionYearly()){
			
			// 積立年休使用時間を取得する
			//*****（未）　仮に0設定。日次側ドメインの作成待ち。
			val retentionYearlyUseTime = new AttendanceTimeMonth(0);
			
			// 休暇使用時間に積立年休使用時間を加算する
			vacationAddTime = vacationAddTime.addMinutes(retentionYearlyUseTime.v());
		}
		
		if (addSet.isSpecialHoliday()){
			
			// 特別休暇使用時間を取得する
			//*****（未）　仮に0設定。日次側ドメインの作成待ち。
			val specialHolidayUseTime = new AttendanceTimeMonth(0);
			
			// 休暇使用時間に特別休暇使用時間を加算する
			vacationAddTime = vacationAddTime.addMinutes(specialHolidayUseTime.v());
		}

		return vacationAddTime;
	}
}
