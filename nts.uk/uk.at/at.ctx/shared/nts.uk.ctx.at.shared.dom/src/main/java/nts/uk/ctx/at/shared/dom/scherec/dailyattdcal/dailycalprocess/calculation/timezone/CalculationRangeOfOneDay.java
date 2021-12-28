package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.FlexWithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PreviousAndNextDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.SchedulePerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationWorkEachNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.BreakClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.ShortTimeWorkSheetWithoutWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.WorkingBreakTimeAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.LateTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeFrame;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FixedChangeAtr;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowCalculateSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowOTSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.PrePlanWorkTimeCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 1日の計算範囲
 * 
 * @author keisuke_hoshina
 *
 */
@AllArgsConstructor
@Getter
public class CalculationRangeOfOneDay {

	//1日の範囲
	private TimeSpanForDailyCalc oneDayOfRange;

	//勤務情報
	private WorkInfoOfDailyAttendance workInformationOfDaily;
	
	@Setter
	//出退勤
	private TimeLeavingOfDailyAttd attendanceLeavingWork;

	//所定時間設定
	private PredetermineTimeSetForCalc predetermineTimeSetForCalc;
	
	/*----------------Optional-----------------------*/
	//インターバル制度管理
	
	//非勤務時間帯
	private Optional<NonWorkingTimeSheet> nonWorkingTimeSheet;
	
	//勤務外短時間勤務時間帯
	private Optional<ShortTimeWorkSheetWithoutWork> shortTimeWSWithoutWork = Optional.empty();
	/*----------------------Finally------------------------*/
	//加給時間
	
	//就業時間内時間帯
	private Finally<WithinWorkTimeSheet> withinWorkingTimeSheet = Finally.empty();

	@Setter
	//就業時間外時間帯
	private Finally<OutsideWorkTimeSheet> outsideWorkTimeSheet = Finally.empty();

	//出勤前時間
	private Finally<AttendanceTime> beforeAttendance = Finally.of(new AttendanceTime(0)); 
	
	//退勤後時間
	private Finally<AttendanceTime> afterLeaving = Finally.of(new AttendanceTime(0));

	public CalculationRangeOfOneDay(Finally<WithinWorkTimeSheet> withinWorkingTimeSheet,
			Finally<OutsideWorkTimeSheet> outsideWorkTimeSheet, TimeSpanForDailyCalc oneDayOfRange,
			TimeLeavingOfDailyAttd attendanceLeavingWork, PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			WorkInfoOfDailyAttendance workInformationofDaily,
			Optional<NonWorkingTimeSheet> nonWorkingTimeSheet) {
		this.withinWorkingTimeSheet = withinWorkingTimeSheet;
		this.outsideWorkTimeSheet = outsideWorkTimeSheet;
		this.oneDayOfRange = oneDayOfRange;
		this.attendanceLeavingWork = attendanceLeavingWork;
		this.predetermineTimeSetForCalc = predetermineTimeSetForCalc;
		this.workInformationOfDaily = workInformationofDaily;
		this.nonWorkingTimeSheet = nonWorkingTimeSheet;
	}
	
	public static CalculationRangeOfOneDay createEmpty(IntegrationOfDaily integrationOfDaily) {
		return new CalculationRangeOfOneDay(
				Finally.of(new FlexWithinWorkTimeSheet(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
								Collections.emptyList(), Optional.empty())),
				Finally.of(new OutsideWorkTimeSheet(Optional.empty(),Optional.empty())),
				null,
				integrationOfDaily.getAttendanceLeave().orElse(null),
				null,
				integrationOfDaily.getWorkInformation(),
				Optional.empty()
		);
	}
	
	/**
	 * 就業時間帯の作成
	 * アルゴリズム：固定勤務の時間帯作成
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 当日の勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param previousAndNextDaily 前日と翌日の勤務
	 */
	public void createWithinWorkTimeSheet(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			PreviousAndNextDaily previousAndNextDaily) {
		
		/* 控除時間帯の作成 */
		DeductionTimeSheet deductionTimeSheet = DeductionTimeSheet.createDeductionTimeForCalc(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				this.oneDayOfRange,
				this.attendanceLeavingWork,
				Optional.empty(),
				companyCommonSetting,
				personDailySetting);
		
		this.theDayOfWorkTimesLoop(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				deductionTimeSheet,
				previousAndNextDaily);
	}

	/**
	 * 時間帯作成(勤務回数分ループ) 就業時間内・外作成処理
	 * アルゴリズム：時間帯の作成
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別勤怠(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param previousAndNextDaily 前日と翌日の勤務
	 */
	public void theDayOfWorkTimesLoop(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			PreviousAndNextDaily previousAndNextDaily) {
		
		if (personDailySetting.getPersonInfo().getLaborSystem().isExcludedWorkingCalculate()) {
			/* 計算対象外 */
			return;
		}
		
		// 時間休暇WORKを作成する
		TimeVacationWork timeVacationWork = this.createTimeVacationWork(integrationOfDaily);
		
		/*attendanceLeavingWork は、ジャスト遅刻早退の補正処理を行った結果をCalculationRangeOneDayが持っているので、
		 * intergrationOfDailyではなく、rangeOneDayが持っている出退勤を使う事。
		 * */
		this.attendanceLeavingWork.getTimeLeavingWorks().forEach(timeLeavingWork -> {
			
			/* 就業時間内時間帯作成 */
			//打刻はある前提で動く
			val createWithinWorkTimeSheet = WithinWorkTimeSheet.createAsFixed(
					companyCommonSetting,
					personDailySetting,
					todayWorkType,
					integrationOfWorkTime,
					integrationOfDaily,
					deductionTimeSheet,
					this.predetermineTimeSetForCalc,
					timeLeavingWork,
					timeVacationWork);
			if(this.withinWorkingTimeSheet.isPresent()) {
				this.withinWorkingTimeSheet.get().getWithinWorkTimeFrame().addAll(createWithinWorkTimeSheet.getWithinWorkTimeFrame());
			}
			else {
				this.withinWorkingTimeSheet.set(createWithinWorkTimeSheet);
			}
			/* 就業時間外時間帯作成 */
			//打刻はある前提で動く
			val createOutSideWorkTimeSheet = OutsideWorkTimeSheet.createOutsideWorkTimeSheet(
					companyCommonSetting,
					personDailySetting,
					todayWorkType,
					integrationOfWorkTime,
					integrationOfDaily,
					deductionTimeSheet,
					this.predetermineTimeSetForCalc,
					timeLeavingWork,
					previousAndNextDaily,
					createWithinWorkTimeSheet);
			if(!outsideWorkTimeSheet.isPresent()) {
				this.outsideWorkTimeSheet = Finally.of(createOutSideWorkTimeSheet);
			}
			else {
				//残業
				if(outsideWorkTimeSheet.get().getOverTimeWorkSheet().isPresent()) {
					List<OverTimeFrameTimeSheetForCalc> addOverList = createOutSideWorkTimeSheet.getOverTimeWorkSheet().isPresent()? createOutSideWorkTimeSheet.getOverTimeWorkSheet().get().getFrameTimeSheets():Collections.emptyList();
					outsideWorkTimeSheet.get().getOverTimeWorkSheet().get().getFrameTimeSheets().addAll(addOverList);
				}
				else {
					this.outsideWorkTimeSheet = Finally.of(new OutsideWorkTimeSheet(createOutSideWorkTimeSheet.getOverTimeWorkSheet(),this.outsideWorkTimeSheet.get().getHolidayWorkTimeSheet()));
				}
				//休出
				if(outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().isPresent()) {
					List<HolidayWorkFrameTimeSheetForCalc> addHolList = createOutSideWorkTimeSheet.getHolidayWorkTimeSheet().isPresent()? createOutSideWorkTimeSheet.getHolidayWorkTimeSheet().get().getWorkHolidayTime():Collections.emptyList();
					outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().get().getWorkHolidayTime().addAll(addHolList);
				}
				else {
					this.outsideWorkTimeSheet = Finally.of(new OutsideWorkTimeSheet(this.outsideWorkTimeSheet.get().getOverTimeWorkSheet(),createOutSideWorkTimeSheet.getHolidayWorkTimeSheet()));
				}
			}
		});
		
		if(!this.withinWorkingTimeSheet.isPresent()) {
			this.withinWorkingTimeSheet = Finally.of(new WithinWorkTimeSheet(Arrays.asList(new WithinWorkTimeFrame(new EmTimeFrameNo(5), 
																									new WorkNo(3),
																									new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(0)), 
																									new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(0)), 
																									new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN), 
																									Collections.emptyList(), 
																									Collections.emptyList(), 
																									Collections.emptyList(), 
																									MidNightTimeSheetForCalcList.createEmpty(), 
																									Collections.emptyList(), 
																									Optional.empty(), 
																									Optional.empty())),
																			Collections.emptyList(),
																			Optional.empty(),
																			Optional.empty()));
		}
	}

	/**
	 * 加給時間を計算する(就内・残業・休出時間帯)
	 * アルゴリズム：加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 加給時間(List)
	 */
	public List<BonusPayTime> calcBonusPayTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		List<BonusPayTime> overTimeBonusPay = new ArrayList<>();
		List<BonusPayTime> holidayWorkBonusPay = new ArrayList<>();
		List<BonusPayTime> withinBonusPay = new ArrayList<>();
		if(this.withinWorkingTimeSheet != null && withinWorkingTimeSheet.isPresent())
			withinBonusPay = withinWorkingTimeSheet.get().calcBonusPayTimeInWithinWorkTime(bpTimeItemSets, calcAtrOfDaily);
		
		if(this.outsideWorkTimeSheet != null && this.outsideWorkTimeSheet.isPresent())
		{
			if(this.outsideWorkTimeSheet.get().getOverTimeWorkSheet().isPresent()) { 
				overTimeBonusPay = outsideWorkTimeSheet.get().getOverTimeWorkSheet().get().calcBonusPayTimeInOverWorkTime(bpTimeItemSets, calcAtrOfDaily);
			}
			
			if(this.outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().isPresent()) {
				holidayWorkBonusPay = outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().get().calcBonusPayTimeInHolidayWorkTime(bpTimeItemSets, calcAtrOfDaily);
			}
		}
		return calcBonusPayTime(withinBonusPay,overTimeBonusPay,holidayWorkBonusPay);
	}
	
	/**
	 * 特定加給時間を計算する(就内・残業・休出時間帯)
	 * アルゴリズム：加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 特定加給時間(List)
	 */
	public List<BonusPayTime> calcSpecBonusPayTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> overTimeBonusPay = new ArrayList<>();
		List<BonusPayTime> holidayWorkBonusPay = new ArrayList<>();
		List<BonusPayTime> withinBonusPay = new ArrayList<>();
		
		if(withinWorkingTimeSheet.isPresent())
			 withinBonusPay = withinWorkingTimeSheet.get().calcSpecifiedBonusPayTimeInWithinWorkTime(bpTimeItemSets, calcAtrOfDaily);

		if(outsideWorkTimeSheet.isPresent())
		{
			if(outsideWorkTimeSheet.get().getOverTimeWorkSheet().isPresent()) { 
				overTimeBonusPay = outsideWorkTimeSheet.get().getOverTimeWorkSheet().get().calcSpecBonusPayTimeInOverWorkTime(bpTimeItemSets, calcAtrOfDaily);
			}
			
			if(outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().isPresent()) {
				holidayWorkBonusPay = outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().get().calcSpecBonusPayTimeInHolidayWorkTime(bpTimeItemSets, calcAtrOfDaily);
			}
		}
		return calcBonusPayTime(withinBonusPay,overTimeBonusPay,holidayWorkBonusPay);
	}
	
	/**
	 * 就・残�?休�?�?給時間を合計す�?
	 * @param withinBonusPay
	 * @param overTimeBonusPay
	 * @param holidayWorkBonusPay
	 * @return�?合計後�?�?算時�?(Noでユニ�?ク)
	 */
	private List<BonusPayTime> calcBonusPayTime(List<BonusPayTime> withinBonusPay ,
								   List<BonusPayTime> overTimeBonusPay ,
								   List<BonusPayTime> holidayWorkBonusPay){
		List<BonusPayTime> returnList = new ArrayList<>();
		
		overTimeBonusPay.addAll(holidayWorkBonusPay);
		val excessPayTime = overTimeBonusPay;
		for(int bonusPayNo = 1 ; bonusPayNo <= 10 ; bonusPayNo++) {
			returnList.add(addWithinAndExcessBonusTime(sumBonusPayTime(getByBonusPayNo(withinBonusPay,bonusPayNo),bonusPayNo),
													   sumBonusPayTime(getByBonusPayNo(excessPayTime,bonusPayNo),bonusPayNo)
													   ,bonusPayNo));
		}
		return returnList;
	}
	
	/**
	 * 受け取っ�?2つの�?給時間が持つ時間を合�?
	 * @param within
	 * @param excess
	 * @param bonusPayNo
	 * @return
	 */
	private BonusPayTime addWithinAndExcessBonusTime(BonusPayTime within,BonusPayTime excess,int bonusPayNo) {
		return new BonusPayTime(bonusPayNo,
								within.getBonusPayTime().addMinutes(excess.getBonusPayTime().valueAsMinutes()),
								within.getWithinBonusPay().addMinutes(excess.getWithinBonusPay().getTime(), 
																	  excess.getWithinBonusPay().getCalcTime()),
								within.getExcessBonusPayTime().addMinutes(excess.getExcessBonusPayTime().getTime(), 
										  								  excess.getExcessBonusPayTime().getCalcTime())
								);
	}
	
	/**
	 * �?��間�?合計を算�?
	 * @param bonusPayList�?�?給時間のリス�?
	 * @param bonusPayNo�?�?給時間?�??
	 * @return�?合計時間�?�?給時間
	 */
	private BonusPayTime sumBonusPayTime(List<BonusPayTime> bonusPayList, int bonusPayNo) {
		AttendanceTime bonusPayTime =  new AttendanceTime(bonusPayList.stream().map(tc -> tc.getBonusPayTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
		AttendanceTime withinTime = new AttendanceTime(bonusPayList.stream().map(tc -> tc.getWithinBonusPay().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
		AttendanceTime withinCalcTime = new AttendanceTime(bonusPayList.stream().map(tc -> tc.getWithinBonusPay().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
		AttendanceTime excessTime = new AttendanceTime(bonusPayList.stream().map(tc -> tc.getExcessBonusPayTime().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
		AttendanceTime excessCalcTime = new AttendanceTime(bonusPayList.stream().map(tc -> tc.getExcessBonusPayTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
		
		return new BonusPayTime(bonusPayNo,
								bonusPayTime,
								TimeWithCalculation.createTimeWithCalculation(withinTime, withinCalcTime),
								TimeWithCalculation.createTimeWithCalculation(excessTime, excessCalcTime));
	}
	
	/**
	 * 受け取った加給時間?�?�を持つ�?給時間を取�?
	 * @param bonusPayTime �?給時間
	 * @param bonusPayNo�?�?給時間?�??
	 * @return�?�?給時間リス�?
	 */
	private List<BonusPayTime> getByBonusPayNo(List<BonusPayTime> bonusPayTime,int bonusPayNo){
		return bonusPayTime.stream().filter(tc -> tc.getBonusPayTimeItemNo() == bonusPayNo).collect(Collectors.toList());
	}
	
	/**
	 * 控除時間を取得
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @param statutoryAtr 法定内区分
	 * @param roundAtr 丸め区分(時間帯で丸めるかの区分)
	 * @param sumRoundSet 合算丸め設定
	 * @return 控除時間
	 */
	public TimeWithCalculation getDeductionTime(
			ConditionAtr conditionAtr,
			DeductionAtr dedAtr,
			StatutoryAtr statutoryAtr,
			TimeSheetRoundingAtr roundAtr,
			Optional<TimeRoundingSetting> sumRoundSet,
			NotUseAtr canOffset) {
		
		int deductMinutes = 0;		// 控除時間
		// 法定内
		if (statutoryAtr.isStatutory()) {
			if (this.withinWorkingTimeSheet.isPresent()) {
				// 就業時間帯から控除時間を取得
				deductMinutes += this.withinWorkingTimeSheet.get()
						.getDeductionTime(conditionAtr, dedAtr, roundAtr, canOffset).valueAsMinutes();
			}
		}
		// 法定外
		else if (statutoryAtr.isExcess()) {
			if (this.outsideWorkTimeSheet.isPresent()) {
				// 残業時間帯から控除時間を取得
				deductMinutes += this.outsideWorkTimeSheet.get()
						.getDeductionTimeFromOverTime(conditionAtr, dedAtr, roundAtr, canOffset).valueAsMinutes();
				// 休出時間帯から控除時間を取得
				deductMinutes += this.outsideWorkTimeSheet.get()
						.getDeductionTimeFromHolidayWork(conditionAtr, dedAtr, roundAtr, canOffset).valueAsMinutes();
			}
		}
		// 丸め区分を取得
		if (roundAtr == TimeSheetRoundingAtr.ALL){
			// 丸め処理（合算丸め区分を使用）
			if (sumRoundSet.isPresent()){
				deductMinutes = sumRoundSet.get().round(deductMinutes);
			}
		}
		// 控除時間を返す
		return TimeWithCalculation.sameTime(new AttendanceTime(deductMinutes));
	}

	/**
	 * 休憩回数の計算
	 * @return 休憩回数
	 */
	public int calcBreakCount(){
		
		int breakCount = 0;		// 合計休憩回数
		// 控除回数の計算（就業時間内時間帯）
		if (this.withinWorkingTimeSheet.isPresent()){
			breakCount += this.withinWorkingTimeSheet.get()
					.calcDeductionCount(ConditionAtr.BREAK, DeductionAtr.Appropriate);
		}
		if (this.outsideWorkTimeSheet.isPresent()){
			// 控除回数の計算（残業時間帯）
			breakCount += this.outsideWorkTimeSheet.get()
					.calcDeductionCountFromOverTime(ConditionAtr.BREAK, DeductionAtr.Appropriate);
			// 控除回数の計算（休出時間帯）
			breakCount += this.outsideWorkTimeSheet.get()
					.calcDeductionCountFromHolidayWork(ConditionAtr.BREAK, DeductionAtr.Appropriate);
		}
		// 合計休憩回数を返す
		return breakCount;
	}

	/**
	 * フレックス勤務の時間帯作成
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting  社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param previousAndNextDaily 前日と翌日の勤務
	 */
	public void createTimeSheetAsFlex(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			PreviousAndNextDaily previousAndNextDaily){
		
		//控除時間帯の作成
		DeductionTimeSheet deductionTimeSheet = DeductionTimeSheet.createDeductionTimeForCalc(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				this.oneDayOfRange,
				this.attendanceLeavingWork,
				Optional.empty(),
				companyCommonSetting,
				personDailySetting);
		
		theDayOfWorkTimesLoop(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				deductionTimeSheet,
				previousAndNextDaily);
		
		/*コアタイムのセット*/
		if(this.withinWorkingTimeSheet.isPresent())
			this.withinWorkingTimeSheet = Finally.of(
					withinWorkingTimeSheet.get().createWithinFlexTimeSheet(integrationOfWorkTime.getFlexWorkSetting().get().getCoreTimeSetting(), deductionTimeSheet));
	}

	/**
	 * 控除時間帯の仮確定
	 * アルゴリズム：計上用と控除用の控除時間帯の取得
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param predetermineTimeSetForCalc 所定時間設定(計算用クラス)
	 * @param lateTimeSheet 遅刻時間帯(List)
	 * @param correctWithEndTime 退勤が含まれているかどうか
	 * @param betweenWorkTimeSheets 勤務間
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting  社員設定管理
	 * @return 控除時間帯
	 */
	public DeductionTimeSheet provisionalDeterminationOfDeductionTimeSheet(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			List<LateTimeSheet> lateTimeSheet,
			boolean correctWithEndTime,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		//控除用
		val dedTimeSheet = DeductionTimeSheet.provisionalDecisionOfDeductionTimeSheet(
				DeductionAtr.Deduction,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				oneDayOfRange,
				attendanceLeaveWork,
				predetermineTimeSetForCalc, lateTimeSheet, 
				this, correctWithEndTime, betweenWorkTimeSheets,
				companyCommonSetting,
				personCommonSetting);
		
		//計上用
		val recordTimeSheet = DeductionTimeSheet.provisionalDecisionOfDeductionTimeSheet(
				DeductionAtr.Appropriate,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				oneDayOfRange,
				attendanceLeaveWork,
				predetermineTimeSetForCalc, lateTimeSheet, 
				this, correctWithEndTime, betweenWorkTimeSheets,
				companyCommonSetting,
				personCommonSetting);
	
		return new DeductionTimeSheet(
				dedTimeSheet,
				recordTimeSheet,
				integrationOfDaily.getBreakTime(),
				integrationOfDaily.getOutingTime(),
				integrationOfDaily.getShortTime().isPresent()
						? integrationOfDaily.getShortTime().get().getShortWorkingTimeSheets()
						: Collections.emptyList()); 
	}
	
	/**
	 * 大塚モード使用時専用の遅刻、早退削除処理
	 */
	public void cleanLateLeaveEarlyTimeForOOtsuka() {
		if(this.getWithinWorkingTimeSheet() != null
			&& this.getWithinWorkingTimeSheet().isPresent()){
			this.withinWorkingTimeSheet.get().cleanLateLeaveEarlyTimeForOOtsuka();
		}
	}
	
	/**
	 * 流動勤務の時間帯作成
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 当日の勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param previousAndNextDaily 前日と翌日の勤務
	 * @param schedulePerformance 予定実績
	 */
	public void createFlowWork(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			PreviousAndNextDaily previousAndNextDaily,
			Optional<SchedulePerformance> schedulePerformance) {
		
		if (todayWorkType.getDecisionAttendanceHolidayAttr()) {
			//1日休日は就内就外を空で作成する
			this.withinWorkingTimeSheet.set(WithinWorkTimeSheet.createEmpty());
			this.outsideWorkTimeSheet.set(OutsideWorkTimeSheet.createEmpty());
			return;
		}
		
		//出勤時刻を予定開始時刻にする
		this.setScheduleStartTimeForFlow(
				integrationOfDaily.getWorkInformation(),
				integrationOfWorkTime.getFlowWorkSetting().get().getFlowSetting().getCalculateSetting());
		
		//空の就業時間内時間帯を作成。これを遅刻早退と就内の処理で編集していく。
		WithinWorkTimeSheet creatingWithinWorkTimeSheet = new WithinWorkTimeSheet(new ArrayList<>(), new ArrayList<>(), Optional.empty(), Optional.empty());
		
		//事前に遅刻早退、控除時間帯を取得する
		DeductionTimeSheet deductTimeSheet = this.prePocessForFlow(//設計上は控除項目の時間帯を返すが、就内で呼ぶ共通処理で必要な為、控除時間帯を返している。
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				creatingWithinWorkTimeSheet);
		//現状integrationOfDailyの出退勤を使用している箇所がある為、自身が持つ出退勤で上書き。いずれ統一させる必要がある。
		integrationOfDaily.setAttendanceLeave(Optional.of(this.attendanceLeavingWork));
		
		if(todayWorkType.isWeekDayAttendance()) {
			
			//所定時間設定をコピーして計算用の所定時間設定を作成する → 既に持っている
			
			//所定時間帯、残業開始を補正する
			this.fluctuationPredeterminedForFlow(
					companyCommonSetting,
					personDailySetting,
					todayWorkType,
					integrationOfDaily,
					integrationOfWorkTime.getFlowWorkSetting().get(),
					schedulePerformance);
			
			//時間休暇WORKを作成する
			TimeVacationWork timeVacationWork = this.createTimeVacationWork(integrationOfDaily);
			
			//流動勤務(就内、平日)
			this.withinWorkingTimeSheet.set(WithinWorkTimeSheet.createAsFlow(
					companyCommonSetting,
					personDailySetting,
					todayWorkType,
					integrationOfWorkTime,
					integrationOfDaily,
					this.predetermineTimeSetForCalc,
					deductTimeSheet,
					creatingWithinWorkTimeSheet,
					timeVacationWork,
					this.attendanceLeavingWork));
			
			if(this.withinWorkingTimeSheet.get().getWithinWorkTimeFrame().isEmpty())
				return;
			
			//流動勤務(平日・就外）
			this.outsideWorkTimeSheet.set(
					OutsideWorkTimeSheet.createOverTimeAsFlow(
							companyCommonSetting,
							personDailySetting,
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							this.predetermineTimeSetForCalc,
							deductTimeSheet,
							this.withinWorkingTimeSheet.get(),
							previousAndNextDaily,
							this.attendanceLeavingWork));
		} else {
			//休出の場合でも就業時間内時間帯を作成する必要がある為、空で作成
			this.withinWorkingTimeSheet = Finally.of(WithinWorkTimeSheet.createEmpty());
			
			if(!creatingWithinWorkTimeSheet.getFirstStartAndLastEnd().isPresent())
				return;
				
			//流動勤務(休日出勤)
			 this.outsideWorkTimeSheet.set(
					OutsideWorkTimeSheet.createHolidayAsFlow(
							companyCommonSetting,
							personDailySetting,
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							deductTimeSheet,
							creatingWithinWorkTimeSheet.getFirstStartAndLastEnd().get(),
							this.oneDayOfRange,
							previousAndNextDaily));
		}
	}
	
	/**
	 * 事前処理
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingOfDaily 日別勤怠の出退勤
	 * @param creatingWithinWorkTimeSheet 就業時間内時間帯
	 * @return 控除時間帯
	 */
	public DeductionTimeSheet prePocessForFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			WithinWorkTimeSheet creatingWithinWorkTimeSheet){
		
		//補正用出退勤としてクローンする
		TimeLeavingOfDailyAttd timeLeavingForCorrect = new TimeLeavingOfDailyAttd(
				new ArrayList<>(this.attendanceLeavingWork.getTimeLeavingWorks()), this.attendanceLeavingWork.getWorkTimes());
		List<TimeLeavingWork> timeLeavingWorksForCorrect = timeLeavingForCorrect.getTimeLeavingWorks();
		
		//1回目と2回目の間の時間帯を作成  遅刻丸めによる時刻補正を行うよりも前に時間帯を作成する必要がある為、ここで作成。
		Optional<TimeSheetOfDeductionItem> betweenTimeSheet = this.createBetweenWork(
				timeLeavingForCorrect.getTimeLeavingWorks(),
				integrationOfWorkTime.getFlowWorkSetting().get());
		
		//間の休憩を非勤務時間帯へ
		if(betweenTimeSheet.isPresent()
				&& betweenTimeSheet.get().getWorkingBreakAtr().isWorking()
				&& betweenTimeSheet.get().getDeductionAtr().isBreak()) {
			this.nonWorkingTimeSheet = Optional.of(new NonWorkingTimeSheet(betweenTimeSheet.get()));
		};
		
		//控除時間帯の取得
		DeductionTimeSheet deductionTimeSheetCalcBefore = DeductionTimeSheet.createDeductionTimeForCalc(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				this.oneDayOfRange,
				timeLeavingForCorrect,
				betweenTimeSheet,
				companyCommonSetting,
				personDailySetting);
		
		//遅刻時間帯の計算
		List<TimeLeavingWork> calcLateTimeLeavingWorks = new ArrayList<>(timeLeavingWorksForCorrect);
		timeLeavingWorksForCorrect.clear();
		for(TimeLeavingWork timeLeavingWork : calcLateTimeLeavingWorks) {
			timeLeavingWorksForCorrect.add(
					this.calcLateTimeSheet(
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							deductionTimeSheetCalcBefore,
							personDailySetting.getAddSetting().getVacationCalcMethodSet(),
							timeLeavingWork,
							creatingWithinWorkTimeSheet));
		}
		
		//控除時間帯の取得
		DeductionTimeSheet deductionTimeSheetCalcAfter = DeductionTimeSheet.createDeductionTimeForCalc(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				this.oneDayOfRange,
				timeLeavingForCorrect,
				betweenTimeSheet,
				companyCommonSetting,
				personDailySetting);
		
		//早退時間帯の計算
		List<TimeLeavingWork> calcLeaveEarlyTimeLeavingWorks = new ArrayList<>(timeLeavingWorksForCorrect);
		timeLeavingWorksForCorrect.clear();
		for(TimeLeavingWork timeLeavingWork : calcLeaveEarlyTimeLeavingWorks) {
			timeLeavingWorksForCorrect.add(
					this.calcLeaveEarlyTimeSheet(
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							deductionTimeSheetCalcBefore,
							personDailySetting.getAddSetting().getVacationCalcMethodSet(),
							timeLeavingWork,
							creatingWithinWorkTimeSheet));
		}
		return deductionTimeSheetCalcAfter;
	}
	
	/**
	 * 補正用事前処理
	 * アルゴリズム：控除時間帯の取得
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingOfDaily 日別勤怠の出退勤
	 * @param creatingWithinWorkTimeSheet 就業時間内時間帯
	 * @return 控除項目の時間帯(List)
	 */
	public List<TimeSheetOfDeductionItem> prePocessForFlowCorrect(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeLeavingOfDailyAttd timeLeavingOfDaily,
			WithinWorkTimeSheet creatingWithinWorkTimeSheet,
			boolean correctWithEndTime){
		
		if (integrationOfDaily.getAttendanceLeave()
				.map(x -> x.getTimeLeavingWorks().stream().allMatch(y -> !y.existsTimeWithDay())).orElse(true))
			return new ArrayList<>();

		//出勤時刻を予定開始時刻にする
		this.setScheduleStartTimeForFlow(
				integrationOfDaily.getWorkInformation(),
				integrationOfWorkTime.getFlowWorkSetting().get().getFlowSetting().getCalculateSetting());
		
		//パラメータを受け取る
		//補正用出退勤としてクローンする
		TimeLeavingOfDailyAttd timeLeavingForCorrect = new TimeLeavingOfDailyAttd(
				new ArrayList<>(this.attendanceLeavingWork.getTimeLeavingWorks()), this.attendanceLeavingWork.getWorkTimes());
		List<TimeLeavingWork> timeLeavingWorksForCorrect = timeLeavingForCorrect.getTimeLeavingWorks();
		
		/** 1回目と2回目の間の時間帯を作成 */
		Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets = this.createBetweenWork(
				timeLeavingWorksForCorrect,
				integrationOfWorkTime.getFlowWorkSetting().get());
		
		/** 休憩が固定の場合は控除時間帯を取得する */
		List<TimeSheetOfDeductionItem> deductionItemsCalcBefore = getDeductionTimeSheetOnFixed(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				companyCommonSetting,
				personDailySetting, correctWithEndTime);
		DeductionTimeSheet deductionTimeSheetCalcBefore = new DeductionTimeSheet(
				deductionItemsCalcBefore,
				new ArrayList<>(),
				new BreakTimeOfDailyAttd(),
				Optional.empty(),
				new ArrayList<>());
		
		//遅刻時間帯の計算
		List<TimeLeavingWork> calcLateTimeLeavingWorks = new ArrayList<>(timeLeavingWorksForCorrect);
		timeLeavingWorksForCorrect.clear();
		for(TimeLeavingWork timeLeavingWork : calcLateTimeLeavingWorks) {
			timeLeavingWorksForCorrect.add(
					this.calcLateTimeSheet(
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							deductionTimeSheetCalcBefore,
							personDailySetting.getAddSetting().getVacationCalcMethodSet(),
							timeLeavingWork,
							creatingWithinWorkTimeSheet));
		}
		
		List<LateTimeSheet> lateTimeSheet = creatingWithinWorkTimeSheet.getWithinWorkTimeFrame().stream()
																		.filter(c -> c.getLateTimeSheet().isPresent())
																		.map(c -> c.getLateTimeSheet().get())
																		.collect(Collectors.toList());
		
		//控除時間帯の取得
		DeductionTimeSheet deductionTimeSheetCalcAfter = provisionalDeterminationOfDeductionTimeSheet(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				this.oneDayOfRange,
				Optional.of(timeLeavingForCorrect),
				this.predetermineTimeSetForCalc,
				lateTimeSheet, correctWithEndTime, betweenWorkTimeSheets,
				companyCommonSetting,
				personDailySetting);
		
		//早退時間帯の計算
		List<TimeLeavingWork> calcLeaveEarlyTimeLeavingWorks = new ArrayList<>(timeLeavingWorksForCorrect);
		timeLeavingWorksForCorrect.clear();
		for(TimeLeavingWork timeLeavingWork : calcLeaveEarlyTimeLeavingWorks) {
			timeLeavingWorksForCorrect.add(
					this.calcLeaveEarlyTimeSheet(
							todayWorkType,
							integrationOfWorkTime,
							integrationOfDaily,
							deductionTimeSheetCalcBefore,
							personDailySetting.getAddSetting().getVacationCalcMethodSet(),
							timeLeavingWork,
							creatingWithinWorkTimeSheet));
		}
		
		return deductionTimeSheetCalcAfter.getForDeductionTimeZoneList();
	}
	
	/** 休憩が固定の場合は控除時間帯を取得する (補正処理から呼ばれる)*/
	public List<TimeSheetOfDeductionItem> getDeductionTimeSheetOnFixed(WorkType workType,
			IntegrationOfWorkTime workTime, IntegrationOfDaily integrationOfDaily,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting, boolean correctWithEndTime) {
		
		//** 1日半日出勤・1日休日系の判定 */
		if(workType.checkWorkDay() == WorkStyle.ONE_DAY_REST) {
			return new ArrayList<>();
		}
		
		/** 控除時間帯の取得 */
		val deductionTimeSheet = provisionalDeterminationOfDeductionTimeSheet(
						workType, workTime, integrationOfDaily, this.oneDayOfRange, 
						integrationOfDaily.getAttendanceLeave(), this.predetermineTimeSetForCalc, 
						new ArrayList<>(), correctWithEndTime, Optional.empty(),
						companyCommonSetting, personDailySetting)
				.getForDeductionTimeZoneList();
		
		/** 休憩が固定かどうかを判断する */
		if (workTime.isFixBreak(workType)) {
			return deductionTimeSheet;
		}
		
		/** 流動休憩の場合は、休憩時間帯は削除する */
		return deductionTimeSheet.stream()
				.filter(dts -> dts.getDeductionAtr() != DeductionClassification.BREAK)
				.collect(Collectors.toList());
	}
	
	/**
	* 遅刻時間帯の計算
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param timeLeavingWork 出退勤
	 * @param creatingWithinWorkTimeSheet 就業時間内時間帯
	 * @return 出退勤
	 */
	public TimeLeavingWork calcLateTimeSheet(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			HolidayCalcMethodSet holidayCalcMethodSet,
			TimeLeavingWork timeLeavingWork,
			WithinWorkTimeSheet creatingWithinWorkTimeSheet){
		
		//所定時間帯を取得する
		PredetermineTimeSetForCalc predetermineTimeSet = this.getPredetermineTimeSheetForFlow(timeLeavingWork.getWorkNo(), todayWorkType);
		
		//計算範囲を判断する
		creatingWithinWorkTimeSheet.getWithinWorkTimeFrame().add(this.createWithinWorkTimeFrameIncludingCalculationRange(
				todayWorkType, integrationOfWorkTime, timeLeavingWork, predetermineTimeSet));

		//遅刻時間帯を計算
		return creatingWithinWorkTimeSheet.calcLateTimeDeduction(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				deductionTimeSheet,
				holidayCalcMethodSet,
				timeLeavingWork,
				predetermineTimeSet);
	}
	
	/**
	 * 早退時間帯の計算
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param timeLeavingWork 出退勤
	 * @param creatingWithinWorkTimeSheet 就業時間内時間帯
	 * @return  出退勤
	 */
	private TimeLeavingWork calcLeaveEarlyTimeSheet(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			HolidayCalcMethodSet holidayCalcMethodSet,
			TimeLeavingWork timeLeavingWork,
			WithinWorkTimeSheet creatingWithinWorkTimeSheet){
		
		//所定時間帯を取得する
		PredetermineTimeSetForCalc predetermineTimeSet = getPredetermineTimeSheetForFlow(timeLeavingWork.getWorkNo(), todayWorkType);
		
		//早退時間帯を計算
		return creatingWithinWorkTimeSheet.calcLeaveEarlyTimeDeduction(
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				deductionTimeSheet,
				holidayCalcMethodSet,
				timeLeavingWork,
				predetermineTimeSet);
	}
	
	/**
	* 所定時間帯を取得する(流動)_自身の計算用所定時間設定を補正した新しいインスタンスを作成して返す
	* @param workNo 勤務NO
	* @param workType 勤務種類
	* @return PredetermineTimeSetForCalc 所定時間設定(計算用クラス)
	*/
	private PredetermineTimeSetForCalc getPredetermineTimeSheetForFlow(
			WorkNo workNo,
			WorkType workType){
		
		PredetermineTimeSetForCalc copiedPredetermineTimeSetForCalc = this.predetermineTimeSetForCalc.clone();
			
		//始業/終業時刻から参照
		Optional<ScheduleTimeSheet> scheduleTimeSheet = this.workInformationOfDaily.getScheduleTimeSheet(workNo);
		
		if(scheduleTimeSheet.isPresent()) {
			copiedPredetermineTimeSetForCalc.getTimeSheets().stream().filter(c -> c.getWorkNo() == workNo.v())
				.findFirst().ifPresent(c -> {
					c.updateStartTime(scheduleTimeSheet.get().getAttendance());
					c.updateEndTime(scheduleTimeSheet.get().getLeaveWork());
				});
//			copiedPredetermineTimeSetForCalc.getTimeSheets().get(workNo.v()).updateStartTime(scheduleTimeSheet.get().getAttendance());
//			copiedPredetermineTimeSetForCalc.getTimeSheets().get(workNo.v()).updateEndTime(scheduleTimeSheet.get().getLeaveWork());
		}
		
		//午前勤務、午後勤務の場合に時間帯を補正する
		copiedPredetermineTimeSetForCalc.correctPredetermineTimeSheet(workType.getDailyWork(),workNo.v());
		return copiedPredetermineTimeSetForCalc;
	}
	
	/**
	 * 出勤時刻を予定開始時刻にする
	 * @param workInfo 日別勤怠の勤務情報
	 * @param flowCalculateSet 流動計算設定
	 */
	public void setScheduleStartTimeForFlow(WorkInfoOfDailyAttendance workInfo, FlowCalculateSet flowCalculateSet) {
		for(TimeLeavingWork timeLeavingWork : this.attendanceLeavingWork.getTimeLeavingWorks()) {
			Optional<ScheduleTimeSheet> schedule = workInfo.getScheduleTimeSheet(timeLeavingWork.getWorkNo());
			if(!schedule.isPresent()) {
				continue;
			}
			timeLeavingWork.setScheduleStartTimeForFlow(schedule.get().getAttendance(), flowCalculateSet);
		}
	}
	
	/**
	 * 計算範囲を判断
	 * @param todayWorkType 勤務種類
	 * @param workTime 統合就業時間帯
	 * @param timeLeavingWork 出退勤
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @return 就業時間内時間枠
	 */
	public WithinWorkTimeFrame createWithinWorkTimeFrameIncludingCalculationRange(WorkType todayWorkType,
			IntegrationOfWorkTime workTime, TimeLeavingWork timeLeavingWork, PredetermineTimeSetForCalc predetermineTimeSet) {
		
		/** 計算範囲を判断 */
		TimeSpanForDailyCalc calcRange = getCalcRange(todayWorkType, timeLeavingWork, predetermineTimeSet);
		
		//就業時間内時間枠作成
		return new WithinWorkTimeFrame(
				new EmTimeFrameNo(timeLeavingWork.getWorkNo().v()),
				timeLeavingWork.getWorkNo(),
				calcRange,
				calcRange,
				workTime.getFlowWorkSetting().isPresent()
						? workTime.getFlowWorkSetting().get().getHalfDayWorkTimezone().getWorkTimeZone().getWorkTimeRounding()
						: new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				MidNightTimeSheetForCalcList.createEmpty(),
				new ArrayList<>(),
				Optional.empty(),
				Optional.empty());
	}

	/** 計算範囲を判断 */
	private TimeSpanForDailyCalc getCalcRange(WorkType todayWorkType, TimeLeavingWork timeLeavingWork,
			PredetermineTimeSetForCalc predetermineTimeSet) {
		//出退勤の計算範囲を計算用クラスとして作成
		TimeSpanForDailyCalc calcRange = new TimeSpanForDailyCalc(timeLeavingWork.getTimeZone().getStart(), timeLeavingWork.getTimeZone().getEnd());
		
		if(todayWorkType.getAttendanceHolidayAttr().isMorning()) {
			//午前終了の時刻までの時間帯に補正する
			calcRange = calcRange.shiftOnlyEnd(predetermineTimeSet.getAMEndTime());
		}
		
		if(todayWorkType.getAttendanceHolidayAttr().isAfternoon()) {
			//午後開始の時刻からの時間帯に補正する
			calcRange = calcRange.shiftOnlyStart(predetermineTimeSet.getPMStartTime());
		}
		return calcRange;
	}
	
	/**
	 * 1回目と2回目の間の時間帯を作成
	 * @param timeLeavingForFlowWork 出退勤
	 * @param workType 勤務種類
	 * @param flowWorkSetting 流動勤務設定
	 * @return Optional<TimeSheetOfDeductionItem>  控除項目の時間帯
	 */
	public Optional<TimeSheetOfDeductionItem> createBetweenWork(
			List<TimeLeavingWork> timeLeavingForFlowWork,
			FlowWorkSetting flowWorkSetting) {
		
		//1回目の退勤と2回目の出勤がない
		if(!this.existsFirstEndAndSecondStart(timeLeavingForFlowWork)) return Optional.empty();
		
		//時間帯を作成
		TimeSpanForDailyCalc betweenWorkTimeSheet = createBetweenWorkTimeSheet(timeLeavingForFlowWork, flowWorkSetting);
		
		//控除時間帯を作成
		Optional<TimeSheetOfDeductionItem> deductionTimeBetweenWork = Optional.of(TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(
				betweenWorkTimeSheet, 
				flowWorkSetting.getRestSetting().getFlowRestSetting().getRoundingBreakMultipleWork(),
				Collections.emptyList(),
				Collections.emptyList(),
				WorkingBreakTimeAtr.WORKING,
				Finally.empty(),
				flowWorkSetting.getRestSetting().getFlowRestSetting().isUsePluralWorkRestTime() ? Finally.of(BreakClassification.BREAK): Finally.empty(),
				Optional.empty(),
				//休憩として扱う場合、就業時間から控除し休憩時間に計上する(控除種別.休憩)　休憩として扱わない場合、就業時間から控除するが休憩時間には計上しない(控除種別.計上なし)
				flowWorkSetting.getRestSetting().getFlowRestSetting().isUsePluralWorkRestTime() ? DeductionClassification.BREAK : DeductionClassification.NON_RECORD,
				Optional.empty(),
				false));
		
		return deductionTimeBetweenWork;
	}
	
	/**
	 * 出退勤に時刻が埋まっているかどうかを確認する(1回目の退勤と2回目の出勤が埋まっているか)
	 * @param timeLeavingWorks 出退勤（List)
	 * @return true：埋まっている false：埋まっていない
	 */
	private boolean existsFirstEndAndSecondStart(List<TimeLeavingWork> timeLeavingWorks) {
		if(timeLeavingWorks.size() < 2) return false;
		
		//1回目の退勤
		Optional<TimeLeavingWork> first = timeLeavingWorks.stream()
				.filter(a -> a.getWorkNo().equals(new WorkNo(1)))
				.findFirst();
		if(!first.isPresent()) return false;
		if(!first.get().getLeaveStamp().isPresent()) return false;
		
		//2回目の出勤
		Optional<TimeLeavingWork> second = timeLeavingWorks.stream()
				.filter(a -> a.getWorkNo().equals(new WorkNo(2)))
				.findFirst();
		if(!second.isPresent()) return false;
		if(!second.get().getAttendanceStamp().isPresent()) return false;

		return true;
	}
	
	/**
	 * 時間帯を判断（流動_勤務間）
	 * @param timeLeavingWorks 出退勤
	 * @param flowWorkSetting 流動勤務設定
	 * @return 勤務間の時間帯
	 */
	private TimeSpanForDailyCalc createBetweenWorkTimeSheet(
			List<TimeLeavingWork> timeLeavingWorks,
			FlowWorkSetting flowWorkSetting) {
		
		TimeSpanForDailyCalc betweenWorkTimeSheet = new TimeSpanForDailyCalc(TimeWithDayAttr.THE_PRESENT_DAY_0000, TimeWithDayAttr.THE_PRESENT_DAY_0000);
		
		val timeLeaveNo1 = timeLeavingWorks.stream().filter(c -> c.getWorkNo().v() == 1).findFirst().get();
		val timeLeaveNo2 = timeLeavingWorks.stream().filter(c -> c.getWorkNo().v() == 2).findFirst().get();
		val predetermineNo1 = this.predetermineTimeSetForCalc.getTimeSheet(1).get();
		val predetermineNo2 = this.predetermineTimeSetForCalc.getTimeSheet(2).get();
		
		//1回目の勤務の退勤>=所定
		if(timeLeaveNo1.getTimespan().getEnd().greaterThanOrEqualTo(predetermineNo1.getEnd().v())) {
			betweenWorkTimeSheet = betweenWorkTimeSheet.shiftOnlyStart(timeLeaveNo1.getTimespan().getEnd());
		} else {
			betweenWorkTimeSheet = betweenWorkTimeSheet.shiftOnlyStart(predetermineNo1.getEnd());
		}
		
		//2回目の勤務の出勤>=所定
		if(timeLeaveNo2.getTimespan().getStart().greaterThanOrEqualTo(predetermineNo2.getStart().v())) {
			return betweenWorkTimeSheet.shiftOnlyEnd(predetermineNo2.getStart());
		} else {
			//予定開始時刻から計算する
			if(flowWorkSetting.getFlowSetting().getCalculateSetting().getCalcStartTimeSet().equals(PrePlanWorkTimeCalcMethod.CALC_FROM_PLAN_START_TIME)) {
				return betweenWorkTimeSheet.shiftOnlyEnd(timeLeaveNo2.getTimespan().getStart());
			}
			//出勤時刻から計算する
			if(flowWorkSetting.getFlowSetting().getCalculateSetting().getCalcStartTimeSet().equals(PrePlanWorkTimeCalcMethod.CALC_FROM_WORK_TIME)) {
				return betweenWorkTimeSheet.shiftOnlyEnd(predetermineNo2.getStart());
			}
		}
		return betweenWorkTimeSheet;
	}

	/**
	 * 流動勤務所定変動
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfDaily 日別実績(Work)（実績）
	 * @param flowWorkSetting 流動勤務設定
	 * @param schedulePerformance 予定実績
	 */
	private void fluctuationPredeterminedForFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfDaily integrationOfDaily,
			FlowWorkSetting flowWorkSetting,
			Optional<SchedulePerformance> schedulePerformance) {
		
		//変動させるかチェックする
		if(!isFluctuationPredeterminedForFlow(todayWorkType, integrationOfDaily, flowWorkSetting.getFlowSetting().getOvertimeSetting(), schedulePerformance))
			return;
		
		//変動させる時間を求める
		AttendanceTimeOfExistMinus fluctuationTime = getFluctuationPredeterminedForFlow(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfDaily,
				schedulePerformance.get(),
				flowWorkSetting.getFlowSetting().getOvertimeSetting());
		
		if(!fluctuationTime.equals(AttendanceTimeOfExistMinus.ZERO)) {
			//所定時間帯を変動させる
			this.predetermineTimeSetForCalc.fluctuationPredeterminedTimeSheetToSchedule(
					schedulePerformance.get().getCalculationRangeOfOneDay().getAttendanceLeavingWork().getTimeLeavingWorks());
			
			//所定時間を変動させる
			this.predetermineTimeSetForCalc.getAdditionSet().fluctuationPredeterminedTimeForFlow(fluctuationTime);
			
			//残業時間帯を変動させる
			flowWorkSetting.getHalfDayWorkTimezone().getWorkTimeZone().fluctuationElapsedTimeInLstOTTimezone(fluctuationTime);
		}
	}
	
	/**
	 * 変動させる時間を求める
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfDaily 日別実績(Work)（実績）
	 * @param schedulePerformance 予定実績
	 * @param FlowOTSet 流動残業設定
	 * @return changeTime 変動させる時間
	 */
	private AttendanceTimeOfExistMinus getFluctuationPredeterminedForFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfDaily integrationOfDaily,
			SchedulePerformance schedulePerformance,
			FlowOTSet flowOTSet) {
		
		//予定の所定時間を計算する
		AttendanceTime schedulePredetermineTime = WorkScheduleTimeOfDaily.calcPredeterminedFromTime(
				companyCommonSetting,
				personDailySetting,
				integrationOfDaily,
				schedulePerformance,
				flowOTSet);
		
		//実績の所定時間を計算する
		AttendanceTime recordPredetermineTime = this.predetermineTimeSetForCalc.getpredetermineTime(todayWorkType.getDailyWork());
		
		//予定の所定時間 - 実績の所定時間
		AttendanceTimeOfExistMinus fluctuationTime = new AttendanceTimeOfExistMinus(schedulePredetermineTime.v()).minusMinutes(recordPredetermineTime.v());
		
		//所定変動区分が「後にズラす」
		if(flowOTSet.getFixedChangeAtr() == FixedChangeAtr.AFTER_SHIFT && fluctuationTime.isNegative()) {
			fluctuationTime = AttendanceTimeOfExistMinus.ZERO;
		}
		return fluctuationTime;
	}
	
	/**
	 * 変動させるかチェックする
	 * @param todayWorkType 勤務種類
	 * @param integrationOfDaily 日別実績(Work)（実績）
	 * @param FlowOTSet 流動残業設定
	 * @param manageReGetClassOfSchedule 時間帯作成、時間計算で再取得が必要になっているクラスたちの管理クラス(予定）
	 * @return true:変動させる false:変動させない
	 */
	private boolean isFluctuationPredeterminedForFlow(
			WorkType todayWorkType,
			IntegrationOfDaily integrationOfDaily,
			FlowOTSet flowOTSet,
			Optional<SchedulePerformance> schedulePerformance) {
		
		if(!schedulePerformance.isPresent() //予定を渡している場合（予定の計算時には変動させない為）
			|| !isMatchWorkInfomation(integrationOfDaily.getWorkInformation().getRecordInfo(), 
								 	  integrationOfDaily.getSnapshot().map(c -> c.getWorkInfo())) //勤務実績と勤務予定の勤務情報が一致しない
			|| todayWorkType.chechAttendanceDay().equals(AttendanceDayAttr.HOLIDAY_WORK) //勤務実績の勤務種類が休出
			|| flowOTSet.getFixedChangeAtr().equals(FixedChangeAtr.NOT_CHANGE)) { //所定変動区分が「変動しない」
			return false;
		}
		return true;
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 変更する時間
	 * @param commonSet 就業時間帯の共通設定
	 * @return 1日の計算範囲
	 */
	public Optional<CalculationRangeOfOneDay> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, Optional<WorkTimezoneCommonSet> commonSet) {
		Optional<TimeSpanForDailyCalc> duplicate = this.oneDayOfRange.getDuplicatedWith(timeSpan);
		if(!duplicate.isPresent()) {
			return Optional.empty();
		}
		Finally<WithinWorkTimeSheet> within = Finally.empty();
		if(this.withinWorkingTimeSheet.isPresent()) {
			//就業時間内時間帯を重複する時間帯で作り直す
			within = Finally.of(this.withinWorkingTimeSheet.get().recreateWithDuplicate(duplicate.get(), commonSet));
		}
		Finally<OutsideWorkTimeSheet> outside = Finally.empty();
		if(this.outsideWorkTimeSheet.isPresent()) {
			//就業時間外時間帯を重複する時間帯で作り直す
			outside = Finally.of(this.outsideWorkTimeSheet.get().recreateWithDuplicate(duplicate.get(), commonSet));
		}
		return Optional.of(new CalculationRangeOfOneDay(
				duplicate.get(),
				this.workInformationOfDaily,
				this.attendanceLeavingWork,
				this.predetermineTimeSetForCalc,
				this.nonWorkingTimeSheet,
				this.shortTimeWSWithoutWork,
				within,
				outside,
				this.beforeAttendance,
				this.afterLeaving));
	}

	/**
	 * 勤務実績と勤務予定の勤務情報を比較
	 * @param workNo
	 * @param predetermineTimeSheetSetting
	 * @return
	 */
	private boolean isMatchWorkInfomation(WorkInformation record, Optional<WorkInformation> schedule) {
		val isSameWorkType = record.getWorkTypeCode().equals(schedule.map(c -> c.getWorkTypeCode()).orElse(null));
		val isSameWorkTime = record.getWorkTimeCodeNotNull()
					.map(wtc -> wtc.equals(schedule.flatMap(c -> c.getWorkTimeCodeNotNull()).orElse(null)))
					.orElse(false);
		if(isSameWorkType && isSameWorkTime) {
			return true;
		}
		return false;
	}
	
	/**
	 * 勤務間休憩時間を計算する
	 * @return 勤務間休憩時間
	 */
	public AttendanceTime calcBetweenBreakTime() {
		if(!this.nonWorkingTimeSheet.isPresent()) {
			return AttendanceTime.ZERO;
		}
		return this.nonWorkingTimeSheet.get().calcBetweenBreakTime();
	}
	
	/**
	 * 勤務外短時間勤務時間帯の作成
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 */
	public void createShortTimeWSWithoutWork(
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting){
		
		this.shortTimeWSWithoutWork = Optional.of(ShortTimeWorkSheetWithoutWork.create(
				workType, integrationOfWorkTime, integrationOfDaily, this,
				companyCommonSetting, personCommonSetting));
	}
	
	/**
	 * 時間休暇WORKを作成する
	 * @param itgOfDaily 日別勤怠(WORK)
	 * @return 時間休暇WORK
	 */
	public TimeVacationWork createTimeVacationWork(IntegrationOfDaily itgOfDaily){
		
		if (!itgOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) return TimeVacationWork.defaultValue();
		// 総労働時間
		TotalWorkingTime totalWorkTime = itgOfDaily.getAttendanceTimeOfDailyPerformance().get()
				.getActualWorkingTimeOfDaily().getTotalWorkingTime();
		// 勤務NO
		List<WorkNo> workNoList = new ArrayList<>(); 
		for (LateTimeOfDaily late : totalWorkTime.getLateTimeOfDaily()){
			if (!workNoList.contains(late.getWorkNo())) workNoList.add(late.getWorkNo());
		}
		for (LeaveEarlyTimeOfDaily early : totalWorkTime.getLeaveEarlyTimeOfDaily()){
			if (!workNoList.contains(early.getWorkNo())) workNoList.add(early.getWorkNo());
		}
		// 勤務NO毎の時間
		List<TimeVacationWorkEachNo> eachNoList = new ArrayList<>();
		for (WorkNo workNo : workNoList){
			Optional<LateTimeOfDaily> lateOpt = totalWorkTime.getLateTimeNo(workNo.v());
			Optional<LeaveEarlyTimeOfDaily> earlyOpt = totalWorkTime.getLeaveEarlyTimeNo(workNo.v());
			TimeVacationWorkEachNo eachNo = TimeVacationWorkEachNo.of(
					workNo,
					lateOpt.isPresent() ?
							lateOpt.get().getTimePaidUseTime().clone() : TimevacationUseTimeOfDaily.defaultValue(),
					earlyOpt.isPresent() ?
							earlyOpt.get().getTimePaidUseTime().clone() : TimevacationUseTimeOfDaily.defaultValue());
			eachNoList.add(eachNo);
		}
		// 私用外出
		Optional<OutingTimeOfDaily> privateOutingOpt = totalWorkTime.getOutingTimeByReason(GoingOutReason.PRIVATE);
		// 組合外出
		Optional<OutingTimeOfDaily> unionOutingOpt = totalWorkTime.getOutingTimeByReason(GoingOutReason.UNION);
		// 時間休暇WORK
		return TimeVacationWork.of(
				eachNoList,
				privateOutingOpt.isPresent() ?
						privateOutingOpt.get().getTimeVacationUseOfDaily().clone() : TimevacationUseTimeOfDaily.defaultValue(),
				unionOutingOpt.isPresent() ?
						unionOutingOpt.get().getTimeVacationUseOfDaily().clone() : TimevacationUseTimeOfDaily.defaultValue());
	}
}
