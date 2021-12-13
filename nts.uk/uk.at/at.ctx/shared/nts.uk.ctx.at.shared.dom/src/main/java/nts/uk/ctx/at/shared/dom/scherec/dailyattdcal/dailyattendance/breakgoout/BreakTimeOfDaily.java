package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service.CalcDeductionTimeService;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.TimezoneOfFixedRestTimeSet;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 日別実績の休憩時間
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.休憩・外出.日別勤怠の休憩時間
 * @author keisuke_hoshina
 */
@Getter
public class BreakTimeOfDaily {
	/** 計上用合計時間: 控除合計時間 */
	private DeductionTotalTime toRecordTotalTime;
	/** 控除用合計時間: 控除合計時間 */
	private DeductionTotalTime deductionTotalTime;
	/** 休憩回数: 休憩外出回数 */
	private BreakTimeGoOutTimes gooutTimes;
	/** 勤務間時間: 勤怠時間 */
	private AttendanceTime workTime;
	/** 補正後時間帯: 休憩時間帯 */
	private List<BreakTimeSheet> breakTimeSheet;
	
	/**
	 * Constructor
	 * @param record 計上用
	 * @param deduction 控除用
	 */
	private BreakTimeOfDaily(DeductionTotalTime record,DeductionTotalTime deduction) {
		this.toRecordTotalTime = record;
		this.deductionTotalTime = deduction;
	}
	
	public BreakTimeOfDaily(DeductionTotalTime toRecordTotalTime, DeductionTotalTime deductionTotalTime,
			BreakTimeGoOutTimes gooutTimes, AttendanceTime workTime, List<BreakTimeSheet> breakTimeSheet) {
		super();
		this.toRecordTotalTime = toRecordTotalTime;
		this.deductionTotalTime = deductionTotalTime;
		this.gooutTimes = gooutTimes;
		this.workTime = workTime;
		this.breakTimeSheet = breakTimeSheet;
	}
	
	/**
	 * 控除、計上用両方を受け取った時間に入れ替える
	 * @return 日別実績の休憩時間
	 */
	public static BreakTimeOfDaily sameTotalTime(DeductionTotalTime deductionTime) {
		return new BreakTimeOfDaily(deductionTime,deductionTime);
	}
	
	/**
	 * 全ての休憩時間を算出する指示クラス
	 * アルゴリズム：日別実績の休憩時間
	 * @param oneDay 1日の計算範囲
	 * @param isCalculatable 計算可能かどうか
	 * @return 日別実績の休憩時間
	 */
	public static BreakTimeOfDaily calcTotalBreakTime(
			CalculationRangeOfOneDay oneDay,
			Boolean isCalculatable) {
		
		DeductionTotalTime recordCalcTime = DeductionTotalTime.defaultValue();
		DeductionTotalTime dedCalcTime =  DeductionTotalTime.defaultValue();
		BreakTimeGoOutTimes goOutTimes = new BreakTimeGoOutTimes(0);
		AttendanceTime duringTime = new AttendanceTime(0);
		List<BreakTimeSheet> breakTimeSheets = new ArrayList<>();
		if(isCalculatable) {
			//計上用計算時間
			recordCalcTime = calcTotalDeductBreakTime(oneDay, DeductionAtr.Appropriate);
			//控除用計算時間
			dedCalcTime = calcTotalDeductBreakTime(oneDay, DeductionAtr.Deduction);
			//休憩回数
			goOutTimes = new BreakTimeGoOutTimes(oneDay.calcBreakCount());
			//勤務間時間
			duringTime = oneDay.calcBetweenBreakTime();
			//補正後時間帯
			breakTimeSheets = new ArrayList<>();
		}
		return new BreakTimeOfDaily(recordCalcTime, dedCalcTime, goOutTimes, duringTime, breakTimeSheets);
	}

	/**
	 * 休憩合計時間の計算（控除時間）
	 * @param oneDay 1日の計算範囲
	 * @param dedAtr 控除区分
	 * @return 控除合計時間
	 */
	public static DeductionTotalTime calcTotalDeductBreakTime(
			CalculationRangeOfOneDay oneDay,
			DeductionAtr dedAtr) {
		
		return CalcDeductionTimeService.calcTotalTime(
				oneDay, ConditionAtr.BREAK, dedAtr, TimeSheetRoundingAtr.PerTimeSheet, Optional.empty(), NotUseAtr.NOT_USE);
	}
	
	/**
	 * 休憩未使用時間の計算
	 * @param fixRestTimezoneSet 固定勤務の休憩時間帯
	 * @param fixWoSetting 就業時間の時間帯設定（固定勤務）
	 * @param timeLeavingOfDailyPerformance 日別実績の出退勤 
	 * @return 休憩未使用時間
	 */
	public AttendanceTime calcUnUseBrekeTime(
			TimezoneOfFixedRestTimeSet fixRestTimezoneSet,
			List<EmTimeZoneSet> fixWoSetting,
			TimeLeavingOfDailyAttd timeLeavingOfDailyPerformance) {
		
		//実績の休憩時間を取得
		val recordTotalTime = this.getToRecordTotalTime().getWithinStatutoryTotalTime();
		//出退勤リスト取得
		val timeSpans = timeLeavingOfDailyPerformance.getTimeLeavingWorks().stream().map(tc -> tc.getTimespan()).collect(Collectors.toList());
		//就業時間帯の時間帯取得
		val workSpans = fixWoSetting.stream().map(tc -> tc.getTimezone().getTimeSpan()).collect(Collectors.toList());
		//出退勤、就業時間帯の時間帯、休憩時間帯から就業時間帯の休憩時間を算出する
		val totalBreakTime = fixRestTimezoneSet.calcTotalTimeDuplicatedAttLeave(timeSpans, workSpans);
		
		return totalBreakTime.minusMinutes(recordTotalTime.getCalcTime().valueAsMinutes());
	}
}
