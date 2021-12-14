package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHdFrameNo;

/**
 * 
 * @author sonnlb
 * 
 *         時間休暇使用情報
 *
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
public class VacationTimeUseInfor {
	/**
	 * 時間休種類
	 */
	private AppTimeType timeType ;
	/**
	 * 時間年休使用時間
	 */
	private AttendanceTime nenkyuTime;
	/**
	 * 時間代休使用時間
	 */
	private AttendanceTime kyukaTime;
	/**
	 * 60H超休使用時間
	 */
	private AttendanceTime hChoukyuTime;
	/**
	 * 特別休暇使用時間
	 */
	private AttendanceTime specialHolidayUseTime;
	/**
	 * 子の看護休暇使用時間
	 */
	private AttendanceTime timeChildCareHolidayUseTime;
	/**
	 * 介護休暇使用時間
	 */
	private AttendanceTime timeCareHolidayUseTime;
	/**
	 * 特別休暇枠NO
	 */
	private Optional<SpecialHdFrameNo> spcVacationFrameNo;
	
	public static VacationTimeUseInfor fromLateDomain(LateTimeOfDaily domain, AppTimeType appTimeType) {
		
		TimevacationUseTimeOfDaily timeUse = domain.getTimePaidUseTime();
		
		return VacationTimeUseInfor.builder()
				//時間休暇使用情報．時間休暇種類 ← 時間休暇申請詳細．詳細．時間休暇種類
				.timeType(appTimeType)
				//時間休暇使用情報．時間年休使用時間 ← 時間休暇申請詳細．詳細．申請時間．時間年休使用時間
				.nenkyuTime(timeUse.getTimeAnnualLeaveUseTime())
				//時間休暇使用情報．時間代休使用時間 ← 時間休暇申請詳細．詳細．申請時間．時間代休使用時間
				.kyukaTime(timeUse.getTimeCompensatoryLeaveUseTime())
				//時間休暇使用情報．時間60H超休使用時間 ← 時間休暇申請詳細．詳細．申請時間．時間60H超休使用時間
				.hChoukyuTime(timeUse.getSixtyHourExcessHolidayUseTime())
				//時間休暇使用情報．特別休暇使用時間 ← 時間休暇申請詳細．詳細．申請時間．特別休暇使用時間
				.specialHolidayUseTime(timeUse.getTimeSpecialHolidayUseTime())
				//時間休暇使用情報．子の看護使用時間 ← 時間休暇申請詳細．詳細．申請時間．子の看護使用時間
				.timeChildCareHolidayUseTime(timeUse.getTimeChildCareHolidayUseTime())
				//時間休暇使用情報．介護使用時間 ← 時間休暇申請詳細．詳細．申請時間．介護使用時間
				.timeCareHolidayUseTime(timeUse.getTimeCareHolidayUseTime())
				//時間休暇使用情報．特別休暇枠NO ← 時間休暇申請詳細．詳細．申請時間．特別休暇枠NO
				.spcVacationFrameNo(timeUse.getSpecialHolidayFrameNo())
				.build();
	}

	public static VacationTimeUseInfor fromEarlyDomain(LeaveEarlyTimeOfDaily domain, AppTimeType appTimeType) {
		
		TimevacationUseTimeOfDaily timeUse = domain.getTimePaidUseTime();
		
		return VacationTimeUseInfor.builder()
				.timeType(appTimeType)
				.nenkyuTime(timeUse.getTimeAnnualLeaveUseTime())
				.kyukaTime(timeUse.getTimeCompensatoryLeaveUseTime())
				.hChoukyuTime(timeUse.getSixtyHourExcessHolidayUseTime())
				.specialHolidayUseTime(timeUse.getTimeSpecialHolidayUseTime())
				.timeChildCareHolidayUseTime(timeUse.getTimeChildCareHolidayUseTime())
				.timeCareHolidayUseTime(timeUse.getTimeCareHolidayUseTime())
				.spcVacationFrameNo(timeUse.getSpecialHolidayFrameNo())
				.build();
	}

	public static VacationTimeUseInfor fromOutDomain(OutingTimeOfDaily domain, AppTimeType appTimeType) {
		
		TimevacationUseTimeOfDaily timeUse = domain.getTimeVacationUseOfDaily();
		
		return VacationTimeUseInfor.builder()
				.timeType(appTimeType)
				.nenkyuTime(timeUse.getTimeAnnualLeaveUseTime())
				.kyukaTime(timeUse.getTimeCompensatoryLeaveUseTime())
				.hChoukyuTime(timeUse.getSixtyHourExcessHolidayUseTime())
				.specialHolidayUseTime(timeUse.getTimeSpecialHolidayUseTime())
				.timeChildCareHolidayUseTime(timeUse.getTimeChildCareHolidayUseTime())
				.timeCareHolidayUseTime(timeUse.getTimeCareHolidayUseTime())
				.spcVacationFrameNo(timeUse.getSpecialHolidayFrameNo())
				.build();
	}
}
