package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal;

import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.RefDesForAdditionalTakeLeave;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/** 特別休暇使用時間を計算 */
public class SpcVacationUseTimeCalc {

	/** 計算する */
	public static AttendanceTime calc(Require require, String cid, String sid, GeneralDate baseDate,
			WorkInformation workInfo, int spcNo, Optional<AttendanceTimeOfDailyAttendance> dailyAttendance) {
		
		/** $日単位の特別休暇使用時間 = [prv-1]日単位の使用時間を取得 */
		val dailyUse = getUseInfoOfDaily(require, cid, sid, baseDate, workInfo, spcNo);
		
		/** $時間単位の特別休暇使用時間 =  [prv-2]時間単位の使用時間を取得 */
		val timeUse = getUseInfoOfTime(spcNo, dailyAttendance);
		
		/** $日単位の特別休暇使用時間 + $時間単位の特別休暇使用時間 */
		return dailyUse.minusMinutes(timeUse.valueAsMinutes());
	}
	
	/** 日単位の使用時間を取得 */
	private static AttendanceTime getUseInfoOfDaily(Require require, String cid, String sid, GeneralDate baseDate,
			WorkInformation workInfo, int spcNo) {
		
		/** $勤務種類 = require.勤務種類を取得する(会社ID, 勤務情報.勤務種類コード) */
		val workType = require.workType(cid, workInfo.getWorkTypeCode().v()).orElse(null);
		if (workType == null) {
			return AttendanceTime.ZERO;
		}
		
		/** $特別休暇の1日午前午後区分 = $勤務種類.特別休暇の1日午前午後区分を取得(特別休暇枠NO) */
		val workAtr = workType.getWorkAtrForSpecialHoliday(spcNo).orElse(null);
		
		/** if $特別休暇の１日午前午後区分 not isPresent() */
		if (workAtr == null) {
			
			/** return 勤怠時間(0:00) */
			return new AttendanceTime(0);
		}
		
		/** $休暇加算時間設定 = require.休暇加算時間設定を取得する(会社ID) */
		val holAddSet = require.holidayAddtionSet(cid).orElse(null);
		if (holAddSet == null) {
			return AttendanceTime.ZERO;
		}
		
		/** $1日の内訳 = $休暇加算時間設定.参照先.休暇加算時間の取得 */
		val brdTimeDay = holAddSet.getReference().getVacationAddTime(require, cid, sid, 
				workInfo.getWorkTimeCodeNotNull(), baseDate);
		
		/** return: $１日の内訳.取得($特別休暇の１日午前午後区分) */
		return brdTimeDay.get(workAtr);
	}
	
	/** 時間単位の使用時間を取得 */
	private static AttendanceTime getUseInfoOfTime(int spcNo, Optional<AttendanceTimeOfDailyAttendance> dailyAttendance) {
		
		/** 勤怠時間.時間特別休暇の使用時間を取得(特別休暇枠NO) */
		return dailyAttendance.map(c -> c.getActualWorkingTimeOfDaily()
											.getTotalWorkingTime().getTotalTimeSpecialVacation(spcNo))
				.orElse(AttendanceTime.ZERO);
	}
	
	public static interface Require extends RefDesForAdditionalTakeLeave.Require {
		
		Optional<WorkType> workType(String cid, String workTypeCode);
		
		Optional<HolidayAddtionSet> holidayAddtionSet(String cid);
	}
}
