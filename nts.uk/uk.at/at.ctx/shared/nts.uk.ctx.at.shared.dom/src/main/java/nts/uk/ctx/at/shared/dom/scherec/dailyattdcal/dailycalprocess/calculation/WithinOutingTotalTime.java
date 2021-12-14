package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 所定内外出合計時間
 * @author keisuke_hoshina
 */
@Getter
public class WithinOutingTotalTime {
	//合計時間
	TimeWithCalculation totalTime;
	//コア内時間
	TimeWithCalculation withinCoreTime;
	//コア外時間
	TimeWithCalculation excessCoreTime;
	
	/**
	 * Constructor 
	 */
	private WithinOutingTotalTime(TimeWithCalculation totalTime, TimeWithCalculation withinCoreTime,
			TimeWithCalculation excessCoreTime) {
		super();
		this.totalTime = totalTime;
		this.withinCoreTime = withinCoreTime;
		this.excessCoreTime = excessCoreTime;
	}
	
	public static WithinOutingTotalTime of(TimeWithCalculation totalTime, TimeWithCalculation withinCoreTime,TimeWithCalculation excessCoreTime) {
		return new WithinOutingTotalTime(totalTime, withinCoreTime, excessCoreTime);
	}
	
	public static WithinOutingTotalTime sameTime(TimeWithCalculation time) {
		return new WithinOutingTotalTime(time, time, time);
	}
	
	/**
	 * コアタイム内外を分けて計算
	 * @param oneDay 1日の計算範囲
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @param reason 外出理由
	 * @return 外出合計時間
	 */
	public static WithinOutingTotalTime calcCoreTimeSeparate(
			CalculationRangeOfOneDay oneDay,
			DeductionAtr dedAtr,
			TimeSheetRoundingAtr roundAtr,
			GoingOutReason reason,
			NotUseAtr canOffset) {
		
		ConditionAtr conditionAtr = ConditionAtr.convertFromGoOutReason(reason);	// 控除種別区分
		
		// 所定内合計時間の計算
		TimeWithCalculation withinDedTime = oneDay.getDeductionTime(
				conditionAtr, dedAtr, StatutoryAtr.Statutory, roundAtr, Optional.empty(), canOffset);
		// コア内の外出時間の計算
		FlexWithinWorkTimeSheet changedFlexTimeSheet = (FlexWithinWorkTimeSheet)oneDay.getWithinWorkingTimeSheet().get();
		AttendanceTime withinFlex = changedFlexTimeSheet.calcOutingTimeInFlex(true, conditionAtr, dedAtr, roundAtr, canOffset);
		// コア外外出時間の計算
		AttendanceTime excessFlex = changedFlexTimeSheet.calcOutingTimeInFlex(false, conditionAtr, dedAtr, roundAtr, canOffset);
		// 外出合計時間を返す
		return WithinOutingTotalTime.of(
				withinDedTime,
				TimeWithCalculation.sameTime(withinFlex),
				TimeWithCalculation.sameTime(excessFlex));
	}
	
	/**
	 * コアタイム内外を分けずに計算
	 * @param oneDay 1日の計算範囲
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @param reason 外出理由
	 * @return 外出合計時間
	 */
	public static WithinOutingTotalTime calcCoreTimeNotSeparate(
			CalculationRangeOfOneDay oneDay,
			DeductionAtr dedAtr,
			TimeSheetRoundingAtr roundAtr,
			GoingOutReason reason,
			NotUseAtr canOffset) {
		
		// 所定内合計時間の計算
		TimeWithCalculation withinDedTime = oneDay.getDeductionTime(
				ConditionAtr.convertFromGoOutReason(reason),
				dedAtr, StatutoryAtr.Statutory, roundAtr, Optional.empty(), canOffset);
		// 外出合計時間を返す（コア外に全て入れる）
		return WithinOutingTotalTime.of(
				withinDedTime,
				TimeWithCalculation.sameTime(AttendanceTime.ZERO),
				withinDedTime);
	}
	
	public static WithinOutingTotalTime mergeTimeAndCalcTime(WithinOutingTotalTime time, WithinOutingTotalTime calcTime) {
		return new WithinOutingTotalTime(TimeWithCalculation.mergeTimeAndCalcTime(time.getTotalTime(), calcTime.getTotalTime()), 
				TimeWithCalculation.mergeTimeAndCalcTime(time.getWithinCoreTime(), calcTime.getWithinCoreTime()), 
				TimeWithCalculation.mergeTimeAndCalcTime(time.getExcessCoreTime(), calcTime.getExcessCoreTime()));
	}
}
