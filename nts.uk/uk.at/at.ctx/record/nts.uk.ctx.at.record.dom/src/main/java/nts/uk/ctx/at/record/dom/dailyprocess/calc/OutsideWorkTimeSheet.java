package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.record.dom.actualworkinghours.SubHolOccurrenceInfo;
import nts.uk.ctx.at.record.dom.daily.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.record.dom.daily.midnight.MidNightTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.withinstatutory.WithinWorkTimeSheet;
import nts.uk.ctx.at.record.dom.raisesalarytime.RaisingSalaryTime;
import nts.uk.ctx.at.record.dom.raisesalarytime.SpecificDateAttrOfDailyPerfor;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.common.DailyTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.ot.zerotime.ZeroTime;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkDeformedLaborAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkFlexAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkRegularAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.statutory.worktime.sharedNew.DailyUnit;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.overtime.StatutoryPrioritySet;
import nts.uk.ctx.at.shared.dom.workrule.statutoryworktime.DailyCalculationPersonalInformation;
import nts.uk.ctx.at.shared.dom.worktime.common.HDWorkTimeSheetSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.LegalOTSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.BreakDownTimeDay;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 就業時間外時間帯
 * 
 * @author keisuke_hoshina
 *
 */
@Getter
public class OutsideWorkTimeSheet {

	//残業時間帯
	private Optional<OverTimeSheet> overTimeWorkSheet;

	//休出時間帯
	private Optional<HolidayWorkTimeSheet> holidayWorkTimeSheet;

	/**
	 * Constructor 
	 */
	public OutsideWorkTimeSheet(Optional<OverTimeSheet> overTimeWorkSheet,
			Optional<HolidayWorkTimeSheet> holidayWorkTimeSheet) {
		super();
		this.overTimeWorkSheet = overTimeWorkSheet;
		this.holidayWorkTimeSheet = holidayWorkTimeSheet;
	}
	
	/**
	 * 就業時間外時間帯を作成する
	 * 
	 * @param overTimeHourSetList
	 * @param fixOff
	 * @param attendanceLeave
	 * @param workNo
	 * @param overDayEndCalcSet
	 * @param overDayEndSet
	 * @param holidayTimeWorkItem
	 * @param beforeDay
	 * @param toDay
	 * @param afterDay
	 * @param workTime
	 * @param workingSystem
	 * @param breakdownTimeDay
	 * @param dailyTime
	 * @param autoCalculationSet
	 * @param statutorySet
	 * @param prioritySet
	 * @param createWithinWorkTimeSheet 
	 * @param workTimeDailyAtr 
	 * @param integrationOfDaily 
	 * @return
	 */
	public static OutsideWorkTimeSheet createOutsideWorkTimeSheet(List<OverTimeOfTimeZoneSet> overTimeHourSetList,
			List<HDWorkTimeSheetSetting> fixOff, TimeLeavingWork attendanceLeave, int workNo, Optional<ZeroTime> overDayEndCalcSet,
			WorkTimezoneCommonSet overDayEndSet, List<HolidayWorkFrameTimeSheet> holidayTimeWorkItem, WorkType beforeDay,
			WorkType toDay, WorkType afterDay, WorkTimeSetting workTime, WorkingSystem workingSystem,
			BreakDownTimeDay breakdownTimeDay, DailyTime dailyTime, AutoCalOvertimeSetting autoCalculationSet,
			LegalOTSetting statutorySet, StatutoryPrioritySet prioritySet,Optional<BonusPaySetting> bonuspaySetting,MidNightTimeSheet midNightTimeSheet,
			DailyCalculationPersonalInformation personalInfo,DeductionTimeSheet deductionTimeSheet,DailyUnit dailyUnit,HolidayCalcMethodSet holidayCalcMethodSet, WithinWorkTimeSheet createWithinWorkTimeSheet,
    		VacationClass vacationClass, AttendanceTime timevacationUseTimeOfDaily,
    		PredetermineTimeSetForCalc predetermineTimeSet, Optional<WorkTimeCode> siftCode, 
    		AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
    		AddSetting addSetting,
    		HolidayAddtionSet holidayAddtionSet,WorkingConditionItem conditionItem,
    		Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,Optional<CoreTimeSetting> coreTimeSetting,
    		Optional<WorkInformation> beforeInfo, Optional<WorkInformation> afterInfo,Optional<SpecificDateAttrOfDailyPerfor> specificDateAttrSheets, WorkTimeDailyAtr workTimeDailyAtr) {
		
		List<HolidayWorkFrameTimeSheetForCalc> holidayWorkFrameTimeSheetForCalc = new ArrayList<>();
		List<OverTimeFrameTimeSheetForCalc> overTimeWorkFrameTimeSheet = new ArrayList<>();
		if (toDay.isWeekDayAttendance()) {
			/* 就業時間外時間帯の平日出勤の処理 */
			overTimeWorkFrameTimeSheet = OverTimeFrameTimeSheetForCalc.createOverWorkFrame(
					overTimeHourSetList, workingSystem, attendanceLeave, workNo, breakdownTimeDay, dailyTime,
					autoCalculationSet, statutorySet, prioritySet,bonuspaySetting,midNightTimeSheet,
					personalInfo,true,deductionTimeSheet,dailyUnit,holidayCalcMethodSet,createWithinWorkTimeSheet, 
					vacationClass, timevacationUseTimeOfDaily, toDay,
					predetermineTimeSet, siftCode, autoCalcOfLeaveEarlySetting, addSetting, holidayAddtionSet,Optional.of(overDayEndSet),conditionItem,
					predetermineTimeSetByPersonInfo,coreTimeSetting, specificDateAttrSheets,workTimeDailyAtr);

			/* 0時跨ぎ処理 */
			if(overDayEndCalcSet.isPresent()) {
				OverDayEnd overTimeDayEnd = OverDayEnd.forOverTime(overDayEndSet.isZeroHStraddCalculateSet(), overTimeWorkFrameTimeSheet, beforeDay, toDay,afterDay, beforeInfo,afterInfo,overDayEndCalcSet.get());
				overTimeWorkFrameTimeSheet = overTimeDayEnd.getOverTimeList();
				holidayWorkFrameTimeSheetForCalc = overTimeDayEnd.getHolList();
			}
		} else {
			holidayWorkFrameTimeSheetForCalc = HolidayWorkFrameTimeSheetForCalc.createHolidayTimeWorkFrame(attendanceLeave,fixOff,toDay,bonuspaySetting,midNightTimeSheet,deductionTimeSheet,Optional.of(overDayEndSet), specificDateAttrSheets);

			/* 0時跨ぎ */
			if(overDayEndCalcSet.isPresent()) {
				OverDayEnd holidayWorkDayEnd = OverDayEnd.forHolidayWorkTime(overDayEndSet.isZeroHStraddCalculateSet(), holidayWorkFrameTimeSheetForCalc, beforeDay, toDay,afterDay,beforeInfo,afterInfo,overDayEndCalcSet.get());
				overTimeWorkFrameTimeSheet = holidayWorkDayEnd.getOverTimeList();
				holidayWorkFrameTimeSheetForCalc = holidayWorkDayEnd.getHolList();
			}

		}
		return new OutsideWorkTimeSheet(
				   Optional.of(new OverTimeSheet(new RaisingSalaryTime(),
						   						 overTimeWorkFrameTimeSheet,
						   						new SubHolOccurrenceInfo()
						   						 ))
				   ,
				   Optional.of(new HolidayWorkTimeSheet(new RaisingSalaryTime(),
						   								holidayWorkFrameTimeSheetForCalc, 
						   								new SubHolOccurrenceInfo()))
				   );
	}

	
	/**
	 * 残業時間の中にある控除時間を算出する
	 * @param dedAtr
	 * @param atr
	 * @return 控除時間
	 */
	public AttendanceTime caluclationAllOverTimeFrameTime(DeductionAtr dedAtr,ConditionAtr atr) {
		if(this.overTimeWorkSheet.isPresent()) {
			return this.overTimeWorkSheet.get().calculationAllFrameDeductionTime(dedAtr,atr);
		}
		return new AttendanceTime(0);
	}
	
	/**
	 * 休出時間の中にある控除時間を算出する
	 * @param dedAtr
	 * @param atr
	 * @return　控除時間
	 */
	public AttendanceTime caluclationAllHolidayFrameTime(DeductionAtr dedAtr,ConditionAtr atr) {
		if(this.holidayWorkTimeSheet.isPresent()) {
			return this.holidayWorkTimeSheet.get().calculationAllFrameDeductionTime(dedAtr,atr);
		}
		return new AttendanceTime(0);
	}
	
	/**
	 * 流動勤務の残業として就業時間外時間帯を作成する
	 * @param personalInfo 日別計算用の個人情報
	 * @param flowWorkSetting 流動勤務設定
	 * @param predetermineTimeSetForCalc 所定時間設定(計算用クラス)
	 * @param timeSheetOfDeductionItems 控除項目の時間帯
	 * @param calcRange 残業開始終了時刻
	 * @param bonuspaySetting 加給設定
	 * @param integrationOfDaily 日別実績(Work)
	 * @param midNightTimeSheet 深夜時間帯
	 * @param addSetting 加算設定
	 * @param timeVacationAdditionRemainingTime 休暇使用合計残時間未割当
	 * @param zeroTime 0時跨ぎ計算設定
	 * @param todayWorkType 勤務種類（当日）
	 * @param previousWorkType 勤務種類（前日）
	 * @param nextWorkType 勤務種類（翌日）
	 * @param withinWorkTimeSheet 就業時間内時間帯
	 * @param personCommonSetting 毎日変更の可能性のあるマスタ管理クラス
	 * @param vacation 休暇クラス
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @return 残業時間帯
	 */
	public static OutsideWorkTimeSheet createOverTimeAsFlow(
			DailyCalculationPersonalInformation personalInfo,
			FlowWorkSetting flowWorkSetting,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			List<TimeSheetOfDeductionItem> timeSheetOfDeductionItems,
			TimeSpanForDailyCalc calcRange,
			Optional<BonusPaySetting> bonuspaySetting,
			IntegrationOfDaily integrationOfDaily,
			MidNightTimeSheet midNightTimeSheet,
			AddSetting addSetting,
			AttendanceTime timeVacationAdditionRemainingTime,
			ZeroTime zeroTime,
			WorkType todayWorkType,
			WorkType previousWorkType,
			WorkType nextWorkType,
			//共通処理呼ぶ用
			Optional<WorkInformation> previousInfo,
			Optional<WorkInformation> nextInfo,
			WithinWorkTimeSheet withinWorkTimeSheet,
			ManagePerPersonDailySet personCommonSetting,
			VacationClass vacation,
			HolidayAddtionSet holidayAddtionSet) {
		
		OverTimeSheet overTimeSheet = OverTimeSheet.createAsFlow(
				personalInfo,
				flowWorkSetting,
				predetermineTimeSetForCalc,
				timeSheetOfDeductionItems,
				calcRange,
				bonuspaySetting,
				integrationOfDaily,
				midNightTimeSheet,
				addSetting,
				timeVacationAdditionRemainingTime,
				todayWorkType,
				//共通処理呼ぶ用
				withinWorkTimeSheet,
				personCommonSetting,
				vacation,
				holidayAddtionSet);
		
		//0時跨ぎの時間帯分割
		OverDayEnd overDayEnd = OverDayEnd.forOverTime(
				flowWorkSetting.getCommonSetting().isZeroHStraddCalculateSet(),
				overTimeSheet.getFrameTimeSheets(),
				previousWorkType,
				todayWorkType,
				nextWorkType,
				previousInfo,
				nextInfo,
				zeroTime);
		
		return new OutsideWorkTimeSheet(
				Optional.of(new OverTimeSheet(new RaisingSalaryTime(), overDayEnd.getOverTimeList(), new SubHolOccurrenceInfo())),
				Optional.of(new HolidayWorkTimeSheet(new RaisingSalaryTime(), overDayEnd.getHolList(), new SubHolOccurrenceInfo())));
	}
	
	/**
	 * 流動勤務の休日出勤として就業時間外時間帯を作成する
	 * @param todayWorkType 当日の勤務種類
	 * @param flowWorkSetting 流動勤務設定
	 * @param timeSheetOfDeductionItems 控除項目の時間帯(List)
	 * @param holidayStartEnd 休出開始終了時刻（出退勤時間帯）
	 * @param bonuspaySetting 加給設定
	 * @param integrationOfDaily 日別実績(Work)
	 * @param midNightTimeSheet 深夜時間帯
	 * @param zeroTime 0時跨ぎ計算設定
	 * @param previousWorkType 前日の勤務種類
	 * @param nextWorkType 翌日の勤務種類
	 * @param previousInfo 前日の勤務情報
	 * @param nextInfo 翌日の勤務情報
	 * @param oneDayOfRange 1日の計算範囲
	 * @return 休日出勤時間帯
	 */
	public static OutsideWorkTimeSheet createHolidayAsFlow(
			WorkType todayWorkType,
			FlowWorkSetting flowWorkSetting,
			List<TimeSheetOfDeductionItem> timeSheetOfDeductionItems,
			TimeSpanForDailyCalc holidayStartEnd,
			Optional<BonusPaySetting> bonuspaySetting,
			IntegrationOfDaily integrationOfDaily,
			MidNightTimeSheet midNightTimeSheet,
			ZeroTime zeroTime,
			WorkType previousWorkType,
			WorkType nextWorkType,
			Optional<WorkInformation> previousInfo,
			Optional<WorkInformation> nextInfo,
			TimeSpanForDailyCalc oneDayOfRange) {
		
		HolidayWorkTimeSheet hollidayWorkTImeSheet = HolidayWorkTimeSheet.createAsFlow(
				todayWorkType,
				flowWorkSetting,
				timeSheetOfDeductionItems,
				holidayStartEnd,
				bonuspaySetting,
				integrationOfDaily,
				midNightTimeSheet,
				oneDayOfRange);
		
		//0時跨ぎ処理
		OverDayEnd overDayEnd = OverDayEnd.forHolidayWorkTime(
				flowWorkSetting.getCommonSetting().isZeroHStraddCalculateSet(),
				hollidayWorkTImeSheet.getWorkHolidayTime(),
				previousWorkType,
				todayWorkType,
				nextWorkType,
				previousInfo,
				nextInfo,
				zeroTime);
		
		return new OutsideWorkTimeSheet(
				Optional.of(new OverTimeSheet(new RaisingSalaryTime(), overDayEnd.getOverTimeList(), new SubHolOccurrenceInfo())),
				Optional.of(new HolidayWorkTimeSheet(new RaisingSalaryTime(), overDayEnd.getHolList(), new SubHolOccurrenceInfo())));
	}
}
