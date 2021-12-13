package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * ドメインサービス：控除時間計算
 * @author shuichi_ishida
 */
public class CalcDeductionTimeService {

	/**
	 * 控除合計時間の計算
	 * @param oneDay 1日の計算範囲
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @param sumRoundSet 合算丸め設定
	 * @return 控除合計時間
	 */
	public static DeductionTotalTime calcTotalTime(
			CalculationRangeOfOneDay oneDay,
			ConditionAtr conditionAtr,
			DeductionAtr dedAtr,
			TimeSheetRoundingAtr roundAtr,
			Optional<TimeRoundingSetting> sumRoundSet,
			NotUseAtr canOffset) {

		// 所定内合計時間の計算
		TimeWithCalculation withinDedTime = oneDay.getDeductionTime(
				conditionAtr, dedAtr, StatutoryAtr.Statutory, roundAtr, sumRoundSet, canOffset);
		// 所定外合計時間の計算
		TimeWithCalculation excessDedTime = oneDay.getDeductionTime(
				conditionAtr, dedAtr, StatutoryAtr.Excess, roundAtr, sumRoundSet, canOffset);
		// 控除区分を確認する
		if (dedAtr.isAppropriate()){
			// 勤務間休憩時間の計算
			AttendanceTime betweenBreakTime = oneDay.calcBetweenBreakTime();
			withinDedTime.addMinutesNotReturn(betweenBreakTime, betweenBreakTime);
		}
		// 控除合計時間を返す
		return DeductionTotalTime.of(
				withinDedTime.addMinutes(excessDedTime.getTime(), excessDedTime.getCalcTime()),
				withinDedTime, excessDedTime);
	}
}
