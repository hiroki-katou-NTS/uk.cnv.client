package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkTimeCalcMethodDetailOfHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ActualWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.FlexWithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.SpecBonusPayTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationWorkEachNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.MidNightTimeSheetForCalcList;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.someitems.BonusPayTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.holidaypriorityorder.CompanyHolidayPriorityOrder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.ootsuka.OotsukaStaticService;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.LateEarlyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.OtherEmTimezoneLateEarlySet;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZoneRounding;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.TimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 就業時間内時間枠
 * @author keisuke_hoshina
 */
@Getter
public class WithinWorkTimeFrame extends ActualWorkingTimeSheet {

	/** 就業時間枠NO */
	private final EmTimeFrameNo workingHoursTimeNo;
	/** 勤務NO */
	private final WorkNo workNo;
	/** 遅刻早退控除前時間帯 */
	private TimeSpanForDailyCalc beforeLateEarlyTimeSheet;
	/** 遅刻時間帯 */
	// ・・・deductByLateLeaveEarlyを呼ぶまでは値が無い
	private Optional<LateTimeSheet> lateTimeSheet;
	/** 早退時間帯 */
	// ・・・deductByLateLeaveEarlyを呼ぶまでは値が無い
	private Optional<LeaveEarlyTimeSheet> leaveEarlyTimeSheet;
	/** 所定内割増時間帯 */
	@Setter
	private Optional<WithinPremiumTimeSheetForCalc> premiumTimeSheetInPredetermined;
	
	/**
	 * constructor
	 * @param workingHoursTimeNo
	 * @param timeSheet
	 * @param calculationTimeSheet
	 */
	public WithinWorkTimeFrame(
			EmTimeFrameNo workingHoursTimeNo,
			WorkNo workNo,
			TimeSpanForDailyCalc timeSheet,
			TimeSpanForDailyCalc beforeLateEarlyTimeSheet,
			TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets,
			List<BonusPayTimeSheetForCalc> bonusPayTimeSheet,
			MidNightTimeSheetForCalcList midNightTimeSheet,
			List<SpecBonusPayTimeSheetForCalc> specifiedBonusPayTimeSheet,
			Optional<LateTimeSheet> lateTimeSheet,
			Optional<LeaveEarlyTimeSheet> leaveEarlyTimeSheet) {
		
		super(timeSheet, rounding, recorddeductionTimeSheets,deductionTimeSheets,bonusPayTimeSheet,specifiedBonusPayTimeSheet,midNightTimeSheet);
		this.workingHoursTimeNo = workingHoursTimeNo;
		this.workNo = workNo;
		this.beforeLateEarlyTimeSheet = beforeLateEarlyTimeSheet;
		this.lateTimeSheet = lateTimeSheet;
		this.leaveEarlyTimeSheet = leaveEarlyTimeSheet;
		this.premiumTimeSheetInPredetermined = Optional.empty();
	}
	
	/**
	 * 空で作成する
	 * @param frameNo 就業時間枠No
	 * @return 就業時間内時間枠
	 */
	public static WithinWorkTimeFrame createEmpty(EmTimeFrameNo frameNo, WorkNo workNo) {
		return new WithinWorkTimeFrame(
				frameNo,
				workNo,
				new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(0)),
				new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(0)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				MidNightTimeSheetForCalcList.createEmpty(),
				Collections.emptyList(),
				Optional.empty(),
				Optional.empty());
	}

	/**
	 * 遅刻時間帯の作成
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param lateDesClock 遅刻判断時刻
	 * @param duplicateTimeSheet 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param workNo 勤務No
	 * @param predetermineTime 所定時間帯
	 * @param workType 勤務種類
	 * @param predetermineTimeForSet 計算用就業時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @return 遅刻時間帯
	 */
	public static Optional<LateTimeSheet> createLateTimeSheet(
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			Optional<LateDecisionClock> lateDesClock,
			WithinWorkTimeFrame duplicateTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			int workNo,
			Optional<TimezoneUse> predetermineTime,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {

		if(integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().isFlex() && !integrationOfWorkTime.getFlexWorkSetting().get().getCoreTimeSetting().isUseTimeSheet())
			return Optional.empty();
		
		//遅刻時間帯の作成
		return LateTimeSheet.createLateTimeSheet(
				lateDesClock,
				timeLeavingWork,
				otherEmTimezoneLateEarlySet,
				duplicateTimeSheet,
				deductionTimeSheet,
				predetermineTime,
				workNo,
				workType,
				predetermineTimeForSet,
				integrationOfWorkTime,
				integrationOfDaily);
	}

	/**
	 * 早退時間帯の作成
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param leaveEarlyDesClock 早退判断時刻
	 * @param duplicateTimeSheet 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param workNo 勤務No
	 * @param predetermineTimeSet 所定時間帯
	 * @param workType 勤務種類
	 * @param predetermineTimeForSet 計算用就業時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @return 遅刻早退時間帯
	 */
	public static Optional<LeaveEarlyTimeSheet> createLeaveEarlyTimeSheet(
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			Optional<LeaveEarlyDecisionClock> leaveEarlyDesClock,
			WithinWorkTimeFrame duplicateTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			int workNo,
			Optional<TimezoneUse> predetermineTimeSet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		//早退時間帯の作成
		return LeaveEarlyTimeSheet.createLeaveEarlyTimeSheet(
				leaveEarlyDesClock,
				timeLeavingWork,
				otherEmTimezoneLateEarlySet,
				duplicateTimeSheet,
				deductionTimeSheet,
				predetermineTimeSet,
				workNo,
				workType,
				predetermineTimeForSet,
				integrationOfWorkTime,
				integrationOfDaily);
	}
	
	/**
	 * 実働時間を求め、就業時間を計算する
	 * アルゴリズム：時間帯単位の計算処理
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param premiumAtr 割増区分
	 * @param commonSetting 就業時間帯の共通設定
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @param limitAddTime 時間休暇を加算できる時間
	 * @param parentSheet 就業時間内時間帯
	 * @return 就業時間
	 */
	public AttendanceTime calcActualWorkTimeAndWorkTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			PremiumAtr premiumAtr,
			Optional<WorkTimezoneCommonSet> commonSetting,
			NotUseAtr lateEarlyMinusAtr,
			WithinWorkTimeSheet parentSheet) {
		
		// 休暇の計算方法の設定を取得する
		HolidayCalcMethodSet holidayCalcMethodSet = addSetting.getVacationCalcMethodSet();
		// 日別勤怠の出退勤を確認する
		Optional<TimeLeavingWork> timeLeavingWork = integrationOfDaily.getAttendanceLeave()
				.flatMap(a -> a.getAttendanceLeavingWork(this.workingHoursTimeNo.v()));
		// 遅刻判断時刻を取得する
		Optional<LateDecisionClock> lateDecisionClock =
				parentSheet.getLateDecisionClock(this.workingHoursTimeNo.v());
		// 早退判断時刻を取得する
		Optional<LeaveEarlyDecisionClock> leaveEarlyDecisionClock =
				parentSheet.getLeaveEarlyDecisionClock(this.workingHoursTimeNo.v());
		// 就業時間の計算
		AttendanceTime actualTime = calcActualTime(holidayCalcMethodSet,premiumAtr);
		// 実働時間を就業時間に入れる
		AttendanceTime workTime = new AttendanceTime(actualTime.valueAsMinutes());
		// 遅刻、早退時間を就業時間から控除
		AttendanceTime lateEarlyDiductionTime = this.getLateEarlyDiductionTime(
				integrationOfWorkTime,
				autoCalcOfLeaveEarlySetting,
				holidayCalcMethodSet,
				premiumAtr,
				commonSetting,
				lateEarlyMinusAtr,
				timeLeavingWork,
				lateDecisionClock,
				leaveEarlyDecisionClock);
		workTime = new AttendanceTime(workTime.valueAsMinutes() - lateEarlyDiductionTime.valueAsMinutes());
		// 就業時間に加算する時間休暇相殺時間を取得
		AttendanceTime timeVacationOffsetTime = this.getTimeVacationOffsetTimeForAddWorkTime(
				integrationOfDaily, integrationOfWorkTime, addSetting, holidayAddtionSet);
		workTime = new AttendanceTime(workTime.valueAsMinutes() + timeVacationOffsetTime.valueAsMinutes());
		if(this.leaveEarlyTimeSheet.isPresent() && this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().isPresent()) {
			//休暇加算によって、就業時間から溢れて残業になる時間帯
			TimeSpanForDailyCalc overTimebyTimeVacation = new TimeSpanForDailyCalc(
					this.timeSheet.getEnd(),
					this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().getAfterRoundingAsLeaveEarly().getStart());
			workTime = new AttendanceTime(workTime.valueAsMinutes() - overTimebyTimeVacation.lengthAsMinutes());
		}
		// 丸め処理
		TimeRoundingSetting rounding = this.getRounding();
		workTime = new AttendanceTime(rounding.round(workTime.valueAsMinutes()));
		// 就業時間を返す
		return workTime;
	}
	
	/**
	 * 遅刻、早退時間を就業時間から控除
	 * 
	 * 遅刻早退を強制的に控除する==true				=>	控除する
	 * 遅刻早退を強制的に控除する==false
	 * 		休暇の計算方法の設定==控除する
	 * 				就業時間に猶予時間を含む
	 * 						遅刻(早退)している		=>	控除する
	 * 						遅刻(早退)していない	=>	控除しない①
	 * 				就業時間に猶予時間を含まない	=>	控除する
	 * 		休暇の計算方法の設定=控除しない			=>	控除しない②
	 * 
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param premiumAtr 割増区分
	 * @param commonSetting 就業時間帯の共通設定
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する（true:強制的に控除する  false:強制的に控除しない（設定を参照する））
	 * @param attendanceLeavingWork 日別勤怠の出退勤
	 * @param lateDecisionClock 遅刻判断時刻
	 * @param leaveEarlyDecisionClock 早退判断時刻
	 * @return 遅刻早退控除時間
	 */
	private AttendanceTime getLateEarlyDiductionTime(
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			HolidayCalcMethodSet holidayCalcMethodSet,
			PremiumAtr premiumAtr,
			Optional<WorkTimezoneCommonSet> commonSetting,
			NotUseAtr lateEarlyMinusAtr,
			Optional<TimeLeavingWork> timeLeavingWork,
			Optional<LateDecisionClock> lateDecisionClock,
			Optional<LeaveEarlyDecisionClock> leaveEarlyDecisionClock) {
		
		// 遅刻早退を控除するかどうか判断
		if (!WithinWorkTimeFrame.isDeductLateLeaveEarly(
				integrationOfWorkTime, premiumAtr, holidayCalcMethodSet, commonSetting, lateEarlyMinusAtr)) {
			// 控除しない②
			return AttendanceTime.ZERO;
		}
		//遅刻控除時間
		int lateDeductTime = 0;
		if(commonSetting.get().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getGraceTimeSet().isIncludeWorkingHour()
				&& (lateDecisionClock.isPresent() && timeLeavingWork.map(t -> t.getAttendanceTime()).isPresent()
						&& !lateDecisionClock.get().isLate(timeLeavingWork.get().getAttendanceTime().get()))
				&& lateEarlyMinusAtr.equals(NotUseAtr.NOT_USE)) {
			//控除しない①
			lateDeductTime = 0;
		} else {
			//控除する
			if(this.lateTimeSheet.flatMap(l -> l.getForDeducationTimeSheet()).isPresent()) {
				//遅刻時間帯と自身が重複している時間
				Optional<TimeSpanForDailyCalc> lateDupulicateTime
						= this.lateTimeSheet.get().getForDeducationTimeSheet().get().getTimeSheet().getDuplicatedWith(this.beforeLateEarlyTimeSheet);
				
				if(lateDupulicateTime.isPresent()) {
					//就業時間内時間枠の時間帯と重複している遅刻時間帯のみを控除する
					this.lateTimeSheet.get().getForDeducationTimeSheet().get().shiftTimeSheet(lateDupulicateTime.get());
					lateDeductTime = this.lateTimeSheet.get().calcDedctionTime(autoCalcOfLeaveEarlySetting.isLate(), NotUseAtr.USE).getCalcTime().valueAsMinutes();
				}
			}
		}
		//早退控除時間
		int leaveEarlyDeductTime = 0;
		if(commonSetting.get().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY).getGraceTimeSet().isIncludeWorkingHour()
				&& (leaveEarlyDecisionClock.isPresent() && timeLeavingWork.map(t -> t.getLeaveTime()).isPresent()
						&& !leaveEarlyDecisionClock.get().isLeaveEarly(timeLeavingWork.get().getLeaveTime().get()))
				&& lateEarlyMinusAtr.equals(NotUseAtr.NOT_USE)) {
			//控除しない①
			leaveEarlyDeductTime = 0;
		} else {
			//控除する
			if(this.leaveEarlyTimeSheet.flatMap(l -> l.getForDeducationTimeSheet()).isPresent()) {
				//早退時間帯と自身が重複している時間
				Optional<TimeSpanForDailyCalc> leaveEarlyDupulicateTime 
						= this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().getTimeSheet().getDuplicatedWith(this.beforeLateEarlyTimeSheet);
				
				if(leaveEarlyDupulicateTime.isPresent()) {
					//就業時間内時間枠の時間帯と重複している早退時間帯のみを控除する
					this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().shiftTimeSheet(leaveEarlyDupulicateTime.get());
					leaveEarlyDeductTime = this.leaveEarlyTimeSheet.get().calcDedctionTime(autoCalcOfLeaveEarlySetting.isLeaveEarly(), NotUseAtr.USE).getCalcTime().valueAsMinutes();
				}
			}
		}
		int lateLeaveEarlySubtraction = lateDeductTime + leaveEarlyDeductTime;
		return new AttendanceTime(lateLeaveEarlySubtraction);
	}
	
	/**
	 * 実働時間を計算する
	 * @return　実働時間
	 */
	public AttendanceTime calcActualTime(HolidayCalcMethodSet holidayCalcMethodSet,PremiumAtr premiumAtr) {
		//開始～終了の間の時間を計算する
		AttendanceTime result = new AttendanceTime(this.beforeLateEarlyTimeSheet.getTimeSpan().lengthAsMinutes());
		//控除時間を控除する
		result =  result.minusMinutes(this.calcDeductionTime(holidayCalcMethodSet, premiumAtr).valueAsMinutes());
		//丸め処理
		result = new AttendanceTime(this.rounding.round(result.valueAsMinutes()));
		return result;
	}
	
	//控除時間の計算
	public AttendanceTime calcDeductionTime(HolidayCalcMethodSet holidayCalcMethodSet,PremiumAtr premiumAtr) {
		AttendanceTime result = new AttendanceTime(0);
		//休憩
		result = result.addMinutes(((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.BREAK).valueAsMinutes());
		//外出(私用)
		result = result.addMinutes(((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.PrivateGoOut).valueAsMinutes());
		//外出(組合)
		result = result.addMinutes(((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.UnionGoOut).valueAsMinutes());
		//短時間
		AttendanceTime shortTime = new AttendanceTime(0);
		//介護
		AttendanceTime careTime = new AttendanceTime(0);
		//短時間勤務を控除するか判断
		if(premiumAtr.isRegularWork() && !holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().isCalculateIncludCareTime()) {
			shortTime = ((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.Child);
			careTime = ((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.Care);
		}
		if(premiumAtr.isPremium() && !holidayCalcMethodSet.getPremiumCalcMethodOfHoliday().isCalculateIncludCareTime()) {
			shortTime = ((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.Child);
			careTime = ((CalculationTimeSheet)this).calcDedTimeByAtr(DeductionAtr.Deduction,ConditionAtr.Care);
		}
		result = result.addMinutes(shortTime.valueAsMinutes());
		result = result.addMinutes(careTime.valueAsMinutes());
		return result;
	}

	/**
	 * 就業時間内時間枠毎の処理
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingWork 出退勤
	 * @param duplicateTimeSheet 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param lateDesClock 遅刻判断時刻
	 * @param leaveEarlyDesClock 早退判断時刻
	 * @param timeVacationWork 時間休暇WORK
	 * @param isLastIndex ループの最後である
	 * @return 就業時間内時間枠
	 */
	public static WithinWorkTimeFrame createWithinWorkTimeFrame(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeLeavingWork timeLeavingWork,
			WithinWorkTimeFrame duplicateTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			Optional<LateDecisionClock> lateDesClock,
			Optional<LeaveEarlyDecisionClock> leaveEarlyDesClock,
			TimeVacationWork timeVacationWork,
			boolean isLastIndex) {
		
		boolean isDeductLate = true;
		boolean isDeductLeaveEarly = true;
		
		TimeWithDayAttr startOclock = duplicateTimeSheet.getTimeSheet().getStart();
		TimeWithDayAttr endOclock = duplicateTimeSheet.getTimeSheet().getEnd();
		
		EmTimeZoneSet dupTimeSheet = new EmTimeZoneSet(
				duplicateTimeSheet.getWorkingHoursTimeNo(),
				new TimeZoneRounding(duplicateTimeSheet.getTimeSheet().getStart(),
				duplicateTimeSheet.getTimeSheet().getEnd(),
				duplicateTimeSheet.getRounding()));

		List<TimeSheetOfDeductionItem> addBreakListInLateEarly = new ArrayList<>();
		
		//遅刻時間帯の作成
		Optional<LateTimeSheet> lateTimeSheet = createLateTimeSheet(
				timeLeavingWork,
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE),
				lateDesClock,
				duplicateTimeSheet,
				deductionTimeSheet,
				timeLeavingWork.getWorkNo().v(),
				predetermineTimeSetForCalc.getTimeSheets(todayWorkType.getDailyWork().decisionNeedPredTime(), timeLeavingWork.getWorkNo().v()),
				todayWorkType,
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily);
		
		//遅刻時間を計算する
		AttendanceTime lateDeductTime = AttendanceTime.ZERO;
		if(lateTimeSheet.isPresent() && lateTimeSheet.get().getForDeducationTimeSheet().isPresent()) {
			lateDeductTime = lateTimeSheet.get().getForDeducationTimeSheet().get().calcTotalTime();
		}
		
		if(lateDesClock.isPresent()) {//←のifはフレックスの最低勤務時間利用の場合に下記処理を走らせたくない為追加
			Optional<WorkTimeCalcMethodDetailOfHoliday> holidayCalcMethodSet = personDailySetting.getAddSetting().getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet();
			if(holidayCalcMethodSet.isPresent()) {
				
				//就業時間内時間帯から控除するか判断し控除する
				isDeductLate = holidayCalcMethodSet.get().decisionLateDeductSetting(
						lateDeductTime,
						integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getGraceTimeSet(),
						Optional.of(integrationOfWorkTime.getCommonSetting()));
				
				//控除後の出勤時刻を取得（控除しない場合は、所定の開始時刻をセットする)
				startOclock = getDeductedStartOclockByLate(
						companyCommonSetting,
						personDailySetting,
						todayWorkType,
						integrationOfWorkTime,
						integrationOfDaily,
						timeLeavingWork,
						duplicateTimeSheet,
						deductionTimeSheet,
						predetermineTimeSetForCalc,
						isLastIndex,
						isDeductLate,
						lateTimeSheet);

				//遅刻早退時間帯が持っている休憩を就業時間枠時間帯へ入れる
				if(lateTimeSheet.isPresent() && lateTimeSheet.get().getForDeducationTimeSheet() != null && lateTimeSheet.get().getForDeducationTimeSheet().isPresent()) {
					addBreakListInLateEarly.addAll(lateTimeSheet.get().getForDeducationTimeSheet().get().getDeductionTimeSheet().stream().filter(tc -> tc.getDeductionAtr().isBreak()).collect(Collectors.toList()));
				}
			}
		}
		//早退時間帯の作成
		Optional<LeaveEarlyTimeSheet> LeaveEarlyTimeSheet = createLeaveEarlyTimeSheet(
				timeLeavingWork,
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY),
				leaveEarlyDesClock,
				duplicateTimeSheet,
				deductionTimeSheet,
				timeLeavingWork.getWorkNo().v(),
				predetermineTimeSetForCalc.getTimeSheets(todayWorkType.getDailyWork().decisionNeedPredTime(), timeLeavingWork.getWorkNo().v()),
				todayWorkType,
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily);
		
		//早退時間を計算する
		AttendanceTime LeaveEarlyDeductTime = AttendanceTime.ZERO;
		if(LeaveEarlyTimeSheet.isPresent() && LeaveEarlyTimeSheet.get().getForDeducationTimeSheet().isPresent()) {
			LeaveEarlyDeductTime = LeaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().calcTotalTime();
		}
		
		if(leaveEarlyDesClock.isPresent()) {//←のifはフレックスの最低勤務時間利用の場合に下記処理を走らせたくない為追加
			Optional<WorkTimeCalcMethodDetailOfHoliday> holidayCalcMethodSet = personDailySetting.getAddSetting().getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet();
			if(holidayCalcMethodSet.isPresent()) {
				
				//就業時間内時間帯から控除するか判断
				isDeductLeaveEarly = (holidayCalcMethodSet.get().decisionLateDeductSetting(LeaveEarlyDeductTime,
					integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY).getGraceTimeSet(),
					Optional.of(integrationOfWorkTime.getCommonSetting())));
				
				//控除後の退勤時刻を取得（控除しない場合は、所定の終了時刻をセットする)
				endOclock = getDeductedEndOclockByEarly(
						companyCommonSetting,
						personDailySetting,
						todayWorkType,
						integrationOfWorkTime,
						integrationOfDaily,
						timeLeavingWork,
						duplicateTimeSheet,
						deductionTimeSheet,
						predetermineTimeSetForCalc,
						isLastIndex,
						isDeductLeaveEarly,
						LeaveEarlyTimeSheet);

				//遅刻早退時間帯が持っている休憩を就業時間枠時間帯へ入れる
				if(LeaveEarlyTimeSheet.isPresent() && LeaveEarlyTimeSheet.get().getForDeducationTimeSheet() != null && LeaveEarlyTimeSheet.get().getForDeducationTimeSheet().isPresent()) {
					addBreakListInLateEarly.addAll(LeaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().getDeductionTimeSheet().stream().filter(tc -> tc.getDeductionAtr().isBreak()).collect(Collectors.toList()));
				}
			}
		}
		
		//控除後の出勤と退勤時刻から時間帯を作成する。
		dupTimeSheet = new EmTimeZoneSet(
				new EmTimeFrameNo(timeLeavingWork.getWorkNo().v()), 
				new TimeZoneRounding(startOclock, endOclock, duplicateTimeSheet.getRounding()));
		
		// 就業時間内時間枠を作成する
		WithinWorkTimeFrame result = new WithinWorkTimeFrame(
				duplicateTimeSheet.getWorkingHoursTimeNo(),
				duplicateTimeSheet.getWorkNo(),
				new TimeSpanForDailyCalc(dupTimeSheet.getTimezone().getTimeSpan()),
				duplicateTimeSheet.getBeforeLateEarlyTimeSheet(),
				duplicateTimeSheet.getRounding(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				MidNightTimeSheetForCalcList.createEmpty(),
				new ArrayList<>(),
				lateTimeSheet,
				LeaveEarlyTimeSheet);
		
		// 控除時間帯をクローンする
		DeductionTimeSheet deductForWithin = new DeductionTimeSheet(
				new ArrayList<>(deductionTimeSheet.getForDeductionTimeZoneList()),
				new ArrayList<>(deductionTimeSheet.getForRecordTimeZoneList()),
				deductionTimeSheet.getBreakTimeOfDailyList(),
				deductionTimeSheet.getDailyGoOutSheet(),
				deductionTimeSheet.getShortTimeSheets());
		
		// 大塚モードの休憩時間帯取得
		List<TimeSheetOfDeductionItem> ootsukaBreak = OotsukaStaticService.getBreakTimeSheet(
				todayWorkType, integrationOfWorkTime, integrationOfDaily.getAttendanceLeave());
		deductForWithin.getForDeductionTimeZoneList().addAll(ootsukaBreak);
		deductForWithin.getForRecordTimeZoneList().addAll(ootsukaBreak);

		// フレックス勤務日かどうか
		if (integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().isFlexWorkDay(
				personDailySetting.getPersonInfo()) == true){
			// コア内の外出時間帯を控除する
			deductForWithin = FlexWithinWorkTimeSheet.deductGoOutSheetWithinCore(
					integrationOfWorkTime, deductForWithin);
		}
		
		// 控除時間帯の登録
		result.registDeductionListForWithin(deductForWithin, Optional.of(integrationOfWorkTime.getCommonSetting()));

		// 控除時間帯の相殺時間を計算
		result.calcOffsetTimeOfDeductTimeSheet(result.getWorkNo().v(), timeVacationWork,
				new CompanyHolidayPriorityOrder(integrationOfWorkTime.getWorkTimeSetting().getCompanyId()));
		
		// 加給時間帯を作成
		result.createBonusPayTimeSheet(
				personDailySetting.getBonusPaySetting(), integrationOfDaily.getSpecDateAttr(), deductForWithin);
		
		// 深夜時間帯を作成
		result.createMidNightTimeSheet(companyCommonSetting.getMidNightTimeSheet(),
				Optional.of(integrationOfWorkTime.getCommonSetting()), deductForWithin);

		// 就業時間内時間枠を返す
		return result;
	}
	
	/**
	 * 控除時間帯の登録（就業時間内時間枠）
	 * @param deductTimeSheet 控除時間帯
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void registDeductionListForWithin(
			DeductionTimeSheet deductTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSet){
		//遅刻早退控除前の時間帯で控除時間を登録しないと、
		//退勤後の休憩が求まらない等の問題が起きる為、一旦退避して置き換える。
		TimeSpanForDailyCalc afterDeduct = this.timeSheet.clone();
		this.timeSheet = this.beforeLateEarlyTimeSheet.clone();
		// 控除時間帯の登録
		this.registDeductionList(ActualWorkTimeSheetAtr.WithinWorkTime, deductTimeSheet, commonSet);
		this.timeSheet = afterDeduct;
		// 保持する控除時間帯を取得（計上用）
		List<TimeSheetOfDeductionItem> recordList = WithinWorkTimeFrame.getDeductSheetForSave(
				this.beforeLateEarlyTimeSheet, this.timeSheet, deductTimeSheet, DeductionAtr.Appropriate);
		// 保持する控除時間帯を取得（控除用）
		List<TimeSheetOfDeductionItem> deductList = WithinWorkTimeFrame.getDeductSheetForSave(
				this.beforeLateEarlyTimeSheet, this.timeSheet, deductTimeSheet, DeductionAtr.Deduction);
		// 控除時間帯リストを入れ替える
		this.recordedTimeSheet = recordList;
		this.deductionTimeSheet = deductList;
	}
	
	/**
	 * 保持する控除時間帯を取得
	 * @param beforeDeductSheet 控除前時間帯
	 * @param afterDeductSheet 控除後時間帯
	 * @param deductTimeSheet 控除時間帯
	 * @param deductAtr 控除区分
	 * @return 控除項目の時間帯(List)
	 */
	public static List<TimeSheetOfDeductionItem> getDeductSheetForSave(
			TimeSpanForDailyCalc beforeDeductSheet,
			TimeSpanForDailyCalc afterDeductSheet,
			DeductionTimeSheet deductTimeSheet,
			DeductionAtr deductAtr){

		List<TimeSheetOfDeductionItem> results = new ArrayList<>();
		
		// 計算範囲による絞り込み（控除前）
		List<TimeSheetOfDeductionItem> beforeDeduct = deductTimeSheet.getDupliRangeTimeSheet(
				beforeDeductSheet, deductAtr);
		
		if (deductAtr == DeductionAtr.Deduction){
			// 控除用
			// 結果に控除前をすべて追加
			results.addAll(beforeDeduct);
		}
		else if (deductAtr == DeductionAtr.Appropriate){
			// 計上用
			// 計算範囲による絞り込み（控除後）
			List<TimeSheetOfDeductionItem> afterDeduct = deductTimeSheet.getDupliRangeTimeSheet(
					afterDeductSheet, deductAtr);
			// 結果に控除前（短時間勤務のみ）を追加
			results.addAll(beforeDeduct.stream()
					.filter(c -> c.getDeductionAtr() == DeductionClassification.CHILD_CARE)
					.collect(Collectors.toList()));
			// 結果に控除後（短時間勤務以外）を追加
			results.addAll(afterDeduct.stream()
					.filter(c -> c.getDeductionAtr() != DeductionClassification.CHILD_CARE)
					.collect(Collectors.toList()));
		}
		// 結果を返す
		return results;
	}
	
	/**
	 * 控除後の出勤時刻を取得する
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingWork 出退勤
	 * @param duplicateTimeSheet 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param isLastIndex ループの最後である
	 * @param isDeductLeaveLate 控除する
	 * @param lateTimeSheet 遅刻時間帯
	 * @return 控除後の出勤時刻
	 */
	private static TimeWithDayAttr getDeductedStartOclockByLate(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeLeavingWork timeLeavingWork,
			WithinWorkTimeFrame duplicateTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			boolean isLastIndex,
			boolean isDeductLeaveLate,
			Optional<LateTimeSheet> lateTimeSheet) {
		
		TimeWithDayAttr startTime = duplicateTimeSheet.getTimeSheet().getStart();
			
		//フレックスの場合はコアタイムを使って遅刻早退の控除を行う。
		//出勤 < コアタイム.開始　→　出勤
		//出勤 > コアタイム.開始　→　コアタイム.開始　を開始時刻とする。
		if(integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().isFlex()) {
			CoreTimeSetting coreTimeSetting = integrationOfWorkTime.getFlexWorkSetting().get().getCoreTimeSetting();
			if(coreTimeSetting.isUseTimeSheet()) {
				startTime = getDecisionCoreTimeSheet(predetermineTimeSetForCalc,coreTimeSetting,todayWorkType).getStartTime();
				if(startTime.greaterThan(duplicateTimeSheet.getTimeSheet().getStart())) {
					startTime = duplicateTimeSheet.getTimeSheet().getStart();
				}
			}
		}

		//就業時間帯枠=1の場合は、
		boolean isFirstIndex = duplicateTimeSheet.getWorkingHoursTimeNo().v().equals(1);
		if(lateTimeSheet.isPresent() && lateTimeSheet.get().getForDeducationTimeSheet().isPresent() && isFirstIndex) {
			EmTimeZoneSet emDuplicateTimeSheet = new EmTimeZoneSet(
					new EmTimeFrameNo(timeLeavingWork.getWorkNo().v()), 
					new TimeZoneRounding(duplicateTimeSheet.getTimeSheet().getStart(), duplicateTimeSheet.getTimeSheet().getEnd(), duplicateTimeSheet.getRounding()));
			Optional<TimeWithDayAttr> correctStartOclock = deductProcessForLate(
					lateTimeSheet.get(), deductionTimeSheet, false, emDuplicateTimeSheet,
					todayWorkType, integrationOfWorkTime, integrationOfDaily);
			startTime = isDeductLeaveLate && correctStartOclock.isPresent()
					?correctStartOclock.get()
					:lateTimeSheet.get().getForDeducationTimeSheet().get().getTimeSheet().getStart();
		}
		return startTime;
	}
	
	/**
	 * 控除後の退勤時刻を取得する
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingWork 出退勤
	 * @param duplicateTimeSheet 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param breakTimeFromMaster 控除項目の時間帯(List) ※就業時間帯マスタから取得した休憩時間帯（大塚用）
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param isLastIndex ループの最後である
	 * @param isDeductLeaveLate 控除する
	 * @param earlyTimeSheet 早退時間帯
	 * @return 控除後の退勤時刻
	 */
	private static TimeWithDayAttr getDeductedEndOclockByEarly(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeLeavingWork timeLeavingWork,
			WithinWorkTimeFrame duplicateTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			boolean isLastIndex,
			boolean isDeductLeaveEarly,
			Optional<LeaveEarlyTimeSheet> earlyTimeSheet) {
	
		//控除が無ければそのままの終了時刻を使う。
		TimeWithDayAttr endTime = duplicateTimeSheet.getTimeSheet().getEnd();
		
		//フレックスの場合はコアタイム
		if(integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().isFlex()) {
			CoreTimeSetting coreTimeSetting = integrationOfWorkTime.getFlexWorkSetting().get().getCoreTimeSetting();
			if(coreTimeSetting.isUseTimeSheet()) {
				endTime = getDecisionCoreTimeSheet(predetermineTimeSetForCalc,coreTimeSetting,todayWorkType).getEndTime();
				if(endTime.lessThan(duplicateTimeSheet.getTimeSheet().getEnd())) {
					endTime = duplicateTimeSheet.getTimeSheet().getEnd();
				}
			}
		}
		
		//早退時間帯がそもそも存在しない　→　引数dupTimeSheetの終了をそのまま（現状維持)　控除しない場合　→　leaveearlyのend する場合　→ leaveEarlyのstart
		if(earlyTimeSheet.isPresent() && earlyTimeSheet.get().getForDeducationTimeSheet().isPresent() && isLastIndex) {
			EmTimeZoneSet emDuplicateTimeSheet = new EmTimeZoneSet(
					new EmTimeFrameNo(timeLeavingWork.getWorkNo().v()), 
					new TimeZoneRounding(duplicateTimeSheet.getTimeSheet().getStart(), duplicateTimeSheet.getTimeSheet().getEnd(), duplicateTimeSheet.getRounding()));
			Optional<TimeWithDayAttr> correctEndOclock = deductProcessForEarly(
					earlyTimeSheet.get(), deductionTimeSheet, false, emDuplicateTimeSheet,
					todayWorkType, integrationOfWorkTime, integrationOfDaily);
			endTime = isDeductLeaveEarly && correctEndOclock.isPresent() 
					?correctEndOclock.get()
					:earlyTimeSheet.get().getForDeducationTimeSheet().get().getTimeSheet().getEnd();
		 }
		 return endTime;
	}
	
	private static Optional<TimeWithDayAttr> deductProcessForLate(
			LateTimeSheet lTSheet,
			DeductionTimeSheet deductionTimeSheet,
			boolean isOOtsuka,
			EmTimeZoneSet originDupTimeSheet,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		if(lTSheet == null) return Optional.empty();
		List<TimeSheetOfDeductionItem> dedList = new ArrayList<>(deductionTimeSheet.getForDeductionTimeZoneList()
				.stream().filter(t -> t.getDeductionAtr().isBreak()).collect(Collectors.toList()));
		TimeWithDayAttr startOclock = lTSheet.getForDeducationTimeSheet().get().getTimeSheet().getEnd();
		if(isOOtsuka) {
			dedList.addAll(OotsukaStaticService.getBreakTimeSheet(
					todayWorkType, integrationOfWorkTime, integrationOfDaily.getAttendanceLeave()));
		}
		for(TimeSheetOfDeductionItem tod : dedList) {
			if(tod.contains(startOclock)) {
				if(tod.contains(originDupTimeSheet.getTimezone().getStart())) {
					startOclock = originDupTimeSheet.getTimezone().getStart();
				}
				else if(originDupTimeSheet.getTimezone().getStart().lessThan(tod.getTimeSheet().getStart())) {
					startOclock = tod.getTimeSheet().getStart();
				}
				else if(originDupTimeSheet.getTimezone().getStart().greaterThan(tod.getTimeSheet().getStart())) {
					startOclock = tod.getTimeSheet().getEnd();
				}
//				else {
//					startOclock = tod.getTimeSheet().getEnd();	
//				}
			}
		}
		return Optional.of(startOclock);
	}
	
	private static Optional<TimeWithDayAttr> deductProcessForEarly(
			LeaveEarlyTimeSheet lSheet,
			DeductionTimeSheet deductionTimeSheet,
			boolean isOOtsuka,
			EmTimeZoneSet originDupTimeSheet,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		if(lSheet == null) return Optional.empty();
		List<TimeSheetOfDeductionItem> dedList = new ArrayList<>(deductionTimeSheet.getForDeductionTimeZoneList()
				.stream().filter(t -> t.getDeductionAtr().isBreak()).collect(Collectors.toList()));
		TimeWithDayAttr endOclock = lSheet.getForDeducationTimeSheet().get().getTimeSheet().getStart();
		if(isOOtsuka) {
			dedList = OotsukaStaticService.getBreakTimeSheet(
					todayWorkType, integrationOfWorkTime, integrationOfDaily.getAttendanceLeave());
		}
		for(TimeSheetOfDeductionItem tod : dedList) {
			if(tod.contains(endOclock)) {
				if(tod.contains(originDupTimeSheet.getTimezone().getEnd())) {
					endOclock = originDupTimeSheet.getTimezone().getEnd();
				}
				else if(originDupTimeSheet.getTimezone().getStart().greaterThan(tod.getTimeSheet().getStart())) {
					endOclock = tod.getTimeSheet().getStart();
				}
				else if(originDupTimeSheet.getTimezone().getStart().lessThan(tod.getTimeSheet().getStart())) {
					endOclock = tod.getTimeSheet().getEnd();
				}
//				else {
//					endOclock = tod.getTimeSheet().getStart();	
//				}
			}
		}
		return Optional.of(endOclock);
	}


	/**
	 * 遅刻早退を控除するかどうか判断
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param commonSetting 就業時間帯の共通設定
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return true:控除する,false:控除しない
	 */
	public static boolean isDeductLateLeaveEarly(
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			HolidayCalcMethodSet holidayCalcMethodSet,
			Optional<WorkTimezoneCommonSet> commonSetting,
			NotUseAtr lateEarlyMinusAtr) {

		// 強制的に遅刻早退控除する時、「控除する」を返す
		if (lateEarlyMinusAtr.equals(NotUseAtr.USE)) return true;
		// 流動勤務の時、常に控除する
		if (integrationOfWorkTime.isPresent()){
			if (integrationOfWorkTime.get().getWorkTimeSetting().getWorkTimeDivision().isFlow()) return true;
		}
		// 就業の休暇の就業時間計算方法詳細．遅刻・早退を控除する
		if(premiumAtr.isRegularWork() && holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().isDeductLateLeaveEarly(commonSetting))
			return true;
		// 割増の休暇の就業時間計算方法詳細．遅刻・早退を控除する
		if(premiumAtr.isPremium() && holidayCalcMethodSet.getPremiumCalcMethodOfHoliday().isDeductLateLeaveEarly(commonSetting))
			return true;
		
		return false;
	}
	
	/**
	 * 大塚モード使用時専用の遅刻、早退削除処理
	 */
	public void cleanLateLeaveEarlyTimeForOOtsuka() {
		lateTimeSheet = Optional.empty();
		leaveEarlyTimeSheet = Optional.empty();
	}
	
	/**
	 * コアタイム時間帯を午前終了、午後開始で補正したコアタイム時間帯の取得
	 * コアありフレの場合に就業時間内時間帯から遅刻早退を控除しない場合用
	 * @param predetermineTimeForSet
	 * @return
	 */
	public static TimeSheet getDecisionCoreTimeSheet(PredetermineTimeSetForCalc predetermineTimeForSet,CoreTimeSetting coreTimeSetting,WorkType workType) {
		
		TimeSheet result = coreTimeSetting.getCoreTimeSheet();
		
		switch (workType.getAttendanceHolidayAttr()) {
		case MORNING:
			TimeWithDayAttr end = result.getEndTime();
			if(predetermineTimeForSet.getAMEndTime().lessThan(end.valueAsMinutes())) {
				end = predetermineTimeForSet.getAMEndTime();
			}
			result = new TimeSheet(result.getStartTime(), end);
			return result;
		case AFTERNOON:
			TimeWithDayAttr start = result.getStartTime();
			if(predetermineTimeForSet.getPMStartTime().greaterThan(start.valueAsMinutes())) {
				start = predetermineTimeForSet.getPMStartTime();
			}
			result = new TimeSheet(start,result.getEndTime());
			return result;
		case FULL_TIME:
		case HOLIDAY:
			return result;
		default:
			throw new RuntimeException("unknown attr:" + workType.getAttendanceHolidayAttr());
		}
			
	}
	
	/**
	 * 自身が持つ短時間勤務時間帯(控除)を収集
	 * @return　短時間勤務時間帯
	 */
	public List<TimeSheetOfDeductionItem> collectShortTimeSheetInFrame(){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>(); 
		//自身が持つ短時間時間帯を保持
		returnList.addAll(this.collectShortTimeSheet());
		//遅刻時間帯の短時間時間帯を保持
		if(this.getLateTimeSheet() != null && this.getLateTimeSheet().isPresent())
			returnList.addAll(this.getLateTimeSheet().get().getShortTimeSheet());
		//早退時間帯の短時間時間帯を保持
		if(this.getLeaveEarlyTimeSheet() != null && this.getLeaveEarlyTimeSheet().isPresent())
			returnList.addAll(this.getLeaveEarlyTimeSheet().get().getShortTimeSheet());
		return returnList;
	}
	
	 /**
	 * 遅刻時間帯の作成（流動）
	 * @param lateDesClock 遅刻判断時刻
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSet 所定時間設定
	 * @param workNo 勤務NO
	 * @param workType 勤務種類
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 */
	public void createLateTimeSheet(
			Optional<LateDecisionClock> lateDesClock,
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimezoneUse> predetermineTimeSet,
			int workNo,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		this.lateTimeSheet = LateTimeSheet.createLateTimeSheet(
				lateDesClock,
				timeLeavingWork,
				otherEmTimezoneLateEarlySet,
				withinWorkTimeFrame,
				deductionTimeSheet,
				predetermineTimeSet,
				workNo,
				workType,
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily);
	}
	
	 /**
	 * 早退時間帯の作成（流動）
	 * @param leaveEarlyDesClock 早退判断時刻
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTime 所定時間設定
	 * @param workNo 勤務NO
	 * @param workType 勤務種類
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 */
	public void createLeaveEarlyTimeSheet(
			Optional<LeaveEarlyDecisionClock> leaveEarlyDesClock,
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimezoneUse> predetermineTime,
			int workNo,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {

		this.leaveEarlyTimeSheet = LeaveEarlyTimeSheet.createLeaveEarlyTimeSheet(
				leaveEarlyDesClock,
				timeLeavingWork,
				otherEmTimezoneLateEarlySet,
				withinWorkTimeFrame,
				deductionTimeSheet,
				predetermineTime,
				workNo,
				workType,
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily);
	}
	
	/**
	 * 遅刻早退控除前時間帯を作成する
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param timeLeavingOfDailyAttd 日別勤怠の出退勤
	 * @param startOverTime 残業開始時刻
	 */
	public void setBeforeLateEarlyTimeSheet(
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			PredetermineTimeSetForCalc predetermineTimeSet,
			Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd,
			TimeWithDayAttr startOverTime) {
		this.beforeLateEarlyTimeSheet = this.timeSheet.clone();
		Optional<TimeLeavingWork> timeLeavingWork = timeLeavingOfDailyAttd.flatMap(t -> t.getAttendanceLeavingWork(this.workingHoursTimeNo.v()));
		if (!timeLeavingWork.isPresent()) {
			return;
		}
		this.beforeLateEarlyTimeSheet = getBeforeLateEarlyTimeSheet(
				this.beforeLateEarlyTimeSheet,
				workType,
				integrationOfWorkTime,
				predetermineTimeSet,
				timeLeavingWork.get(),
				Optional.of(startOverTime));
	}
	
	/**
	 * 遅刻早退控除前時間帯を取得する
	 * @param timeSpan 就業時間内時間枠．時間帯
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param timeLeavingWork 出退勤
	 * @param endTime 終了時刻
	 * @return 遅刻早退控除前時間帯
	 */
	public static TimeSpanForDailyCalc getBeforeLateEarlyTimeSheet(
			TimeSpanForDailyCalc timeSpan,
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			TimeLeavingWork timeLeavingWork,
			Optional<TimeWithDayAttr> endTime) {
		
		TimeSpanForDailyCalc beforeLateEarlyTimeSheet = timeSpan.clone();
		
		// 所定時間設定の取得
		Optional<TimezoneUse> predetermineTimeSheet = predetermineTimeSetForCalc.getTimeSheets(
				workType.getDailyWork().decisionNeedPredTime(),
				timeLeavingWork.getWorkNo().v());
		if(predetermineTimeSheet.isPresent()) {
			//遅刻時間の計算範囲
			Optional<TimeSpanForDailyCalc> lateCalcRange = LateDecisionClock.getCalcRange(
					predetermineTimeSheet.get(),
					timeLeavingWork,
					integrationOfWorkTime,
					predetermineTimeSetForCalc,
					workType.getDailyWork().decisionNeedPredTime());
			if(lateCalcRange.isPresent() && beforeLateEarlyTimeSheet.getStart().greaterThan(lateCalcRange.get().getStart())) {
				beforeLateEarlyTimeSheet = beforeLateEarlyTimeSheet.shiftOnlyStart(lateCalcRange.get().getStart());
			}
			
			//早退時間の計算範囲
			Optional<TimeSpanForDailyCalc> leaveEarlyCalcRange = LeaveEarlyDecisionClock.getCalcRange(
					predetermineTimeSheet.get(),
					timeLeavingWork,
					integrationOfWorkTime,
					predetermineTimeSetForCalc,
					workType.getDailyWork().decisionNeedPredTime());
			if(leaveEarlyCalcRange.isPresent() && beforeLateEarlyTimeSheet.getEnd().lessThan(leaveEarlyCalcRange.get().getEnd())) {
				beforeLateEarlyTimeSheet = beforeLateEarlyTimeSheet.shiftOnlyEnd(leaveEarlyCalcRange.get().getEnd());
			}
		}
		return beforeLateEarlyTimeSheet;
	}
	
	/**
	 * 控除時間帯の相殺時間を計算
	 * @param workNo 勤務NO
	 * @param timeVacationWork 時間休暇WORK
	 * @param companyholidayPriorityOrder 時間休暇相殺優先順位
	 */
	public void calcOffsetTimeOfDeductTimeSheet(
			int workNo,
			TimeVacationWork timeVacationWork,
			CompanyHolidayPriorityOrder companyholidayPriorityOrder){
		
		if (this.lateTimeSheet.isPresent()){
			Optional<TimevacationUseTimeOfDaily> lateOffsetOpt =
					timeVacationWork.getOffsetTimevacationForLate(new WorkNo(workNo));
			if (lateOffsetOpt.isPresent()){
				// 遅刻時間の休暇時間相殺
				this.lateTimeSheet.get().offsetVacationTime(
						DeductionAtr.Appropriate, companyholidayPriorityOrder, lateOffsetOpt.get());
			}
		}
		if (this.leaveEarlyTimeSheet.isPresent()){
			Optional<TimevacationUseTimeOfDaily> earlyOffsetOpt =
					timeVacationWork.getOffsetTimevacationForLeaveEarly(new WorkNo(workNo));
			if (earlyOffsetOpt.isPresent()){
				// 早退時間の休暇時間相殺
				this.leaveEarlyTimeSheet.get().offsetVacationTime(
						DeductionAtr.Appropriate, companyholidayPriorityOrder, earlyOffsetOpt.get());
			}
		}
		// 時間休暇外出
		Map<GoingOutReason, TimevacationUseTimeOfDaily> outing = new HashMap<>();
		outing.put(GoingOutReason.PRIVATE, timeVacationWork.getPrivateOuting());
		outing.put(GoingOutReason.UNION, timeVacationWork.getUnionOuting());
		
		this.recordedTimeSheet.stream()
			.filter(c -> c.getDeductionAtr().isGoOut())
			.filter(c -> c.getGoOutReason().isPresent() && c.getGoOutReason().get().isPrivateOrUnion())
			.forEach(c -> c.setDeductionOffsetTimeOfOuting(companyholidayPriorityOrder, outing));
	}
	
	/**
	 * 時間休暇相殺時間を取得
	 * @return 相殺時間
	 */
	public TimeVacationWork getTimeVacationOffsetTime(){
		
		// 私用外出相殺時間
		TimevacationUseTimeOfDaily privateOuting = TimevacationUseTimeOfDaily.defaultValue();
		List<TimevacationUseTimeOfDaily> addPrivateList = this.recordedTimeSheet.stream()
			.filter(c -> c.getDeductionAtr().isGoOut())
			.filter(c -> c.getGoOutReason().isPresent() && c.getGoOutReason().get().isPrivate())
			.filter(c -> c.getDeductionOffSetTime().isPresent())
			.map(c -> c.getDeductionOffSetTime().get()).collect(Collectors.toList());
		for (TimevacationUseTimeOfDaily addPrivate : addPrivateList) privateOuting = privateOuting.add(addPrivate);
		// 組合外出相殺時間
		TimevacationUseTimeOfDaily unionOuting = TimevacationUseTimeOfDaily.defaultValue();
		List<TimevacationUseTimeOfDaily> addUnionList = this.recordedTimeSheet.stream()
			.filter(c -> c.getDeductionAtr().isGoOut())
			.filter(c -> c.getGoOutReason().isPresent() && c.getGoOutReason().get().isUnion())
			.filter(c -> c.getDeductionOffSetTime().isPresent())
			.map(c -> c.getDeductionOffSetTime().get()).collect(Collectors.toList());
		for (TimevacationUseTimeOfDaily addUnion : addUnionList) unionOuting = unionOuting.add(addUnion);
		// 遅刻
		TimevacationUseTimeOfDaily late = TimevacationUseTimeOfDaily.defaultValue();
		if (this.lateTimeSheet.isPresent()){
			if (this.lateTimeSheet.get().getForRecordTimeSheet().isPresent()){
				if (this.lateTimeSheet.get().getForRecordTimeSheet().get().getDeductionOffSetTime().isPresent()){
					late = this.lateTimeSheet.get().getForRecordTimeSheet().get().getDeductionOffSetTime().get().clone();
				}
			}
		}
		// 早退
		TimevacationUseTimeOfDaily early = TimevacationUseTimeOfDaily.defaultValue();
		if (this.leaveEarlyTimeSheet.isPresent()){
			if (this.leaveEarlyTimeSheet.get().getForRecordTimeSheet().isPresent()){
				if (this.leaveEarlyTimeSheet.get().getForRecordTimeSheet().get().getDeductionOffSetTime().isPresent()){
					early = this.leaveEarlyTimeSheet.get().getForRecordTimeSheet().get().getDeductionOffSetTime().get().clone();
				}
			}
		}
		// 勤務NO毎の時間
		List<TimeVacationWorkEachNo> eachNo = new ArrayList<>();
		eachNo.add(TimeVacationWorkEachNo.of(this.workNo, late, early));
		
		return TimeVacationWork.of(eachNo, privateOuting, unionOuting);
	}

	/**
	 * 就業時間に加算する時間休暇相殺時間を取得
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @return 相殺時間
	 */
	public AttendanceTime getTimeVacationOffsetTimeForAddWorkTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet) {
		if(!integrationOfWorkTime.isPresent()) {
			return AttendanceTime.ZERO;
		}
		List<WithinWorkTimeFrame> frames = new ArrayList<>();
		frames.add(this);
		DeductionTimeSheet deductionTimeSheet = new DeductionTimeSheet(
				this.deductionTimeSheet,
				this.recordedTimeSheet,
				integrationOfDaily.getBreakTime(),
				integrationOfDaily.getOutingTime(), 
				integrationOfDaily.getShortTime().map(c -> c.getShortWorkingTimeSheets()).orElse(new ArrayList<>()));
		// 時間休暇加算時間を計算する
		return holidayAddtionSet.calcTimeVacationAddTime(
				integrationOfDaily,
				deductionTimeSheet,
				addSetting.getVacationCalcMethodSet(),
				frames,
				integrationOfWorkTime.get().getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm());
	}
	
	/**
	 * コアタイムとの重複を判断して時間帯を取得
	 * @param isWithin コア内外区分（true=コア内、false=コア外）
	 * @param coreTimeSpan コアタイム時間帯
	 * @return 就業時間内時間枠
	 */
	public List<WithinWorkTimeFrame> getFrameDuplicatedWithCoreTime(
			boolean isWithin, TimeSpanForCalc coreTimeSpan){
		
		List<WithinWorkTimeFrame> results = new ArrayList<>();
		
		// 重複を判断して時間帯を取得
		List<TimeSpanForCalc> targetSpanList = new ArrayList<>();
		if (isWithin){
			// コア内（重複している時間帯）
			Optional<TimeSpanForCalc> dupSpan = this.timeSheet.getTimeSpan().getDuplicatedWith(coreTimeSpan);
			if (dupSpan.isPresent()) targetSpanList.add(dupSpan.get());
		}
		else{
			// コア外（重複していない時間帯）
			targetSpanList = this.timeSheet.getTimeSpan().getNotDuplicationWith(coreTimeSpan);
		}
		// 就業時間帯時間枠リストに変換
		// ※　計算時間帯は抽象クラスのため、インスタンスとして返せないため、判断に使う就業時間内時間枠の必要な部分をクローンして返す。
		for (TimeSpanForCalc targetSpan : targetSpanList){
			WithinWorkTimeFrame result = WithinWorkTimeFrame.createEmpty(this.workingHoursTimeNo, this.workNo);
			result.timeSheet = new TimeSpanForDailyCalc(targetSpan);
			result.addDuplicatedDeductionTimeSheet(this.deductionTimeSheet, DeductionAtr.Deduction, Optional.empty());
			result.addDuplicatedDeductionTimeSheet(this.recordedTimeSheet, DeductionAtr.Appropriate, Optional.empty());
			results.add(result);
		}
		return results;
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 時間帯
	 * @param commonSet 就業時間帯の共通設定
	 * @return 就業時間内時間枠
	 */
	public Optional<WithinWorkTimeFrame> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, Optional<WorkTimezoneCommonSet> commonSet) {
		Optional<TimeSpanForDailyCalc> duplicate = this.timeSheet.getDuplicatedWith(timeSpan);
		if(!duplicate.isPresent()) {
			return Optional.empty();
		}
		WithinWorkTimeFrame recreated = new WithinWorkTimeFrame(
				new EmTimeFrameNo(this.workingHoursTimeNo.v().intValue()),
				new WorkNo(this.workNo.v()),
				duplicate.get(),
				this.beforeLateEarlyTimeSheet.getDuplicatedWith(duplicate.get()).orElse(duplicate.get()),
				this.rounding.clone(),
				this.recordedTimeSheet.stream().map(t->t.getAfterDeleteOffsetTime()).collect(Collectors.toList()),
				this.deductionTimeSheet.stream().map(t->t.getAfterDeleteOffsetTime()).collect(Collectors.toList()),
				this.getDuplicatedBonusPayNotStatic(this.bonusPayTimeSheet, duplicate.get()),
				this.midNightTimeSheet.getDuplicateRangeTimeSheet(duplicate.get()),
				this.getDuplicatedSpecBonusPayzNotStatic(this.specBonusPayTimesheet, duplicate.get()),
				this.lateTimeSheet.flatMap(l -> l.recreateWithDuplicate(duplicate.get(), this.getCloneDeductionTimeSheet(), commonSet.get())),
				this.leaveEarlyTimeSheet.flatMap(l -> l.recreateWithDuplicate(duplicate.get(), this.getCloneDeductionTimeSheet(), commonSet.get())));
		
		//所定内時間帯を重複する時間帯で作り直す
		recreated.premiumTimeSheetInPredetermined = recreated.premiumTimeSheetInPredetermined.flatMap(p -> p.recreateWithDuplicate(duplicate.get()));
		
		//控除時間帯の登録
		recreated.registDeductionListForWithin(this.getCloneDeductionTimeSheet(), commonSet);
		
		return Optional.of(recreated);
	}
}
