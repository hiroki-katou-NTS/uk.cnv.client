package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.AttendanceItemDictionaryForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.FlexWithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareFrameSet;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.TimezoneOfFixedRestTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.ExceededPredAddVacationCalc;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkCalcSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.OverTimeCalcNoBreak;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 日別実績の残業時間
 * 
 * @author keisuke_hoshina
 */
@Getter
public class OverTimeOfDaily {
	// 残業枠時間帯
	private List<OverTimeFrameTimeSheet> overTimeWorkFrameTimeSheet;
	// 残業枠時間
	@Setter
	private List<OverTimeFrameTime> overTimeWorkFrameTime;
	// 法定外深夜時間 (所定外深夜時間)
	@Setter
	private Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeWorkMidNightTime;
	// 残業拘束時間
	private AttendanceTime overTimeWorkSpentAtWork = new AttendanceTime(0);
	// 変形法定内残業
	private AttendanceTime irregularWithinPrescribedOverTimeWork = new AttendanceTime(0);
	// フレックス時間
	@Setter
	private FlexTime flexTime = new FlexTime(
			TimeDivergenceWithCalculationMinusExist.sameTime(new AttendanceTimeOfExistMinus(0)), new AttendanceTime(0));

	public OverTimeOfDaily(List<OverTimeFrameTimeSheet> frameTimeSheetList, List<OverTimeFrameTime> frameTimeList,
			Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeWorkMidNightTime) {
		this.overTimeWorkFrameTimeSheet = new ArrayList<>(frameTimeSheetList);
		this.overTimeWorkFrameTime = new ArrayList<>(frameTimeList);
		this.excessOverTimeWorkMidNightTime = excessOverTimeWorkMidNightTime;
	}

	public OverTimeOfDaily(List<OverTimeFrameTimeSheet> frameTimeSheetList, List<OverTimeFrameTime> frameTimeList,
			Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeWorkMidNightTime, AttendanceTime irregularTime,
			FlexTime flexTime, AttendanceTime overTimeWork) {
		this.overTimeWorkFrameTimeSheet = new ArrayList<>(frameTimeSheetList);
		this.overTimeWorkFrameTime = new ArrayList<>(frameTimeList);
		this.excessOverTimeWorkMidNightTime = excessOverTimeWorkMidNightTime;
		this.irregularWithinPrescribedOverTimeWork = irregularTime;
		this.flexTime = flexTime;
		this.overTimeWorkSpentAtWork = overTimeWork;
	}

	/**
	 * 勤務回数を見て開始時刻が正しいか判定する
	 * 
	 * @param startTime
	 * @param workNo
	 * @param attendanceTime
	 * @return
	 */
	public static boolean startDicision(TimeWithDayAttr startTime, int workNo, TimeWithDayAttr attendanceTime) {
		if (workNo == 0) {
			return (startTime.v() < attendanceTime.v());
		} else {
			return (startTime.v() >= attendanceTime.v());
		}
	}

	/**
	 * 残業時間が含んでいる加給時間の計算
	 * 
	 * @return 加給時間リスト
	 */
	public List<BonusPayTime> calcBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet, BonusPayAtr bonusPayAtr,
			CalAttrOfDailyAttd calcAtrOfDaily) {
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(OverTimeFrameTimeSheetWork frameTimeSheet : overTimeWorkFrameTimeSheet) {
//			bonusPayList.addAll(frameTimeSheet.calcBonusPay(ActualWorkTimeSheetAtr.OverTimeWork,bonusPayAutoCalcSet, calcAtrOfDaily));
//		}
		return bonusPayList;
	}

	/**
	 * 残業時間が含んでいる特定日加給時間の計算
	 * 
	 * @return 加給時間リスト
	 */
	public List<BonusPayTime> calcSpecifiedBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet,
			BonusPayAtr bonusPayAtr, CalAttrOfDailyAttd calcAtrOfDaily) {
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(OverTimeFrameTimeSheetWork frameTimeSheet : overTimeWorkFrameTimeSheet) {
//			bonusPayList.addAll(frameTimeSheet.calcSpacifiedBonusPay(ActualWorkTimeSheetAtr.OverTimeWork,bonusPayAutoCalcSet, calcAtrOfDaily));
//		}
		return bonusPayList;
	}

	/**
	 * 残業時間が含んでいる深夜時間の算出
	 * 
	 * @return 日別実績の深夜時間帯クラス
	 */
	public ExcessOverTimeWorkMidNightTime calcMidNightTimeIncludeOverTimeWork(AutoCalOvertimeSetting autoCalcSet) {
		int totalTime = 0;
//		for(OverTimeFrameTimeSheetWork frameTime : overTimeWorkFrameTimeSheet) {
//			/*↓分岐の条件が明確になったら記述*/
//			AutoCalcSet setting;
//			if(frameTime.getWithinStatutoryAtr().isStatutory()) {
//				setting = autoCalcSet.getLegalOvertimeHours();
//			}
//			else if(frameTime.isGoEarly()) {
//				setting = autoCalcSet.getEarlyOvertimeHours();
//			}
//			else {
//				setting = autoCalcSet.getNormalOvertimeHours();
//			}
//			totalTime += frameTime.calcMidNight(setting.getCalculationClassification());
//		}
		return new ExcessOverTimeWorkMidNightTime(
				TimeDivergenceWithCalculation.sameTime(new AttendanceTime(totalTime)));
	}

	/**
	 * 全枠の残業時間の合計の算出
	 * 
	 * @return 残業時間
	 */
	public AttendanceTime calcTotalFrameTime() {
		int sumOverTime = this.overTimeWorkFrameTime.stream()
				.collect(Collectors.summingInt(x -> x.getOverTimeWork().getTime().v()));
		return  new AttendanceTime(sumOverTime);
	}

	/**
	 * 全枠の振替残業時間の合計の算出
	 * 
	 * @return 振替残業時間
	 */
	public AttendanceTime calcTransTotalFrameTime() {
		int sumOverTranferTime = this.overTimeWorkFrameTime.stream()
				.collect(Collectors.summingInt(x -> x.getTransferTime().getTime().v()));
		return  new AttendanceTime(sumOverTranferTime);
	}

	//全枠の事前申請時間の合計の算出
	public AttendanceTime calcTotalAppTime() {
		int sumApp = this.overTimeWorkFrameTime.stream()
				.collect(Collectors.summingInt(x -> x.getBeforeApplicationTime().v()));
		return new AttendanceTime(sumApp);
	}
	
	public OverTimeOfDaily createFromJavaType(List<OverTimeFrameTime> frameTimeList,
			ExcessOverTimeWorkMidNightTime midNightTime, AttendanceTime irregularTime, FlexTime flexTime,
			AttendanceTime overTimeWork) {
		return new OverTimeOfDaily(Collections.emptyList(), frameTimeList, Finally.of(midNightTime), irregularTime,
				flexTime, overTimeWork);
	}

	/**
	 * メンバー変数の時間計算を指示するクラス アルゴリズム：日別実績の残業時間
	 * 
	 * @param recordReGet                     実績
	 * @param workType                        勤務種類
	 * @param flexCalcMethod                  フレックス勤務の設定
	 * @param vacationClass                   休暇クラス
	 * @param siftCode                        就業時間帯コード
	 * @param eachWorkTimeSet                 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet              会社別代休時間設定
	 * @param flexPreAppTime                  事前フレ
	 * @param conditionItem                   労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param coreTimeSetting                 コアタイム時間帯設定
	 * @param beforeApplicationTime           事前深夜時間
	 * @param declareResult                   申告時間帯作成結果
	 * @return 日別実績の残業時間
	 */
	public static OverTimeOfDaily calculationTime(ManageReGetClass recordReGet, WorkType workType,
			Optional<SettingOfFlexWork> flexCalcMethod, VacationClass vacationClass, Optional<WorkTimeCode> siftCode,
			AttendanceTime flexPreAppTime, WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			Optional<CoreTimeSetting> coreTimeSetting, AttendanceTime beforeApplicationTime,
			DeclareTimezoneResult declareResult) {

		val overTimeSheet = recordReGet.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get()
				.getOverTimeWorkSheet().get();
		// 残業枠時間帯の作成
		val overTimeFrameTimeSheet = overTimeSheet.changeOverTimeFrameTimeSheet(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(), workType.getCompanyId(),
				recordReGet.getIntegrationOfDaily().getCalAttr().getOvertimeSetting(), workType,
				siftCode.map(x -> x.v()), recordReGet.getIntegrationOfDaily(), recordReGet.getStatutoryFrameNoList(),
				true, recordReGet.getCompanyCommonSetting().getOvertimeFrameList());
		// 残業時間の計算
		val overTimeFrame = overTimeSheet.collectOverTimeWorkTime(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(), workType.getCompanyId(),
				recordReGet.getIntegrationOfDaily().getCalAttr().getOvertimeSetting(), workType,
				siftCode.map(x -> x.v()), recordReGet.getIntegrationOfDaily(), recordReGet.getStatutoryFrameNoList(),
				declareResult, true, recordReGet.getCompanyCommonSetting().getOvertimeFrameList());
		// 残業深夜時間の計算
		val excessOverTimeWorkMidNightTime = Finally.of(calcExcessMidNightTime(overTimeSheet,
				recordReGet.getIntegrationOfDaily().getCalAttr().getOvertimeSetting(), beforeApplicationTime,
				recordReGet.getIntegrationOfDaily().getCalAttr(), declareResult, conditionItem, recordReGet, workType,
				vacationClass, flexCalcMethod, predetermineTimeSetByPersonInfo));
		// 変形法定内残業時間の計算
		val irregularTime = overTimeSheet.calcIrregularTime();
		// フレックス時間
		FlexTime flexTime = new FlexTime(
				TimeDivergenceWithCalculationMinusExist.sameTime(new AttendanceTimeOfExistMinus(0)),
				new AttendanceTime(0));
		// フレ時間の計算に挑戦
		if (recordReGet.getWorkTimeSetting().isPresent()
				&& recordReGet.getWorkTimeSetting().get().getWorkTimeDivision().getWorkTimeDailyAtr().isFlex()
				&& recordReGet.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet() != null) {

			val changeVariant = ((FlexWithinWorkTimeSheet) recordReGet.getCalculationRangeOfOneDay()
					.getWithinWorkingTimeSheet().get());
			// フレックス時間の計算
			flexTime = changeVariant.createWithinWorkTimeSheetAsFlex(recordReGet.getIntegrationOfDaily(),
					recordReGet.getIntegrationOfWorkTime(),
					recordReGet.getIntegrationOfDaily().getCalAttr().getFlexExcessTime().getFlexOtTime().getCalAtr(),
					workType, flexCalcMethod.get(),
					recordReGet.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc(), vacationClass,
					recordReGet.getIntegrationOfDaily().getCalAttr().getLeaveEarlySetting(),
					recordReGet.getAddSetting(), recordReGet.getHolidayAddtionSet().get(),
					recordReGet.getIntegrationOfDaily().getCalAttr().getFlexExcessTime().getFlexOtTime()
							.getUpLimitORtSet(),
					flexPreAppTime, recordReGet.getDailyUnit(), recordReGet.getWorkTimezoneCommonSet(), conditionItem,
					predetermineTimeSetByPersonInfo, NotUseAtr.NOT_USE, Optional.of(DeductionAtr.Appropriate));
		}

		val overTimeWork = new AttendanceTime(0);
		return new OverTimeOfDaily(overTimeFrameTimeSheet, overTimeFrame, excessOverTimeWorkMidNightTime, irregularTime,
				flexTime, overTimeWork);
	}

	/**
	 * 所定外深夜時間の計算 アルゴリズム：残業深夜時間の計算（事後申請制御後）
	 * 
	 * @param overTimeSheet                   残業時間帯
	 * @param autoCalcSet                     残業時間の自動計算設定
	 * @param beforeApplicationTime           事前深夜時間
	 * @param calAttr                         日別実績の計算区分
	 * @param declareResult                   申告時間帯作成結果
	 * @param conditionItem                   労働条件項目
	 * @param recordReGet                     実績
	 * @param workType                        勤務種類
	 * @param vacationClass                   休暇クラス
	 * @param flexCalcMethod                  フレックス勤務の設定
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @return 法定外残業深夜時間
	 */
	private static ExcessOverTimeWorkMidNightTime calcExcessMidNightTime(OverTimeSheet overTimeSheet,
			AutoCalOvertimeSetting autoCalcSet, AttendanceTime beforeApplicationTime, CalAttrOfDailyAttd calAttr,
			DeclareTimezoneResult declareResult, WorkingConditionItem conditionItem, ManageReGetClass recordReGet,
			WorkType workType, VacationClass vacationClass, Optional<SettingOfFlexWork> flexCalcMethod,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo) {

		AttendanceTime flexWithoutTime = new AttendanceTime(0);

		// フレックスの時
		if (recordReGet.getWorkTimeSetting().isPresent()) {
			if (recordReGet.getWorkTimeSetting().get().getWorkTimeDivision().isFlexWorkDay(conditionItem)) {
				// 所定外深夜時間の計算
				Optional<WithinWorkTimeSheet> withinWorkTimeSheetOpt = Optional.empty();
				if (recordReGet.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet().isPresent()) {
					withinWorkTimeSheetOpt = Optional
							.of(recordReGet.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet().get());
				}
				if (withinWorkTimeSheetOpt.isPresent()) {
					flexWithoutTime = ((FlexWithinWorkTimeSheet) withinWorkTimeSheetOpt.get()).calcWithoutMidnightTime(
							recordReGet.getIntegrationOfDaily(), recordReGet.getIntegrationOfWorkTime(),
							recordReGet.getIntegrationOfDaily().getCalAttr().getFlexExcessTime().getFlexOtTime()
									.getCalAtr(),
							workType, flexCalcMethod.get(),
							recordReGet.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc(), vacationClass,
							recordReGet.getIntegrationOfDaily().getCalAttr().getLeaveEarlySetting(),
							recordReGet.getAddSetting(), recordReGet.getHolidayAddtionSet().get(),
							recordReGet.getDailyUnit(), recordReGet.getWorkTimezoneCommonSet(),
							recordReGet.getIntegrationOfDaily().getCalAttr().getFlexExcessTime().getFlexOtTime()
									.getUpLimitORtSet(),
							conditionItem, predetermineTimeSetByPersonInfo, NotUseAtr.NOT_USE);
				}
			}
		}
		// 残業深夜時間の計算
		TimeDivergenceWithCalculation midnightTime = overTimeSheet.calcMidNightTime(autoCalcSet);
		midnightTime = midnightTime.addMinutes(flexWithoutTime, flexWithoutTime);
		// 事前申請制御
		if (calAttr.getOvertimeSetting().getNormalMidOtTime()
				.getUpLimitORtSet() == TimeLimitUpperLimitSetting.LIMITNUMBERAPPLICATION
				&& midnightTime.getTime().greaterThanOrEqualTo(beforeApplicationTime.valueAsMinutes())) {
			return new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation
					.createTimeWithCalculation(beforeApplicationTime, midnightTime.getCalcTime()));
		}
		if (declareResult.getCalcRangeOfOneDay().isPresent()) {
			// 申告残業深夜時間の計算
			AttendanceTime declareTime = OverTimeOfDaily.calcDeclareOvertimeMidnightTime(overTimeSheet, autoCalcSet,
					declareResult);
			if (declareTime.valueAsMinutes() > 0) {
				midnightTime.replaceTimeWithCalc(declareTime);
				// 編集状態．残業深夜 ← true
				if (declareResult.getDeclareCalcRange().isPresent()) {
					declareResult.getDeclareCalcRange().get().getEditState().setOvertimeMn(true);
				}
			}
		}
		return new ExcessOverTimeWorkMidNightTime(midnightTime);
	}

	/**
	 * 申告残業深夜時間の計算
	 * 
	 * @param overTimeSheet 残業枠時間
	 * @param autoCalcSet   残業時間の自動計算設定
	 * @param declareResult 申告時間帯作成結果
	 * @return 申告残業深夜時間
	 */
	private static AttendanceTime calcDeclareOvertimeMidnightTime(OverTimeSheet overTimeSheet,
			AutoCalOvertimeSetting autoCalcSet, DeclareTimezoneResult declareResult) {

		AttendanceTime result = new AttendanceTime(0);
		if (!declareResult.getCalcRangeOfOneDay().isPresent())
			return result;
		CalculationRangeOfOneDay declareCalcRange = declareResult.getCalcRangeOfOneDay().get();
		if (!declareResult.getDeclareCalcRange().isPresent())
			return result;
		DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
		// 枠設定を確認する
		if (calcRange.getDeclareSet().getFrameSet() == DeclareFrameSet.WORKTIME_SET) {
			// 残業深夜時間の計算（事前申請制御前）
			OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
			if (declareOutsideWork.getOverTimeWorkSheet().isPresent()) {
				OverTimeSheet declareSheet = declareOutsideWork.getOverTimeWorkSheet().get();
				result = declareSheet.calcMidNightTime(autoCalcSet).getCalcTime();
			}
			// 申告残業深夜時間を返す
			return result;
		}
		// 申告残業深夜時間の取得
		{
			// 勤務種類の取得
			if (calcRange.getWorkTypeOpt().isPresent()) {
				// 休出かどうかの判断
				if (!calcRange.getWorkTypeOpt().get().isHolidayWork()) {
					// 事前計算していた深夜時間を合計する
					result = new AttendanceTime(calcRange.getCalcTime().getEarlyOvertimeMn().valueAsMinutes()
							+ calcRange.getCalcTime().getOvertimeMn().valueAsMinutes());
				}
			}
		}
		// 申告残業深夜時間を返す
		return result;
	}

	/**
	 * 残業時間超過のチェック&エラーゲット
	 * 
	 * @param attendanceItemDictionary
	 */
	public List<EmployeeDailyPerError> checkOverTimeExcess(String employeeId, GeneralDate targetDate,
			AttendanceItemDictionaryForCalc attendanceItemDictionary, ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for (OverTimeFrameTime frameTime : this.getOverTimeWorkFrameTime()) {
			if (frameTime.isOverLimitDivergenceTime()) {
				// 残業時間
				attendanceItemDictionary.findId("残業時間" + frameTime.getOverWorkFrameNo().v()).ifPresent(
						itemId -> returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(),
								employeeId, targetDate, errorCode, itemId)));
//				//振替時間
//				attendanceItemDictionary.findId("振替残業時間"+frameTime.getOverWorkFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}

	/**
	 * 事前残業申請超過のチェック&エラーゲット
	 */
	public List<EmployeeDailyPerError> checkPreOverTimeExcess(String employeeId, GeneralDate targetDate,
			AttendanceItemDictionaryForCalc attendanceItemDictionary, ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for (OverTimeFrameTime frameTime : this.getOverTimeWorkFrameTime()) {
			if (frameTime.isPreOverLimitDivergenceTime()) {
				// 残業時間
				attendanceItemDictionary.findId("残業時間" + frameTime.getOverWorkFrameNo().v()).ifPresent(
						itemId -> returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(),
								employeeId, targetDate, errorCode, itemId)));
//				//振替時間
//				attendanceItemDictionary.findId("振替残業時間"+frameTime.getOverWorkFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}

	/**
	 * フレ超過のチェック＆エラーゲット
	 * 
	 * @return
	 */
	public List<EmployeeDailyPerError> checkFlexTimeExcess(String employeeId, GeneralDate targetDate, String searchWord,
			AttendanceItemDictionaryForCalc attendanceItemDictionary, ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if (this.getFlexTime().isOverLimitDivergenceTime()) {
			val itemId = attendanceItemDictionary.findId(searchWord);
			if (itemId.isPresent())
				returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate,
						errorCode, itemId.get()));
		}
		return returnErrorList;
	}

	/**
	 * 事前フレ申請超過のチェック＆エラーゲット
	 * 
	 * @return
	 */
	public List<EmployeeDailyPerError> checkPreFlexTimeExcess(String employeeId, GeneralDate targetDate,
			String searchWord, AttendanceItemDictionaryForCalc attendanceItemDictionary,
			ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if (this.getFlexTime().isPreOverLimitDivergenceTime()) {
			val itemId = attendanceItemDictionary.findId(searchWord);
			if (itemId.isPresent())
				returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate,
						errorCode, itemId.get()));
		}
		return returnErrorList;
	}

	/**
	 * 実績深夜時間のチェック＆エラーゲット
	 * 
	 * @return
	 */
	public List<EmployeeDailyPerError> checkNightTimeExcess(String employeeId, GeneralDate targetDate,
			String searchWord, AttendanceItemDictionaryForCalc attendanceItemDictionary,
			ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if (this.getExcessOverTimeWorkMidNightTime().isPresent()
				&& this.getExcessOverTimeWorkMidNightTime().get().isOverLimitDivergenceTime()) {
			val itemId = attendanceItemDictionary.findId(searchWord);
			if (itemId.isPresent())
				returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate,
						errorCode, itemId.get()));
		}
		return returnErrorList;
	}

	// 大塚モード計算
	/**
	 * 
	 * @param actualWorkTime             実働就業時間
	 * @param dailyUnit
	 * @param predetermineTimeSetForCalc
	 * @param finally1
	 * @param optional
	 * @param unUseBreakTime             休憩未取得時間
	 * @param predetermineTime           1日の所定時間
	 * @param ootsukaFixedCalcSet        大塚固定計算設定
	 */
	public void calcOotsukaOverTime(AttendanceTime actualWorkTime, AttendanceTime unUseBreakTime,
			AttendanceTime annualAddTime, AttendanceTime predTime, Optional<FixedWorkCalcSetting> ootsukaFixedCalcSet,
			AutoCalOvertimeSetting autoCalcSet, DailyUnit dailyUnit, Optional<TimezoneOfFixedRestTimeSet> restTimeSheet,
			Finally<WithinWorkTimeSheet> withinWorkTimeSheet, PredetermineTimeSetForCalc predetermineTimeSetForCalc) {
		if (ootsukaFixedCalcSet != null && ootsukaFixedCalcSet.isPresent()) {
			// 休憩未取得時間から残業時間計算
			calcOverTimeFromUnuseTime(actualWorkTime, unUseBreakTime,
					ootsukaFixedCalcSet.get().getOverTimeCalcNoBreak(), dailyUnit, predTime);
			// 所定時間を超過した残業時間を計算
			calcOverTimeFromOverPredTime(actualWorkTime, unUseBreakTime, annualAddTime,
					predetermineTimeSetForCalc.getAdditionSet().getPredTime().getOneDay(),
					ootsukaFixedCalcSet.get().getExceededPredAddVacationCalc(), autoCalcSet, restTimeSheet,
					withinWorkTimeSheet);
		}
	}

	/**
	 * 休憩未取得時間から残業時間計算
	 * 
	 * @param predTime
	 * @param dailyUnit
	 */
	private void calcOverTimeFromUnuseTime(AttendanceTime actualWorkTime, AttendanceTime unUseBreakTime,
			OverTimeCalcNoBreak ootsukaFixedCalcSet, DailyUnit dailyUnit, AttendanceTime predTime) {
		// 就業時間として計算か判定
		if (ootsukaFixedCalcSet == null || ootsukaFixedCalcSet.getCalcMethod() == null
				|| ootsukaFixedCalcSet.getCalcMethod().isCalcAsWorking() || ootsukaFixedCalcSet.getInLawOT() == null
				|| ootsukaFixedCalcSet.getNotInLawOT() == null)
			return;
		// 法定労働時間を取得
		val statutoryTime = dailyUnit.getDailyTime();
		// 既に計算されてきた残業枠Noの一覧
		val frameNoList = this.overTimeWorkFrameTime.stream().map(tc -> tc.getOverWorkFrameNo())
				.collect(Collectors.toList());
		// 実働就業<=法定労働(法定内)
		if (actualWorkTime.lessThanOrEqualTo(statutoryTime.v())) {
			if (unUseBreakTime.greaterThan(0)) {
				if (frameNoList.contains(new OverTimeFrameNo(ootsukaFixedCalcSet.getInLawOT().v()))) {
					this.overTimeWorkFrameTime.forEach(tc -> {
						if (tc.getOverWorkFrameNo().v().equals(ootsukaFixedCalcSet.getInLawOT().v()))
							tc.setOverTimeWork(TimeDivergenceWithCalculation.createTimeWithCalculation(
									tc.getOverTimeWork().getTime().addMinutes(unUseBreakTime.valueAsMinutes()),
									tc.getOverTimeWork().getCalcTime()));
					});
				} else {
					this.overTimeWorkFrameTime
							.add(new OverTimeFrameTime(new OverTimeFrameNo(ootsukaFixedCalcSet.getInLawOT().v()),
									TimeDivergenceWithCalculation.createTimeWithCalculation(unUseBreakTime,
											unUseBreakTime),
									TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)),
									new AttendanceTime(0), new AttendanceTime(0)));
				}
			}
		}
		// 実働就業>法定労働(法定外)
		else {
			// 法内
			final int calcUnbreakTime = unUseBreakTime
					.minusMinutes(actualWorkTime.valueAsMinutes() - statutoryTime.valueAsMinutes()).valueAsMinutes();
			if (frameNoList.contains(new OverTimeFrameNo(ootsukaFixedCalcSet.getInLawOT().v()))) {
				this.overTimeWorkFrameTime.forEach(tc -> {
					if (tc.getOverWorkFrameNo().v().equals(ootsukaFixedCalcSet.getInLawOT().v()))
						tc.setOverTimeWork(TimeDivergenceWithCalculation.createTimeWithCalculation(
								tc.getOverTimeWork().getTime().addMinutes(calcUnbreakTime),
								tc.getOverTimeWork().getCalcTime()));
				});
			} else {
				this.overTimeWorkFrameTime
						.add(new OverTimeFrameTime(new OverTimeFrameNo(ootsukaFixedCalcSet.getInLawOT().v()),
								TimeDivergenceWithCalculation.sameTime(new AttendanceTime(calcUnbreakTime)),
								TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), new AttendanceTime(0),
								new AttendanceTime(0)));
			}
			frameNoList.add(new OverTimeFrameNo(ootsukaFixedCalcSet.getInLawOT().v()));
			// 法外
			final AttendanceTime excessOverTime = actualWorkTime.minusMinutes(statutoryTime.valueAsMinutes())
					.valueAsMinutes() > unUseBreakTime.v() ? unUseBreakTime
							: actualWorkTime.minusMinutes(statutoryTime.valueAsMinutes());
			if (frameNoList.contains(new OverTimeFrameNo(ootsukaFixedCalcSet.getNotInLawOT().v()))) {
				this.overTimeWorkFrameTime.forEach(tc -> {
					if (tc.getOverWorkFrameNo().v().equals(ootsukaFixedCalcSet.getNotInLawOT().v()))
						tc.setOverTimeWork(TimeDivergenceWithCalculation.createTimeWithCalculation(
								tc.getOverTimeWork().getTime().addMinutes(excessOverTime.valueAsMinutes()),
								tc.getOverTimeWork().getCalcTime()));
				});
			} else {
				this.overTimeWorkFrameTime.add(new OverTimeFrameTime(
						new OverTimeFrameNo(ootsukaFixedCalcSet.getNotInLawOT().v()),
						TimeDivergenceWithCalculation.sameTime(
								unUseBreakTime.minusMinutes(excessOverTime.valueAsMinutes()).valueAsMinutes() < 0
										? new AttendanceTime(0)
										: excessOverTime),
						TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), new AttendanceTime(0),
						new AttendanceTime(0)));
			}
		}
	}

	/**
	 * 所定時間を超過した残業時間を計算
	 * 
	 * @param restTimeSheet
	 * @param withinWorkTimeSheet
	 * @param actualWorkTime      実働時間
	 * @param unUseBreakTime      未使用休憩時間
	 * @param annualAddTime       年休加算時間
	 * @param oneDayPredTime      1日の所定時間
	 * @param ootsukaFixedCalcSet 大塚固定計算設定
	 * @param autoCalcSet         残業時間の自動計算設定
	 */
	private void calcOverTimeFromOverPredTime(AttendanceTime actualWorkTime, AttendanceTime unUseBreakTime,
			AttendanceTime annualAddTime, AttendanceTime oneDayPredTime,
			ExceededPredAddVacationCalc ootsukaFixedCalcSet, AutoCalOvertimeSetting autoCalcSet,
			Optional<TimezoneOfFixedRestTimeSet> restTimeSheet, Finally<WithinWorkTimeSheet> withinWorkTimeSheet) {
		// AttendanceTime breakTimeInWithinTimeSheet =
		// getBreakTimeInWithin(withinWorkTimeSheet,restTimeSheet);

		final AttendanceTime totalWorkTime = new AttendanceTime(
				actualWorkTime.valueAsMinutes() + annualAddTime.valueAsMinutes() - unUseBreakTime.valueAsMinutes());

		final AttendanceTime withinOverTime = totalWorkTime.greaterThan(oneDayPredTime.valueAsMinutes())
				? totalWorkTime.minusMinutes(oneDayPredTime.valueAsMinutes())
				: new AttendanceTime(0);

		// 就業時間として計算か判定
		if (ootsukaFixedCalcSet == null || ootsukaFixedCalcSet.getCalcMethod() == null
				|| ootsukaFixedCalcSet.getCalcMethod().isCalcAsWorking() || ootsukaFixedCalcSet.getOtFrameNo() == null)
			return;

		val frameNoList = this.overTimeWorkFrameTime.stream().map(tc -> tc.getOverWorkFrameNo())
				.collect(Collectors.toList());

		// 一旦、普通を見るようにする
		// 打刻から計算する
		if (autoCalcSet.decisionCalcAtr(StatutoryAtr.Statutory, false)) {
			if (withinOverTime.greaterThan(0)) {
				if (frameNoList.contains(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v()))) {
					this.overTimeWorkFrameTime.forEach(tc -> {
						if (tc.getOverWorkFrameNo().equals(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v())))
							tc.setOverTimeWork(TimeDivergenceWithCalculation.createTimeWithCalculation(
									tc.getOverTimeWork().getTime().addMinutes(withinOverTime.valueAsMinutes()),
									tc.getOverTimeWork().getCalcTime().addMinutes(withinOverTime.valueAsMinutes())));
					});
				} else {
					this.overTimeWorkFrameTime
							.add(new OverTimeFrameTime(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v()),
									TimeDivergenceWithCalculation.sameTime(withinOverTime),
									TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)),
									new AttendanceTime(0), new AttendanceTime(0)));
				}
			}
		}
		// 上記条件以外
		else {
			if (frameNoList.contains(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v()))) {
				this.overTimeWorkFrameTime.forEach(tc -> {
					if (tc.getOverWorkFrameNo().equals(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v())))
						tc.setOverTimeWork(TimeDivergenceWithCalculation.createTimeWithCalculation(
								tc.getOverTimeWork().getTime(),
								tc.getOverTimeWork().getCalcTime().addMinutes(withinOverTime.valueAsMinutes())));
				});
			} else {
				this.overTimeWorkFrameTime
						.add(new OverTimeFrameTime(new OverTimeFrameNo(ootsukaFixedCalcSet.getOtFrameNo().v()),
								TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),
										withinOverTime),
								TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), new AttendanceTime(0),
								new AttendanceTime(0)));
			}

		}

	}

	/**
	 * 就内時間帯に含まれている休憩時間の計算
	 * 
	 * @param withinWorkTimeSheet 就業時間帯
	 * @param restTimeSheetSet    就業時間帯マスタの休憩時間帯
	 * @return 取得した休憩時間
	 */
	@SuppressWarnings("unused")
	private AttendanceTime getBreakTimeInWithin(Finally<WithinWorkTimeSheet> withinWorkTimeSheet,
			Optional<TimezoneOfFixedRestTimeSet> restTimeSheetSet) {
		if (!restTimeSheetSet.isPresent() || withinWorkTimeSheet == null || !withinWorkTimeSheet.isPresent())
			return new AttendanceTime(0);
		AttendanceTime restTime = new AttendanceTime(0);
		for (DeductionTime restTimeSheet : restTimeSheetSet.get().getTimezones()) {
			restTime = restTime.addMinutes(withinWorkTimeSheet.get().getDupRestTime(restTimeSheet).valueAsMinutes());
		}
		return restTime;
	}

	/**
	 * 乖離時間のみ再計算
	 * 
	 * @return
	 */
	public OverTimeOfDaily calcDiverGenceTime() {
		List<OverTimeFrameTime> OverTimeFrameList = new ArrayList<>();
		for (OverTimeFrameTime overTimeFrameTime : this.overTimeWorkFrameTime) {
			overTimeFrameTime.calcDiverGenceTime();
			OverTimeFrameList.add(overTimeFrameTime);
		}
		FlexTime flexTime = this.flexTime != null ? this.flexTime.calcDiverGenceTime() : this.flexTime;
		Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeMidNight = this.excessOverTimeWorkMidNightTime.isPresent()
				? Finally.of(this.excessOverTimeWorkMidNightTime.get().calcDiverGenceTime())
				: this.excessOverTimeWorkMidNightTime;
		return new OverTimeOfDaily(this.overTimeWorkFrameTimeSheet, OverTimeFrameList, excessOverTimeMidNight,
				this.irregularWithinPrescribedOverTimeWork, flexTime, this.overTimeWorkSpentAtWork);
	}

	/**
	 * マイナスの乖離時間を0にする
	 * 
	 * @param overtimeFrameTimeList 残業枠時間リスト
	 */
	public static void divergenceMinusValueToZero(List<OverTimeFrameTime> overtimeFrameTimeList) {

		// 大塚モードの確認
		if (AppContexts.optionLicense().customize().ootsuka() == false)
			return;

		// マイナスの乖離時間を0にする
		for (val overtimeFrameTime : overtimeFrameTimeList) {
			overtimeFrameTime.getOverTimeWork().divergenceMinusValueToZero();
			overtimeFrameTime.getTransferTime().divergenceMinusValueToZero();
		}
	}

	// PCログインログオフから計算した計算時間を入れる(大塚モードのみ)
	public void setPCLogOnValue(Map<OverTimeFrameNo, OverTimeFrameTime> map) {
		Map<OverTimeFrameNo, OverTimeFrameTime> changeList = convertOverMap(this.getOverTimeWorkFrameTime());

		for (int frameNo = 1; frameNo <= 10; frameNo++) {

			// 値更新
			if (changeList.containsKey(new OverTimeFrameNo(frameNo))) {
				val getframe = changeList.get(new OverTimeFrameNo(frameNo));
				if (map.containsKey(new OverTimeFrameNo(frameNo))) {
					// 残業時間の置き換え
					if (getframe.getOverTimeWork() != null
							&& map.get(new OverTimeFrameNo(frameNo)).getOverTimeWork() != null) {
						getframe.getOverTimeWork().replaceTimeAndCalcDiv(
								map.get(new OverTimeFrameNo(frameNo)).getOverTimeWork().getCalcTime());
					} else {
						getframe.setOverTimeWork(TimeDivergenceWithCalculation
								.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0)));
					}
					// 振替時間の置き換え
					if (getframe.getTransferTime() != null
							&& map.get(new OverTimeFrameNo(frameNo)).getTransferTime() != null) {
						getframe.getTransferTime().replaceTimeAndCalcDiv(
								map.get(new OverTimeFrameNo(frameNo)).getTransferTime().getCalcTime());
					} else {
						getframe.setTransferTime(TimeDivergenceWithCalculation
								.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0)));
					}
				} else {
					// 残業時間の置き換え
					getframe.getOverTimeWork().replaceTimeAndCalcDiv(new AttendanceTime(0));
					// 振替時間の置き換え
					getframe.getTransferTime().replaceTimeAndCalcDiv(new AttendanceTime(0));
				}
				changeList.remove(new OverTimeFrameNo(frameNo));
				changeList.put(new OverTimeFrameNo(frameNo), getframe);
			}
			// リストへ追加
			else {
				if (map.containsKey(new OverTimeFrameNo(frameNo))) {
					changeList.put(new OverTimeFrameNo(frameNo),
							new OverTimeFrameTime(new OverTimeFrameNo(frameNo),
									TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),
											map.get(new OverTimeFrameNo(frameNo)).getOverTimeWork().getCalcTime()),
									TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),
											map.get(new OverTimeFrameNo(frameNo)).getTransferTime().getCalcTime()),
									new AttendanceTime(0), new AttendanceTime(0)));
				}
			}
		}
		this.overTimeWorkFrameTime = new ArrayList<>(changeList.values());
	}

	private Map<OverTimeFrameNo, OverTimeFrameTime> convertOverMap(List<OverTimeFrameTime> overTimeWorkFrameTime) {
		Map<OverTimeFrameNo, OverTimeFrameTime> map = new HashMap<>();
		for (OverTimeFrameTime ot : overTimeWorkFrameTime) {
			map.put(ot.getOverWorkFrameNo(), ot);
		}
		return map;
	}

	public void transWithinOverTimeForOOtsukaSpecialHoliday(List<OverTimeOfTimeZoneSet> overTimeSheetByWorkTimeMaster,
			AttendanceTime withinOverTime) {
		AttendanceTime copyWithinOverTime = withinOverTime;
		List<OverTimeOfTimeZoneSet> sortedOverTimeZoneSet = overTimeSheetByWorkTimeMaster;
		if (overTimeSheetByWorkTimeMaster.size() > 0) {
			boolean nextLoopFlag = true;
			while (nextLoopFlag) {
				for (int index = 0; index <= sortedOverTimeZoneSet.size(); index++) {
					if (index == sortedOverTimeZoneSet.size() - 1) {
						nextLoopFlag = false;
						break;
					}
					// 精算順序の比較
					if (sortedOverTimeZoneSet.get(index).getSettlementOrder()
							.greaterThan(sortedOverTimeZoneSet.get(index + 1).getSettlementOrder())) {
						OverTimeOfTimeZoneSet pary = sortedOverTimeZoneSet.get(index);
						sortedOverTimeZoneSet.set(index, sortedOverTimeZoneSet.get(index + 1));
						sortedOverTimeZoneSet.set(index + 1, pary);
						break;
					}
					// 枠の比較(精算順序同じケースの整頓)
					else if (sortedOverTimeZoneSet.get(index).getSettlementOrder()
							.equals(sortedOverTimeZoneSet.get(index + 1).getSettlementOrder())) {
						if (sortedOverTimeZoneSet.get(index).getTimezone().getStart()
								.greaterThan(sortedOverTimeZoneSet.get(index + 1).getTimezone().getStart())) {
							OverTimeOfTimeZoneSet pary = sortedOverTimeZoneSet.get(index);
							sortedOverTimeZoneSet.set(index, sortedOverTimeZoneSet.get(index + 1));
							sortedOverTimeZoneSet.set(index + 1, pary);
							break;
						}
					}

				}
			}
		}

		for (OverTimeOfTimeZoneSet set : sortedOverTimeZoneSet) {
			// 全残業枠の残業時間＋振出時間の合計
			Optional<AttendanceTime> transAndOverTime = overTimeWorkFrameTime.stream()
					.filter(tc -> tc.getOverWorkFrameNo().compareTo(set.getOtFrameNo().v()) == 0).map(ts -> ts
							.getOverTimeWork().getTime().addMinutes(ts.getTransferTime().getTime().valueAsMinutes()))
					.findFirst();
			AttendanceTime transTime = new AttendanceTime(0);
			if (transAndOverTime.isPresent() && copyWithinOverTime.greaterThan(transAndOverTime.get())) {
				transTime = transAndOverTime.get();
			} else {
				transTime = copyWithinOverTime;
			}

			final int toTime = transTime.valueAsMinutes();
			// 減算
			overTimeWorkFrameTime.forEach(tc -> {
				if (tc.getOverWorkFrameNo().compareTo(set.getOtFrameNo().v()) == 0) {
					tc.minusTimeResultGreaterEqualZero(new AttendanceTime(toTime));
				}
			});
			// 加算
			Optional<OverTimeFrameNo> forcsWithin = overTimeWorkFrameTime.stream()
					.filter(tc -> tc.getOverWorkFrameNo().compareTo(set.getLegalOTframeNo().v()) == 0)
					.map(ts -> ts.getOverWorkFrameNo()).findFirst();
			// 既存枠がある
			if (forcsWithin.isPresent()) {
				overTimeWorkFrameTime.forEach(ts -> {
					if (ts.getOverWorkFrameNo().compareTo(forcsWithin.get()) == 0) {
						ts.add(new AttendanceTime(toTime));
					}
				});
			}
			// 既存枠がない
			else {
				this.overTimeWorkFrameTime.add(new OverTimeFrameTime(new OverTimeFrameNo(set.getLegalOTframeNo().v()),
						TimeDivergenceWithCalculation.sameTime(new AttendanceTime(toTime)),
						TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), new AttendanceTime(0),
						new AttendanceTime(0)));
			}

			copyWithinOverTime = copyWithinOverTime.minusMinutes(toTime);

			if (copyWithinOverTime.lessThan(0))
				break;
		}

	}

	public void mergeOverTimeList(List<OverTimeFrameTime> frameTimeList) {
		for (OverTimeFrameTime frameTime : frameTimeList) {
			if (this.overTimeWorkFrameTime.stream()
					.filter(tc -> tc.getOverWorkFrameNo().equals(frameTime.getOverWorkFrameNo())).findFirst()
					.isPresent()) {
				this.overTimeWorkFrameTime.stream()
						.filter(tc -> tc.getOverWorkFrameNo().equals(frameTime.getOverWorkFrameNo())).findFirst()
						.ifPresent(ts -> {
							ts = ts.addOverTime(frameTime.getOverTimeWork().getTime(),
									frameTime.getOverTimeWork().getCalcTime());
							ts = ts.addTransoverTime(frameTime.getTransferTime().getTime(),
									frameTime.getTransferTime().getCalcTime());
						});
			} else {
				this.overTimeWorkFrameTime.add(frameTime);
			}
		}
	}

	// 事前申請時間の前にデフォルトを作成
	public static OverTimeOfDaily createDefaultBeforeApp(List<Integer> lstNo) {
		List<OverTimeFrameTime> workFrameTime = lstNo.stream().map(x -> {
			return new OverTimeFrameTime(new OverTimeFrameNo(x), TimeDivergenceWithCalculation.emptyTime(),
					TimeDivergenceWithCalculation.emptyTime(), new AttendanceTime(0), new AttendanceTime(0));
		}).collect(Collectors.toList());
		return new OverTimeOfDaily(new ArrayList<>(), workFrameTime, Finally.empty());
	}

	@Override
	public OverTimeOfDaily clone() {
		// 残業枠時間帯
		List<OverTimeFrameTimeSheet> overTimeWorkFrameTimeSheetClone = this.overTimeWorkFrameTimeSheet.stream()
				.map(x -> x.clone()).collect(Collectors.toList());
		// 残業枠時間
		List<OverTimeFrameTime> overTimeWorkFrameTimeClone = this.overTimeWorkFrameTime.stream().map(x -> x.clone())
				.collect(Collectors.toList());
		// 法定外深夜時間 (所定外深夜時間)
		Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeWorkMidNightTimeClone = excessOverTimeWorkMidNightTime
				.isPresent() ? Finally.of(excessOverTimeWorkMidNightTime.get().clone()) : Finally.empty();
		// 残業拘束時間
		AttendanceTime overTimeWorkClone = new AttendanceTime(overTimeWorkSpentAtWork.v());
		// 変形法定内残業
		AttendanceTime irregularTimeClone = new AttendanceTime(irregularWithinPrescribedOverTimeWork.v());
		// フレックス時間
		FlexTime flexTimeClone = flexTime.clone();

		return new OverTimeOfDaily(overTimeWorkFrameTimeSheetClone, overTimeWorkFrameTimeClone,
				excessOverTimeWorkMidNightTimeClone, irregularTimeClone, flexTimeClone, overTimeWorkClone);
	}

	//事前申請時間から代休振替を行うか判断する
	public boolean tranferOvertimeCompenCall(SubsTransferProcessMode processMode) {
		AttendanceTime sumOverTime = calcTotalFrameTime();
		AttendanceTime sumOverTranferTime = calcTransTotalFrameTime();
		AttendanceTime sumApp = calcTotalAppTime();
		if ((sumOverTime.valueAsMinutes() + sumOverTranferTime.valueAsMinutes()) <= 0
				&& processMode == SubsTransferProcessMode.DAILY && sumApp.valueAsMinutes() > 0) {
			return true;
		}
		return false;
	}
}
