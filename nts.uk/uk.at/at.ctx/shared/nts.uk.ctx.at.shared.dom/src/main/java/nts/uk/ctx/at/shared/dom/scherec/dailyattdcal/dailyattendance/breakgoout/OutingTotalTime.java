package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout;

import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WithinOutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service.CalcDeductionTimeService;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.flexset.OutingCalcWithinCoreTime;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 外出合計時間
 * @author keisuke_hoshina
 */
@Getter
public class OutingTotalTime {
	//合計時間
	TimeWithCalculation totalTime;
	//所定内合計時間
	WithinOutingTotalTime withinTotalTime;
	//所定外合計時間
	TimeWithCalculation excessTotalTime;
	
	/**
	 * Constructor 
	 */
	private OutingTotalTime(TimeWithCalculation totalTime, WithinOutingTotalTime withinTotalTime,
			TimeWithCalculation excessTotalTime) {
		super();
		this.totalTime = totalTime;
		this.withinTotalTime = withinTotalTime;
		this.excessTotalTime = excessTotalTime;
	}
	
	public static OutingTotalTime of(TimeWithCalculation totalTime, WithinOutingTotalTime withinTotalTime,TimeWithCalculation excessTotalTime) {
		return new OutingTotalTime(totalTime,withinTotalTime,excessTotalTime);
	}
	
	/**
	 * 外出時間の計算
	 * @param oneDay 1日の計算範囲
	 * @param dedAtr 控除区分
	 * @param reason 外出理由
	 * @param outingCalcSet コアタイム内の外出計算
	 * @return 外出合計時間
	 */
	public static OutingTotalTime calcOutingTime(
			CalculationRangeOfOneDay oneDay,
			DeductionAtr dedAtr,
			GoingOutReason reason,
			Optional<OutingCalcWithinCoreTime> outingCalcSet, NotUseAtr canOffset) {
		
		// 外出合計時間の計算
		DeductionTotalTime outingTotal = CalcDeductionTimeService.calcTotalTime(oneDay,
				ConditionAtr.convertFromGoOutReason(reason),
				dedAtr, TimeSheetRoundingAtr.PerTimeSheet, Optional.empty(), canOffset);
		
		OutingTotalTime result = OutingTotalTime.of(
				outingTotal.getTotalTime(),
				WithinOutingTotalTime.of(
						outingTotal.getWithinStatutoryTotalTime(),
						TimeWithCalculation.sameTime(AttendanceTime.ZERO),
						TimeWithCalculation.sameTime(AttendanceTime.ZERO)),
				outingTotal.getExcessOfStatutoryTotalTime());

		boolean isSeparate = false;
		
		// コアタイム内の外出計算を取得
		if(outingCalcSet.isPresent()) {
			// コア内と外を分けて計算するかどうか判断
			if (outingCalcSet.get().isSeparateCoreInOutCalc(reason)) isSeparate = true;
		}
		if (isSeparate){
			// 所定内外出をコア内と外で分けて計算
			result.withinTotalTime = WithinOutingTotalTime.calcCoreTimeSeparate(
					oneDay, dedAtr, TimeSheetRoundingAtr.PerTimeSheet, reason, canOffset);
		}
		else{
			// 所定内外出をコア内と外で分けずに計算
			result.withinTotalTime = WithinOutingTotalTime.calcCoreTimeNotSeparate(
					oneDay, dedAtr, TimeSheetRoundingAtr.PerTimeSheet, reason, canOffset);
		}
		// 外出合計時間を返す
		return result;
	}
}
