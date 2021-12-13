package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WithinOutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeFrame;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flexset.OutingCalcWithinCoreTime;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.shr.com.enumcommon.NotUseAtr;

//import nts.uk.ctx.at.record.dom.worktime.primitivevalue.WorkTimes;

/**
 * 日別実績の外出時間
 * @author keisuke_hoshina
 *
 */
@Getter
@Setter
public class OutingTimeOfDaily {
	
	//回数：休憩外出回数
	private BreakTimeGoOutTimes workTime;
	//外出理由：外出理由
	private GoingOutReason reason;
	//休暇使用時間：日別実績の時間休暇使用時間
	private TimevacationUseTimeOfDaily timeVacationUseOfDaily;
	//計上用合計時間：外出合計時間
	private OutingTotalTime recordTotalTime;
	//控除用合計時間：外出合計時間
	private OutingTotalTime deductionTotalTime;
	//補正後時間帯:外出時間帯(List)
	private List<OutingTimeSheet> outingTimeSheets;
	
	
	/**
	 * Constcutor
	 */
	public OutingTimeOfDaily(BreakTimeGoOutTimes workTime, GoingOutReason reason,
			TimevacationUseTimeOfDaily timeVacationUseOfDaily, OutingTotalTime recordTotalTime,
			OutingTotalTime deductionTotalTime, List<OutingTimeSheet> outingTimeSheets) {
		super();
		this.workTime = workTime;
		this.reason = reason;
		this.timeVacationUseOfDaily = timeVacationUseOfDaily;
		this.recordTotalTime = recordTotalTime;
		this.deductionTotalTime = deductionTotalTime;
		this.outingTimeSheets = outingTimeSheets;
	}
	
	/** 相殺代休時間を求める */
	public AttendanceTime getOffsetCompensatoryTime() {
		
		/** IF ＠控除用合計時間.合計時間。計算時間　＜　＠休暇使用時間。時間代休使用時間 */
		if (this.deductionTotalTime.getTotalTime().getCalcTime().lessThan(this.timeVacationUseOfDaily.getTimeCompensatoryLeaveUseTime())) {
			/** Return　＠控除用合計時間.合計時間。計算時間	*/
			return this.deductionTotalTime.getTotalTime().getCalcTime();
		}
		
		/** Return　＠休暇使用時間。時間代休使用時間 */
		return this.timeVacationUseOfDaily.getTimeCompensatoryLeaveUseTime();
	}
	
	/**
	 * 日別実績の外出時間
	 * @param recordClass 時間帯作成、時間計算で再取得が必要になっているクラスたちの管理クラス
	 * @return 日別実績の外出時間(List)
	 */
	public static List<OutingTimeOfDaily> calcList(ManageReGetClass recordClass) {
		List<OutingTimeOfDaily> outingList = new ArrayList<OutingTimeOfDaily>();
		if(recordClass.getIntegrationOfDaily().getOutingTime().isPresent()) {
			for(GoingOutReason reason : GoingOutReason.values()) {
				Optional<OutingTimeSheet> outingOfDaily = recordClass.getIntegrationOfDaily().getOutingTime().get().getOutingTimeSheets().stream()
						.filter(o -> o.getReasonForGoOut().equals(reason))
						.findFirst();
				if(!outingOfDaily.isPresent()) {
					continue;
				}
				outingList.add(OutingTimeOfDaily.calcOutingTime(
						reason,
						recordClass.getCalculationRangeOfOneDay(),
						recordClass.getCalculatable(),
						recordClass.getGoOutCalc(),
						PremiumAtr.RegularWork,
						recordClass.getHolidayCalcMethodSet(),
						recordClass.getWorkTimezoneCommonSet(),
						recordClass));
			}
		}
		return outingList;
	}
	
	/**
	 * 全ての外出時間を計算する指示を出すクラス
	 * アルゴリズム：日別実績の外出時間
	 * @param reason 外出理由
	 * @param oneDay 1日の計算範囲
	 * @param isCalculatable 計算処理に入ることができるかフラグ
	 * @param outingCalcSet コアタイム内の外出計算
	 * @param premiumAtr 割増区分
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param commonSetting 就業時間帯の共通設定
	 * @param recordClass 実績
	 * @return 日別実績の外出時間
	 */
	public static OutingTimeOfDaily calcOutingTime(
			GoingOutReason reason,
			CalculationRangeOfOneDay oneDay,
			boolean isCalculatable,
			Optional<OutingCalcWithinCoreTime> outingCalcSet,
			PremiumAtr premiumAtr,
			HolidayCalcMethodSet holidayCalcMethodSet,
			Optional<WorkTimezoneCommonSet> commonSetting,
			ManageReGetClass recordClass) {
		BreakTimeGoOutTimes goOutTimes = new BreakTimeGoOutTimes(0);
		//休暇使用時間
		Optional<TimevacationUseTimeOfDaily> useVacationTime = Optional.empty();
		
		OutingTotalTime recordTotalTime = OutingTotalTime.of(
				TimeWithCalculation.sameTime(new AttendanceTime(0)),
				WithinOutingTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				TimeWithCalculation.sameTime(new AttendanceTime(0))); 
		
		OutingTotalTime dedTotalTime = OutingTotalTime.of(
				TimeWithCalculation.sameTime(new AttendanceTime(0)),
				WithinOutingTotalTime.of(
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0)),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				TimeWithCalculation.sameTime(new AttendanceTime(0)));
		
		//補正後時間帯
		List<OutingTimeSheet> correctedTimeSheet = new ArrayList<>();
		
		if(isCalculatable) {
			//外出回数
			goOutTimes = calcGoOutTimes(recordClass, ConditionAtr.convertFromGoOutReason(reason));
			//休暇使用時間
			useVacationTime = recordClass.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance()
					.flatMap(a -> a.getOutingTimeOfDaily().stream()
							.filter(o -> o.getReason().equals(reason))
							.map(o -> o.getTimeVacationUseOfDaily())
							.findFirst());
			//計上用合計時間
			recordTotalTime = calcOutingTimeDeductAppro(oneDay, reason, outingCalcSet, DeductionAtr.Appropriate); 
			//控除用合計時間
			dedTotalTime = calcOutingTimeDeductAppro(oneDay, reason, outingCalcSet, DeductionAtr.Deduction);
			//補正後時間帯 
		}
		return new OutingTimeOfDaily(
				goOutTimes,
				reason, 
				useVacationTime.orElse(TimevacationUseTimeOfDaily.defaultValue()), 
				recordTotalTime, 
				dedTotalTime,
				correctedTimeSheet);
	}
	
	//外出合計時間の計算
	private static OutingTotalTime calcOutingTimeDeductAppro(CalculationRangeOfOneDay oneDay,
			GoingOutReason reason,
			Optional<OutingCalcWithinCoreTime> outingCalcSet, DeductionAtr deductAtr) {
		
		//外出時間の計算
		OutingTotalTime recordTotalTime = OutingTotalTime.calcOutingTime(oneDay, deductAtr, reason, outingCalcSet,
				deductAtr == DeductionAtr.Deduction ? NotUseAtr.NOT_USE : NotUseAtr.USE);

		//計算外出時間の計算
		OutingTotalTime calcTotalTime = OutingTotalTime.calcOutingTime(oneDay, deductAtr, reason, outingCalcSet, NotUseAtr.NOT_USE);
		
		return OutingTotalTime.of(
				TimeWithCalculation.mergeTimeAndCalcTime(recordTotalTime.getTotalTime(), calcTotalTime.getTotalTime()),
				WithinOutingTotalTime.mergeTimeAndCalcTime(recordTotalTime.getWithinTotalTime(),
						calcTotalTime.getWithinTotalTime()),
				TimeWithCalculation.mergeTimeAndCalcTime(recordTotalTime.getExcessTotalTime(),
						calcTotalTime.getExcessTotalTime()));
	}
	
	/**
	 * 外出回数計算
	 * @param recordClass
	 * @return 休憩外出回数
	 */
	public static BreakTimeGoOutTimes calcGoOutTimes(ManageReGetClass recordClass, ConditionAtr conditionAtr) {
		
		//控除時間帯一覧の取得
		List<TimeSheetOfDeductionItem> list = new ArrayList<>();
		//就業時間内時間帯
		WithinWorkTimeSheet withinWorkTimeSheet = recordClass.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet().get();
		for(WithinWorkTimeFrame withinWorkTimeFrame:withinWorkTimeSheet.getWithinWorkTimeFrame()) {
			list.addAll(withinWorkTimeFrame.getDedTimeSheetByAtr(DeductionAtr.Appropriate, conditionAtr));
			//遅刻
			if(withinWorkTimeFrame.getLateTimeSheet().isPresent()&&withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(DeductionAtr.Appropriate, conditionAtr));
			}
			//早退
			if(withinWorkTimeFrame.getLeaveEarlyTimeSheet().isPresent()&&withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(DeductionAtr.Appropriate, conditionAtr));
			}
		}
		//就業時間外時間帯
		OutsideWorkTimeSheet outsideWorkTimeSheet = recordClass.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get();
		//残業
		if(outsideWorkTimeSheet.getOverTimeWorkSheet().isPresent()) {
			for(OverTimeFrameTimeSheetForCalc overTimeFrameTimeSheetForCalc:outsideWorkTimeSheet.getOverTimeWorkSheet().get().getFrameTimeSheets()) {
				list.addAll(overTimeFrameTimeSheetForCalc.getDedTimeSheetByAtr(DeductionAtr.Appropriate, conditionAtr));
			}
		}
		//休出
		if(outsideWorkTimeSheet.getHolidayWorkTimeSheet().isPresent()) {
			for(HolidayWorkFrameTimeSheetForCalc holidayWorkFrameTimeSheetForCalc:outsideWorkTimeSheet.getHolidayWorkTimeSheet().get().getWorkHolidayTime()) {
				list.addAll(holidayWorkFrameTimeSheetForCalc.getDedTimeSheetByAtr(DeductionAtr.Appropriate, conditionAtr));
			}
		}
		
		List<TimeSheetOfDeductionItem> result = new ArrayList<>();
		for(TimeSheetOfDeductionItem timeSheetOfDeductionItem:list){
			//時間の計算
			if(timeSheetOfDeductionItem.calcTotalTime().greaterThan(0)) {
				result.add(timeSheetOfDeductionItem);
			}
		}
		return new BreakTimeGoOutTimes(result.size());
	}
	
	/**
	 * 休暇加算時間の計算
	 * @param calcMethodSet 休暇の計算方法の設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param workTimeForm 就業時間帯の勤務形態
	 * @return 時間休暇加算時間
	 */
	public AttendanceTime calcVacationAddTime(HolidayCalcMethodSet calcMethodSet, Optional<HolidayAddtionSet> holidayAddtionSet, WorkTimeForm workTimeForm) {
		if(calcMethodSet.getNotUseAtr(PremiumAtr.RegularWork).isNotUse()) {
			return AttendanceTime.ZERO;
		}
		return holidayAddtionSet.get().getAddTime(this.timeVacationUseOfDaily, this.recordTotalTime.getTotalTime().getCalcTime(), workTimeForm);
	}
	
	/**
	 * 時間休暇加算時間を取得する
	 * @param calcMethodSet 休暇の計算方法の設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param deductionTimeSheet 控除時間帯
	 * @param workTimeForm 就業時間帯の勤務形態
	 * @return 時間休暇加算時間
	 */
	public AttendanceTime calcVacationAddTime(
			HolidayCalcMethodSet calcMethodSet,
			HolidayAddtionSet holidayAddtionSet,
			DeductionTimeSheet deductionTimeSheet,
			WorkTimeForm workTimeForm) {
		
		if(calcMethodSet.getNotUseAtr(PremiumAtr.RegularWork).isNotUse()) {
			return AttendanceTime.ZERO;
		}
		//一致する計上用時間帯を取得する
		List<TimeSheetOfDeductionItem> records = deductionTimeSheet.getForRecordTimeZoneList().stream()
				.filter(r -> r.getDeductionAtr().isGoOut() && r.getGoOutReason().equals(Finally.of(this.reason)))
				.collect(Collectors.toList());
		//計算計上用外出時間
		AttendanceTime outCalcTime = deductionTimeSheet.getDeductionTotalTime(records, false).getTotalTime().getCalcTime();
		
		return holidayAddtionSet.getAddTime(this.timeVacationUseOfDaily, outCalcTime, workTimeForm);
	}
	
	public static OutingTimeOfDaily createDefaultWithReason(GoingOutReason reason) {
		return new OutingTimeOfDaily(new BreakTimeGoOutTimes(0), reason, TimevacationUseTimeOfDaily.defaultValue(),
				OutingTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
						WithinOutingTotalTime.sameTime(TimeWithCalculation.sameTime(new AttendanceTime(0))),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				OutingTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
						WithinOutingTotalTime.sameTime(TimeWithCalculation.sameTime(new AttendanceTime(0))),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				new ArrayList<>());
	}
}
