package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.attendanceitem.StoredProcdureProcess;
import nts.uk.ctx.at.record.dom.attendanceitem.StoredProcdureProcessing.DailyStoredProcessResult;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.converter.CalcDefaultValue;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck.CalculationErrorCheckService;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck.DailyRecordCreateErrorAlermService;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.ootsuka.OotsukaProcessService;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus.MasterShareContainer;
import nts.uk.ctx.at.shared.dom.calculationattribute.enums.DivergenceTimeAttr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.BreakDownTimeDay;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalFlexOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.deviationtime.AutoCalcSetOfDivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.breaktime.BreakTimeSheetGetter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.FlexCalcMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.FlexCalcMethodOfEachPremiumHalfWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.FlexCalcMethodOfHalfWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.TimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PreviousAndNextDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.SchedulePerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrder;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.JustCorrectionAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkCalcSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetermineTime;
import nts.uk.ctx.at.shared.dom.worktime.predset.PrescribedTimezoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDivision;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CalculateDailyRecordServiceImpl implements CalculateDailyRecordService {

	/* 勤務種類 */
	@Inject
	private WorkTypeRepository workTypeRepository;
	/* 所定時間帯 */
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSetRepository;
	/* 就業時間帯設定 */
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	/* 固定勤務設定 */
	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;
	/* 流動勤務設定 */
	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;

	/* フレックス勤務設定 */
	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;

	/* 勤怠項目と勤怠項目の実際の値のマッピング */
	@Inject
	private AttendanceItemConvertFactory attendanceItemConvertFactory;

	/* 大塚ｶｽﾀﾏｲｽﾞ専用処理 */
	@Inject
	private OotsukaProcessService ootsukaProcessService;

	/* エラーチェック処理 */
	@Inject
	private CalculationErrorCheckService calculationErrorCheckService;

	// 任意項目の計算の為に追加
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	// 日別作成側にあったエラーチェック処理
	private DailyRecordCreateErrorAlermService dailyRecordCreateErrorAlermService;

	@Inject
	private StoredProcdureProcess storedProcdureProcess;
	
	/**
	 * 勤務情報を取得して計算
	 * 
	 * @param calculateOption
	 * @param integrationOfDaily(日別実績(WORK))
	 * @param companyCommonSetting
	 *            計算をするための会社共通設定
	 * @param personCommonSetting
	 *            計算をするための個人共通設定
	 * @param justCorrectionAtr
	 *           ジャスト補正区分
	 * @param yesterDayInfo
	 *            前日の勤務情報
	 * @param tomorrowDayInfo
	 *            翌日の勤務情報
	 * @return 日別実績(Work)
	 */
	@Override
	public ManageCalcStateAndResult calculate(CalculateOption calculateOption, IntegrationOfDaily integrationOfDaily,
			ManagePerCompanySet companyCommonSetting, ManagePerPersonDailySet personCommonSetting,JustCorrectionAtr justCorrectionAtr,
			Optional<WorkInfoOfDailyPerformance> yesterDayInfo, Optional<WorkInfoOfDailyPerformance> tomorrowDayInfo) {

		DailyRecordToAttendanceItemConverter converter = attendanceItemConvertFactory.createDailyConverter();
		// 計算できる状態にあるかのチェック①(勤務情報、会社共通の設定、個人共通の設定)
		if (integrationOfDaily.getAffiliationInfor() == null || companyCommonSetting == null
				|| personCommonSetting == null) {
			return ManageCalcStateAndResult.failCalc(integrationOfDaily, attendanceItemConvertFactory);
		}

		// 20190607
		// boolean isShareContainerNotInit = companyCommonSetting.getShareContainer() ==
		// null;
		// if (isShareContainerNotInit) {
		// companyCommonSetting.setShareContainer(MasterShareBus.open());
		// }
		// 実績データの計算
		ManageCalcStateAndResult result = this.calcDailyAttendancePerformance(integrationOfDaily, companyCommonSetting,
				personCommonSetting, converter, justCorrectionAtr,yesterDayInfo, tomorrowDayInfo);

		if (!calculateOption.isSchedule()) {
			// 大塚モードの処理
			// val afterOOtsukaModeCalc =
			// replaceStampForOOtsuka(result.getIntegrationOfDaily(), companyCommonSetting,
			// personCommonSetting, tomorrowDayInfo, tomorrowDayInfo, converter);

			// 任意項目の計算
			result.setIntegrationOfDaily(
					this.calcOptionalItem(result.getIntegrationOfDaily(), converter, companyCommonSetting,personCommonSetting));
		}

		if (!calculateOption.isMasterTime()) {
			// エラーチェック
			if(integrationOfDaily != null) {
			result.getIntegrationOfDaily().setEmployeeId(integrationOfDaily.getEmployeeId());
			result.getIntegrationOfDaily().setYmd((integrationOfDaily.getYmd()));
			}
			result.setIntegrationOfDaily(calculationErrorCheckService.errorCheck(result.getIntegrationOfDaily(),
					personCommonSetting, companyCommonSetting));
		}
		// 20190607
		// if (isShareContainerNotInit) {
		// companyCommonSetting.getShareContainer().clearAll();
		// }

		return result;
	}

	private ManageCalcStateAndResult calcDailyAttendancePerformance(IntegrationOfDaily integrationOfDaily,
			ManagePerCompanySet companyCommonSetting, ManagePerPersonDailySet personCommonSetting,
			DailyRecordToAttendanceItemConverter converter, JustCorrectionAtr justCorrectionAtr,
			Optional<WorkInfoOfDailyPerformance> yesterDayInfo,Optional<WorkInfoOfDailyPerformance> tomorrowDayInfo) {
		
		// 編集状態から「申告反映」のデータを削除する
		integrationOfDaily.removeEditStateForDeclare();
		
		// 個人情報「労働制」を取得 
		if (personCommonSetting.getPersonInfo().getLaborSystem() == WorkingSystem.EXCLUDED_WORKING_CALCULATE){
			// 就業計算対象外処理
			return ManageCalcStateAndResult.successCalcForNoCalc(integrationOfDaily, attendanceItemConvertFactory);
		}
		
		// 出退勤打刻順序不正のチェック
		// ※他の打刻順序不正は計算処理を実施する必要があるため、ここでは弾かない
		// 不正の場合、勤務情報の計算ステータス→未計算にしつつ、エラーチェックは行う必要有）
		val stampIncorrectError = dailyRecordCreateErrorAlermService.stampIncorrect(integrationOfDaily);
		val lackOfstampError = dailyRecordCreateErrorAlermService.lackOfTimeLeavingStamping(integrationOfDaily);
		//if (stampIncorrectError != null || lackOfstampError != null) {
		if (stampIncorrectError.isPresent() || (!lackOfstampError.isEmpty() && lackOfstampError.get(0) != null)) {
			return ManageCalcStateAndResult.failCalc(integrationOfDaily, attendanceItemConvertFactory);
		}
		
		Optional<SchedulePerformance> schedule = createSchedule(
				integrationOfDaily, 
				companyCommonSetting,
				personCommonSetting,
				converter,
				justCorrectionAtr,
				yesterDayInfo,
				tomorrowDayInfo);
		
		//日別勤怠（Work）をcloneする
		IntegrationOfDaily clonedIntegrationOfDaily = converter.setData(integrationOfDaily).toDomain();
				
		//勤務種類の取得
		Optional<WorkType> workType = this.getWorkTypeFromShareContainer(
				companyCommonSetting.getShareContainer(),
				AppContexts.user().companyId(),
				clonedIntegrationOfDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v());
		
		//統合就業時間帯の作成
		Optional<IntegrationOfWorkTime> integrationOfWorkTime = this.createIntegrationOfWorkTime(
				companyCommonSetting.getShareContainer(),
				AppContexts.user().companyId(),
				personCommonSetting,
				clonedIntegrationOfDaily.getWorkInformation());
		
		// 時間帯の作成
		Optional<CalculationRangeOfOneDay> record = createRecord(
				clonedIntegrationOfDaily,
				TimeSheetAtr.RECORD,
				companyCommonSetting,
				personCommonSetting,
				justCorrectionAtr,
				yesterDayInfo,
				tomorrowDayInfo,
				workType,
				integrationOfWorkTime,
				schedule);

		if ((!record.isPresent() || !schedule.isPresent())
				|| (!integrationOfWorkTime.isPresent() && !schedule.get().getIntegrationOfWorkTime().isPresent())) {
			return ManageCalcStateAndResult.failCalc(integrationOfDaily, attendanceItemConvertFactory);
		}
		
		// 申告時間帯の作成
		DeclareTimezoneResult declare = createDeclare(
				converter,
				record,
				clonedIntegrationOfDaily,
				TimeSheetAtr.RECORD,
				companyCommonSetting,
				personCommonSetting,
				justCorrectionAtr,
				yesterDayInfo,
				tomorrowDayInfo,
				workType,
				integrationOfWorkTime,
				schedule);
		
		ManageReGetClass scheduleManageReGetClass = new ManageReGetClass(
				schedule.get().getCalculationRangeOfOneDay(),
				companyCommonSetting,
				personCommonSetting,
				schedule.get().getWorkType(),
				schedule.get().getIntegrationOfWorkTime(),
				integrationOfDaily);
		
		ManageReGetClass recordManageReGetClass = new ManageReGetClass(
				record.get(),
				companyCommonSetting,
				personCommonSetting,
				workType,
				integrationOfWorkTime,
				integrationOfDaily);

		// 実際の計算処理
		val calcResult = calcRecord(recordManageReGetClass, scheduleManageReGetClass, converter, declare);
		return ManageCalcStateAndResult.successCalc(calcResult);
	}

	/**
	 * 時間帯の作成
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeSheetAtr 実働or予定時間帯作成から呼び出されたか
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @param justCorrectionAtr ジャスト補正区分
	 * @param yesterDayInfo 前日の勤務情報
	 * @param tomorrowDayInfo 翌日の勤務情報
	 * @param workType 当日の勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param schedulePerformance 予定実績
	 * @return 1日の計算範囲
	 */
	private Optional<CalculationRangeOfOneDay> createRecord(
			IntegrationOfDaily integrationOfDaily,
			TimeSheetAtr timeSheetAtr,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting,
			JustCorrectionAtr justCorrectionAtr,
			Optional<WorkInfoOfDailyPerformance> yesterDayInfo,
			Optional<WorkInfoOfDailyPerformance> tomorrowDayInfo,
			Optional<WorkType> workType,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			Optional<SchedulePerformance> schedulePerformance) {
	
		// 休暇加算時間設定
		Optional<HolidayAddtionSet> holidayAddtionSetting = companyCommonSetting.getHolidayAdditionPerCompany();
		// 休暇加算設定が取得できなかった時のエラー
		if (!holidayAddtionSetting.isPresent()) {
			throw new BusinessException("Msg_1446");
		}

		/// 連続勤務：ＡＬＬかつＣａｎｔにしないとフレ時間がー所定時間 で算出されてしまう
		if (!workType.isPresent() || shouldTimeALLZero(integrationOfDaily, workType.get())) {
			return Optional.of(CalculationRangeOfOneDay.createEmpty(integrationOfDaily));
		}
		
		Optional<WorkTimeCode> workTimeCode = decisionWorkTimeCode(integrationOfDaily.getWorkInformation(), personCommonSetting, workType);

		/* 就業時間帯勤務区分 */
		// 1日休日の場合、就業時間帯コードはnullであるので、
		// all0を計算させるため(実績が計算できなくても、予定時間を計算する必要がある
		if (!workTimeCode.isPresent())
			return Optional.of(CalculationRangeOfOneDay.createEmpty(integrationOfDaily));
		
		if (!integrationOfWorkTime.isPresent())
			return Optional.empty();
		
		// 就業時間帯の共通設定
		Optional<WorkTimezoneCommonSet> commonSet = Optional.of(integrationOfWorkTime.get().getCommonSetting());
		
		/* 1日の計算範囲クラスを作成 */
		val oneRange = createOneDayCalculationRange(new RequireM1() {
			
			@Override
			public Optional<PredetemineTimeSetting> predetemineTimeSetting(String cid, String workTimeCode) {
				
				return getPredetermineTimeSetFromShareContainer(companyCommonSetting.getShareContainer(), cid, workTimeCode);
			}
		}, integrationOfDaily, commonSet, true, workType.get(), justCorrectionAtr, workTimeCode);
		
		/**
		 * 勤務種類が休日系なら、所定時間の時間を変更する
		 */
		if (workType.get().getDecisionAttendanceHolidayAttr()) {
			oneRange.getPredetermineTimeSetForCalc().endTimeSetStartTime();
		}
		
		MasterShareContainer<String> shareContainer = companyCommonSetting.getShareContainer();

		Optional<WorkInformation> yesterInfo = yesterDayInfo.map(c -> c.getWorkInformation().getRecordInfo());
				
		Optional<WorkInformation> tommorowInfo = tomorrowDayInfo.map(c -> c.getWorkInformation().getRecordInfo());

		Optional<FixedWorkCalcSetting> ootsukaFixedWorkSet = Optional.empty();

		switch(integrationOfWorkTime.get().getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm()) {
			case FLEX:
				if (!integrationOfWorkTime.get().getFlexWorkSetting().isPresent())
					return Optional.empty();
				
				/* フレックス勤務 */ //予定時間帯を求める為に一旦実績に変換する
				if (timeSheetAtr.isSchedule()) {
					integrationOfWorkTime.get().getFlexWorkSetting().get().getOffdayWorkTime().getRestTimezone().restoreFixRestTime(true);
					integrationOfWorkTime.get().getFlexWorkSetting().get().getRestSetting().getCommonRestSetting().changeCalcMethodToRecordUntilLeaveWork();
//					integrationOfWorkTime.get().getFlexWorkSetting().get().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting()
//							.changeCalcMethodToSchedule();

				}
	
				/* 大塚モード */
				workType = Optional.of(ootsukaProcessService.getOotsukaWorkType(workType.get(), ootsukaFixedWorkSet,
						oneRange.getAttendanceLeavingWork(),
						integrationOfWorkTime.get().getFlexWorkSetting().get().getCommonSetting().getHolidayCalculation()));
				
				/* 前日の勤務情報取得 */
				val yesterDayForFlex = getWorkTypeByWorkInfo(yesterDayInfo, workType.get(), shareContainer);
				/* 翌日の勤務情報取得 */
				val tomorrowForFlex = getWorkTypeByWorkInfo(tomorrowDayInfo, workType.get(), shareContainer);
				// 前日と翌日の勤務
				PreviousAndNextDaily previousAndNextDailyForFlex = new PreviousAndNextDaily(yesterDayForFlex, tomorrowForFlex, yesterInfo, tommorowInfo);
				
				// フレックス勤務の時間帯作成
				oneRange.createTimeSheetAsFlex(
						companyCommonSetting,
						personCommonSetting,
						workType.get(),
						integrationOfWorkTime.get(),
						integrationOfDaily,
						previousAndNextDailyForFlex);
				
				break;
				
			case FIXED:	
				if (!integrationOfWorkTime.get().getFixedWorkSetting().isPresent())
					return Optional.empty();

				// 大塚用勤務種類チェック処理の前後で勤務種類が変更になったか
				final boolean isSpecialHoliday = workType.get().getDailyWork().isOneOrHalfDaySpecHoliday();
				boolean workTypeChangedFlagForOOtsuka = ootsukaProcessService.decisionOotsukaMode(workType.get(),
						ootsukaFixedWorkSet, oneRange.getAttendanceLeavingWork(),
						integrationOfWorkTime.get().getFixedWorkSetting().get().getCommonSetting().getHolidayCalculation());
				/* 大塚モード */
				workType = Optional.of(ootsukaProcessService.getOotsukaWorkType(workType.get(), ootsukaFixedWorkSet,
						oneRange.getAttendanceLeavingWork(),
						integrationOfWorkTime.get().getFixedWorkSetting().get().getCommonSetting().getHolidayCalculation()));

				// 大塚用 勤務種類が変更になった時に
				if (workTypeChangedFlagForOOtsuka && isSpecialHoliday) {
					integrationOfDaily.setCalAttr(
							integrationOfDaily.getCalAttr().reCreate(AutoCalAtrOvertime.APPLYMANUALLYENTER));
				}
				
				ootsukaFixedWorkSet = integrationOfWorkTime.get().getFixedWorkSetting().get().getCalculationSetting();

				/* 前日の勤務情報取得 */
				val yesterDayForFixed = getWorkTypeByWorkInfo(yesterDayInfo, workType.get(), shareContainer);
				/* 翌日の勤務情報取得 */
				val tomorrowForFixed = getWorkTypeByWorkInfo(tomorrowDayInfo, workType.get(), shareContainer);
				// 前日と翌日の勤務
				PreviousAndNextDaily previousAndNextDailyForFix = new PreviousAndNextDaily(yesterDayForFixed, tomorrowForFixed, yesterInfo, tommorowInfo);
				
//				if (timeSheetAtr.isSchedule()) {
//					integrationOfWorkTime.get().getFixedWorkSetting().get().getFixedWorkRestSetting().changeCalcMethodToSche();
//				}
				
				// 固定勤務の時間帯作成
				oneRange.createWithinWorkTimeSheet(
						companyCommonSetting,
						personCommonSetting,
						workType.get(),
						integrationOfWorkTime.get(),
						integrationOfDaily,
						previousAndNextDailyForFix);
				
				// 大塚モードの判定(緊急対応)
				if (ootsukaProcessService.decisionOotsukaMode(workType.get(), ootsukaFixedWorkSet,
						oneRange.getAttendanceLeavingWork(),
						integrationOfWorkTime.get().getFixedWorkSetting().get().getCommonSetting().getHolidayCalculation()))
					oneRange.cleanLateLeaveEarlyTimeForOOtsuka();
				
				break;
				
			case FLOW:
				if (!integrationOfWorkTime.get().getFlowWorkSetting().isPresent())
					return Optional.empty();
				
				/* 前日の勤務情報取得 */
				val yesterDayForFlow = getWorkTypeByWorkInfo(yesterDayInfo, workType.get(), shareContainer);
				/* 翌日の勤務情報取得 */
				val tomorrowForFlow = getWorkTypeByWorkInfo(tomorrowDayInfo, workType.get(), shareContainer);
				// 前日と翌日の勤務
				PreviousAndNextDaily previousAndNextDailyFlow = new PreviousAndNextDaily(yesterDayForFlow, tomorrowForFlow, yesterInfo, tommorowInfo);
				
				// 流動勤務の時間帯作成
				oneRange.createFlowWork(
						companyCommonSetting,
						personCommonSetting,
						workType.get(),
						integrationOfWorkTime.get(),
						integrationOfDaily,
						previousAndNextDailyFlow,
						schedulePerformance);
				
				break;
				
			case TIMEDIFFERENCE:
				/* 時差勤務 */
				return Optional.empty();
				
			default:
				throw new RuntimeException(
						"unknown workTimeMethodSet" + integrationOfWorkTime.get().getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet());
		}
		// 勤務外短時間勤務時間帯の作成
		oneRange.createShortTimeWSWithoutWork(
				workType.get(),
				integrationOfWorkTime.get(),
				integrationOfDaily,
				companyCommonSetting,
				personCommonSetting);
		// 1日の計算範囲を返す
		return Optional.of(oneRange);
	}
	

	/**
	 * 就業時間帯コードの取得 勤務情報 > 労働条件 > 就業時間帯無と判定
	 * 
	 * @param workInfo
	 * @param personCommonSetting
	 * @param workType
	 * @return
	 */
	private Optional<WorkTimeCode> decisionWorkTimeCode(WorkInfoOfDailyAttendance workInfo,
			ManagePerPersonDailySet personCommonSetting, Optional<WorkType> workType) {
		if(!workType.isPresent() || workType.get().isNoneWorkTimeType())
			return Optional.empty();
		
		if (workInfo == null || workInfo.getRecordInfo() == null
				|| workInfo.getRecordInfo().getWorkTimeCode() == null) {
				return personCommonSetting.getPersonInfo().getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode();
		}
		return Optional.of(workInfo.getRecordInfo().getWorkTimeCode());
	}

	private boolean checkAttendanceLeaveState(Optional<TimeLeavingOfDailyAttd> attendanceLeave) {
		if (attendanceLeave.isPresent()) {
			for (TimeLeavingWork leavingWork : attendanceLeave.get().getTimeLeavingWorks()) {
				if (leavingWork.checkLeakageStamp())
					return true;
			}
		}
		return false;
	}

	/**
	 * 作成した時間帯から時間を計算する
	 * @param recordReGetClass 実績
	 * @param scheduleReGetClass 予定
	 * @param converter 日別実績コンバータ
	 * @param declareResult 申告時間帯作成結果
	 * @return 日別実績(Work)
	 */
	private IntegrationOfDaily calcRecord(
			ManageReGetClass recordReGetClass,
			ManageReGetClass scheduleReGetClass,
			DailyRecordToAttendanceItemConverter converter,
			DeclareTimezoneResult declareResult) {
		String companyId = AppContexts.user().companyId();

		GeneralDate targetDate = recordReGetClass.getIntegrationOfDaily().getYmd();

		// 休暇クラス
		VacationClass vacation = CalcDefaultValue.DEFAULT_VACATION;

		Optional<SettingOfFlexWork> flexCalcMethod = Optional.of(new SettingOfFlexWork(new FlexCalcMethodOfHalfWork(
				new FlexCalcMethodOfEachPremiumHalfWork(FlexCalcMethod.OneDay, FlexCalcMethod.OneDay),
				new FlexCalcMethodOfEachPremiumHalfWork(FlexCalcMethod.OneDay, FlexCalcMethod.OneDay))));

		// 総拘束時間の計算
		Optional<CalculateOfTotalConstraintTime> optionalCalculateOfTotalConstraintTime = recordReGetClass.getCompanyCommonSetting()
				.getCalculateOfTotalCons();
		if (!optionalCalculateOfTotalConstraintTime.isPresent()) {
			// 総拘束時間が取得できない場合のエラー
			throw new BusinessException("Msg_1447");
		}
		CalculateOfTotalConstraintTime calculateOfTotalConstraintTime = optionalCalculateOfTotalConstraintTime.get();

		// 会社別代休設定取得
		val compensLeaveComSet = recordReGetClass.getCompanyCommonSetting().getCompensatoryLeaveComSet();
		List<CompensatoryOccurrenceSetting> eachCompanyTimeSet = new ArrayList<>();
		if (compensLeaveComSet != null)
			eachCompanyTimeSet = compensLeaveComSet.getCompensatoryOccurrenceSetting();

		// -------------------------計算用一時的クラス作成----------------------------

		val workType = recordReGetClass.getWorkType();
		// if(!workType.isPresent() ||
		// !recordReGetClass.getWorkTimeSetting().isPresent()) return
		// recordReGetClass.getIntegrationOfDaily();
		if (!workType.isPresent())
			return recordReGetClass.getIntegrationOfDaily();

		// 乖離時間(AggregateRoot)取得
		List<DivergenceTimeRoot> divergenceTimeList = recordReGetClass.getCompanyCommonSetting().getDivergenceTime();

		
		/* 時間の計算 */
		recordReGetClass.setIntegrationOfDaily(AttendanceTimeOfDailyAttendance.calcTimeResult(vacation, workType.get(),
				flexCalcMethod, eachCompanyTimeSet, divergenceTimeList,
				calculateOfTotalConstraintTime, scheduleReGetClass, recordReGetClass,
				recordReGetClass.getPersonDailySetting().getPersonInfo(),
				getPredByPersonInfo(recordReGetClass.getPersonDailySetting().getPersonInfo().getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode(),
						recordReGetClass.getCompanyCommonSetting().getShareContainer(), workType.get()),
				converter, recordReGetClass.getCompanyCommonSetting(),
				decisionWorkTimeCode(recordReGetClass.getIntegrationOfDaily().getWorkInformation(), recordReGetClass.getPersonDailySetting(), workType),
				declareResult));

		/* 日別実績への項目移送 */
		return recordReGetClass.getIntegrationOfDaily();
	}

	/**
	 * １日の範囲クラス作成
	 * 
	 * @param companyId 会社コード
	 * @param employeeId 社員ID
	 * @param targetDate 対象日
	 * @param integrationOfDaily 日別実績(Work)
	 * @param isOotsukaMode
	 * @param workType
	 * @param justCorrectionAtr
	 * @return 1日の計算範囲
	 */
	public static CalculationRangeOfOneDay createOneDayCalculationRange(RequireM1 require, 
			IntegrationOfDaily integrationOfDaily, Optional<WorkTimezoneCommonSet> commonSet,
			boolean isOotsukaMode, WorkType workType, JustCorrectionAtr justCorrectionAtr,  Optional<WorkTimeCode> workTimeCode) {
		
		String companyId = AppContexts.user().companyId();
		String employeeId = integrationOfDaily.getEmployeeId();
		GeneralDate targetDate = integrationOfDaily.getYmd();
		/* 所定時間設定取得 */
		Optional<PredetemineTimeSetting> predetermineTimeSet = Optional.empty();
		if (workTimeCode.isPresent()) {
			predetermineTimeSet = require.predetemineTimeSetting(companyId, workTimeCode.get().v());
		}
		/*休日の場合など、もともと働く日じゃない場合は所定時間を0にしたい。*/
		if (!predetermineTimeSet.isPresent()) {
			predetermineTimeSet = Optional.of(new PredetemineTimeSetting(companyId, new AttendanceTime(0),
					workTimeCode.get(),
					new PredetermineTime(
							new BreakDownTimeDay(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0)),
							new BreakDownTimeDay(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0))),
					new PrescribedTimezoneSetting(new TimeWithDayAttr(0), new TimeWithDayAttr(0),
							Collections.emptyList()),
					new TimeWithDayAttr(0), false));

		}
		/* 1日の計算範囲取得 */
		val calcRangeOfOneDay = new TimeSpanForDailyCalc(predetermineTimeSet.get().getStartDateClock(),
				predetermineTimeSet.get().getStartDateClock()
						.forwardByMinutes(predetermineTimeSet.get().getRangeTimeDay().valueAsMinutes()));

		WorkInfoOfDailyAttendance toDayWorkInfo = integrationOfDaily.getWorkInformation();
		Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyPerformance = integrationOfDaily.getAttendanceLeave();
		/* 日別実績の出退勤時刻セット */
		timeLeavingOfDailyPerformance = correctStamp(integrationOfDaily.getAttendanceLeave(), employeeId, targetDate);

		//所定労働時間帯の件数を取得
		val amPmAtr = workType.checkWorkDay().toAmPmAtr().orElse(AmPmAtr.ONE_DAY);
		int predTimeSpanCount = predetermineTimeSet.map(c -> c.getTimezoneByAmPmAtrForCalc(amPmAtr).size()).orElse(0);
		
		//所定労働時間帯の件数に合わせた出退勤
		List<TimeLeavingWork> predTimeLeavingWorks = timeLeavingOfDailyPerformance.get().getTimeLeavingWorks(); //correctStamp()でemptyの可能性がない為、getしている
		if(predTimeSpanCount < 2) {
			predTimeLeavingWorks = predTimeLeavingWorks.stream()
					.sorted((c1, c2) -> c1.getWorkNo().compareTo(c2.getWorkNo()))
					.limit(1).collect(Collectors.toList());
		}
		
		//ジャスト遅刻、早退による時刻補正
		RoundingTime roundingTimeinfo = commonSet.get().getStampSet().getRoundingTime();
		List<TimeLeavingWork> justTimeLeavingWorks = roundingTimeinfo.justTImeCorrection(justCorrectionAtr, predTimeLeavingWorks);
		
		//丸め処理
		List<TimeLeavingWork> roundingTimeLeavingWorks = roundingTimeinfo.roundingttendance(justTimeLeavingWorks);
		
		return new CalculationRangeOfOneDay(Finally.empty(), Finally.empty(), calcRangeOfOneDay,
				new TimeLeavingOfDailyAttd(roundingTimeLeavingWorks,timeLeavingOfDailyPerformance.get().getWorkTimes()), // ジャスト遅刻、早退による時刻補正、丸め処理後の値
				PredetermineTimeSetForCalc.convertMastarToCalc(predetermineTimeSet.get())/* 所定時間帯(計算用) */,
				toDayWorkInfo, Optional.empty());
	}
	
	public static interface RequireM1 {
		
		Optional<PredetemineTimeSetting> predetemineTimeSetting(String cid, String workTimeCode);
	}

	
	/**
	 * 予定時間帯の作成
	 * @param integrationOfDaily 日別実績(Work)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @param converter
	 * @param justCorrectionAtr ジャスト補正区分
	 * @param yesterDayInfo 昨日の勤務情報
	 * @param tomorrowDayInfo 明日の勤務情報
	 * @return 予定実績
	 */
	private Optional<SchedulePerformance> createSchedule(IntegrationOfDaily integrationOfDaily,
			ManagePerCompanySet companyCommonSetting, ManagePerPersonDailySet personCommonSetting,
			DailyRecordToAttendanceItemConverter converter, JustCorrectionAtr justCorrectionAtr,
			Optional<WorkInfoOfDailyPerformance> yesterDayInfo,Optional<WorkInfoOfDailyPerformance> tomorrowDayInfo) {
		val integrationOfDailyForSchedule = converter.setData(integrationOfDaily).toDomain();
		// 予定時間１ ここで、「勤務予定を取得」～「計算区分を変更」を行い、日別実績(Work)をReturnとして受け取る
		IntegrationOfDaily afterScheduleIntegration = createScheduleTimeSheet(integrationOfDailyForSchedule);
		
		//勤務種類の取得
		Optional<WorkType> workType = this.getWorkTypeFromShareContainer(
				companyCommonSetting.getShareContainer(),
				AppContexts.user().companyId(),
				afterScheduleIntegration.getWorkInformation().getRecordInfo().getWorkTypeCode().v());
		
		//統合就業時間帯の作成
		Optional<IntegrationOfWorkTime> integrationOfWorkTime = this.createIntegrationOfWorkTime(
				companyCommonSetting.getShareContainer(),
				AppContexts.user().companyId(),
				personCommonSetting,
				afterScheduleIntegration.getWorkInformation());

		/** 休憩情報を変更 */
		afterScheduleIntegration = changeBreakTime(integrationOfDailyForSchedule, workType, integrationOfWorkTime,
				companyCommonSetting, personCommonSetting);
		
		// 予定時間2 ここで、「時間帯を作成」を実施 Returnとして１日の計算範囲を受け取る
		val returnResult = this.createRecord(
				afterScheduleIntegration,
				TimeSheetAtr.SCHEDULE,
				companyCommonSetting,
				personCommonSetting,
				justCorrectionAtr,
				yesterDayInfo,
				tomorrowDayInfo,
				workType.isPresent()
					? Optional.of(workType.get().clone())
					: Optional.empty(),
				integrationOfWorkTime,
				Optional.empty());
		
		/** 外出を削除 */
		returnResult.ifPresent(dayCalc -> {
			dayCalc.getWithinWorkingTimeSheet().get().getWithinWorkTimeFrame().stream().forEach(c -> {
				c.getDeductionTimeSheet().removeIf(d -> d.getDeductionAtr().isGoOut());
				c.getRecordedTimeSheet().removeIf(d -> d.getDeductionAtr().isGoOut());
			});
		});
		
		if(!returnResult.isPresent()) return Optional.empty();
		
		return Optional.of(new SchedulePerformance(returnResult.get(), workType, integrationOfWorkTime));
	}
	
	public IntegrationOfDaily createScheduleTimeSheet(IntegrationOfDaily integrationOfDaily) {
		
		/*勤務予定を日別実績に変換*/
		val changedShedule = convertScheduleToRecord(integrationOfDaily);
		/*計算区分を変更*/
		val changedCalcAtr = changeCalcAtr(changedShedule);
		
		return changedCalcAtr;
	}
	
	/** 休憩情報を変更 */
	private IntegrationOfDaily changeBreakTime(IntegrationOfDaily integrationOfDaily, Optional<WorkType> workType,
			Optional<IntegrationOfWorkTime> workTime,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting) {
		
		/** 休憩時間帯取得 */
		val correcedBreakTime = BreakTimeSheetGetter.get(createBreakRequire(workTime, workType),
				companyCommonSetting, personDailySetting, integrationOfDaily, true);
		
		integrationOfDaily.setBreakTime(new BreakTimeOfDailyAttd(correcedBreakTime));
		
		return integrationOfDaily;
	}
	
	private RequireM2 createBreakRequire(Optional<IntegrationOfWorkTime> workTime, Optional<WorkType> workType) {
		
		return new RequireM2() {
			
			private Optional<PredetemineTimeSetting> predeteminaTimeSet = Optional.empty();
			
			@Override
			public Optional<WorkTimeSetting> workTimeSetting(String companyId, String workTimeCode) {
				return workTime.map(c -> c.getWorkTimeSetting());
			}
			
			@Override
			public Optional<FlowWorkSetting> flowWorkSetting(String companyId, String workTimeCode) {
				return workTime.flatMap(c -> c.getFlowWorkSetting());
			}
			
			@Override
			public Optional<FlexWorkSetting> flexWorkSetting(String companyId, String workTimeCode) {
				return workTime.flatMap(c -> c.getFlexWorkSetting());
			}
			
			@Override
			public Optional<FixedWorkSetting> fixedWorkSetting(String companyId, String workTimeCode) {
				return workTime.flatMap(c -> c.getFixedWorkSetting());
			}
			
			@Override
			public Optional<WorkType> workType(String companyId, String workTypeCd) {
				return workType;
			}
			
			@Override
			public Optional<PredetemineTimeSetting> predetemineTimeSetting(String cid, String workTimeCode) {
				if(predeteminaTimeSet.isPresent()) {
					return predeteminaTimeSet;
				} else {
					predeteminaTimeSet = predetemineTimeSetRepository.findByWorkTimeCode(cid, workTimeCode);
				}
				return predeteminaTimeSet;
			}
			
			@Override
			public CalculationRangeOfOneDay createOneDayRange(Optional<PredetemineTimeSetting> predetemineTimeSet,
					IntegrationOfDaily integrationOfDaily, Optional<WorkTimezoneCommonSet> commonSet, WorkType workType,
					JustCorrectionAtr justCorrectionAtr, Optional<WorkTimeCode> workTimeCode) {
				
				return createOneDayCalculationRange(this, integrationOfDaily, commonSet, false, workType, justCorrectionAtr, workTimeCode);
			}
		};
	}
	
	public static interface RequireM2 extends RequireM1, BreakTimeSheetGetter.RequireM1 {
		
	}
	
	/**
	 * 勤務予定を日別実績に変換
	 * @param 日別実績の勤務情報
	 * @param 日別実績の出退勤
	 */
	private IntegrationOfDaily convertScheduleToRecord(IntegrationOfDaily integrationOfDaily) {
		
		IntegrationOfDaily copyIntegration = integrationOfDaily;
		//勤務情報を移す
		WorkInfoOfDailyAttendance workInfo = integrationOfDaily.getWorkInformation();
//		workInfo.setRecordInfo(workInfo.getScheduleInfo());
		
		List<TimeLeavingWork> scheduleTimeSheetList = new ArrayList<TimeLeavingWork>(); 
		for(ScheduleTimeSheet schedule : workInfo.getScheduleTimeSheets()) {
			WorkStamp attendance = new WorkStamp(schedule.getAttendance(), new WorkLocationCD("01"), TimeChangeMeans.AUTOMATIC_SET );
			WorkStamp leaving    = new WorkStamp(schedule.getLeaveWork(), new WorkLocationCD("01"), TimeChangeMeans.AUTOMATIC_SET );
			TimeActualStamp atStamp = new TimeActualStamp(attendance,attendance,workInfo.getScheduleTimeSheets().size());
			TimeActualStamp leStamp = new TimeActualStamp(leaving,leaving,workInfo.getScheduleTimeSheets().size());
			TimeLeavingWork timeLeavingWork = new TimeLeavingWork(schedule.getWorkNo(),atStamp,leStamp);
			scheduleTimeSheetList.add(timeLeavingWork);
		}
		val timeLeavingOfDaily = new TimeLeavingOfDailyAttd(scheduleTimeSheetList, new WorkTimes(workInfo.getScheduleTimeSheets().size()));
		copyIntegration.setAttendanceLeave(Optional.of(timeLeavingOfDaily));
		return copyIntegration;
	}
	
	/**
	 * 計算区分を変更する
	 * @return 計算区分変更後の日別実績(WORK)
	 */
	private IntegrationOfDaily changeCalcAtr(IntegrationOfDaily integrationOfDaily){
		
		CalAttrOfDailyAttd calAttr = new CalAttrOfDailyAttd(new AutoCalFlexOvertimeSetting(new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS)),
														  new AutoCalRaisingSalarySetting(true,true),
														  new AutoCalRestTimeSetting(new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS)
																  ,new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS)),
														  new AutoCalOvertimeSetting(new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS), 
																  new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS), 
																  new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS), 
																  new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS), 
																  new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS), 
																  new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS)),
														  new AutoCalcOfLeaveEarlySetting(true, true),
														  new AutoCalcSetOfDivergenceTime(DivergenceTimeAttr.USE));
		if(integrationOfDaily.getCalAttr() != null) {
			calAttr = new CalAttrOfDailyAttd(new AutoCalFlexOvertimeSetting(new AutoCalSetting(integrationOfDaily.getCalAttr().getFlexExcessTime().getFlexOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS)),
											new AutoCalRaisingSalarySetting(true,true),
											new AutoCalRestTimeSetting(new AutoCalSetting(integrationOfDaily.getCalAttr().getHolidayTimeSetting().getLateNightTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS)
													,new AutoCalSetting(integrationOfDaily.getCalAttr().getHolidayTimeSetting().getRestTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS)),
											new AutoCalOvertimeSetting(new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getEarlyOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS), 
													new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getEarlyMidOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS), 
													new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getNormalOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS), 
													new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getNormalMidOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS), 
													new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getLegalOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS), 
													new AutoCalSetting(integrationOfDaily.getCalAttr().getOvertimeSetting().getLegalMidOtTime().getUpLimitORtSet(), AutoCalAtrOvertime.CALCULATEMBOSS)),
											new AutoCalcOfLeaveEarlySetting(true, true),
											new AutoCalcSetOfDivergenceTime(DivergenceTimeAttr.USE));
		}
		integrationOfDaily.setCalAttr(calAttr);
		return integrationOfDaily;
	}

	/**
	 * 任意項目の計算
	 * @param personCommonSetting 
	 * 
	 * @return
	 */
	private IntegrationOfDaily calcOptionalItem(IntegrationOfDaily integrationOfDaily,
			DailyRecordToAttendanceItemConverter converter, ManagePerCompanySet companyCommonSetting, ManagePerPersonDailySet personCommonSetting) {
		String companyId = AppContexts.user().companyId();
		String employeeId = integrationOfDaily.getEmployeeId();
		GeneralDate targetDate = integrationOfDaily.getYmd();
		// 「所属雇用履歴」を取得する
		Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt = this.shareEmploymentAdapter
				.findEmploymentHistory(companyId, employeeId, targetDate);
		// AggregateRoot「任意項目」取得
		List<OptionalItem> optionalItems = companyCommonSetting.getOptionalItems();
		// 計算式を取得(任意項目NOで後から絞る必要あり)
		List<Formula> formulaList = companyCommonSetting.getFormulaList();
		// 計算式の並び順を取得(任意項目NOで後から絞る必要あり)
		List<FormulaDispOrder> formulaOrderList = companyCommonSetting.getFormulaOrderList();
		// 適用する雇用条件の取得(任意項目全部分)
		List<EmpCondition> empCondition = companyCommonSetting.getEmpCondition();
		// 項目選択による計算時に必要なので取得
		converter.setData(integrationOfDaily);

		Optional<WorkTimeSetting> workTime = Optional.empty();
		Optional<PredetemineTimeSetting> predSet = Optional.empty();
		Optional<WorkType> workType = Optional.empty();
		MasterShareContainer<String> shareContainer = companyCommonSetting.getShareContainer();
		
		
		
		if (integrationOfDaily != null ) {
			Optional<WorkTimeCode> recordWorkTime = decisionWorkTimeCode(integrationOfDaily.getWorkInformation(), personCommonSetting, workType);
			if (recordWorkTime.isPresent()) {
				workTime = getWorkTimeSettingFromShareContainer(shareContainer, companyId,recordWorkTime.get().v());
				predSet = getPredetermineTimeSetFromShareContainer(shareContainer, companyId,recordWorkTime.get().v());
			}

			if (integrationOfDaily.getWorkInformation().getRecordInfo().getWorkTypeCode() != null) {
				workType = getWorkTypeFromShareContainer(shareContainer, companyId,
						integrationOfDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().toString());
			}
		}

		Optional<DailyStoredProcessResult> resultProcedure = Optional.empty();
		// 大塚モードの確認
		if (AppContexts.optionLicense().customize().ootsuka()){
			// 任意項目カスタマイズ
			resultProcedure = this.storedProcdureProcess.dailyProcessing(integrationOfDaily, workType, workTime, predSet);
			resultProcedure.ifPresent(tt -> {
				integrationOfDaily.getAttendanceTimeOfDailyPerformance().ifPresent(tc -> {
					tc.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
							.getOverTimeWork().ifPresent(ts -> {
								ts.mergeOverTimeList(tt.getOverTimes());
							});
				});
			});
		}

		// 編集状態を取得（日別実績の編集状態が持つ勤怠項目IDのみのList作成）
		List<Integer> attendanceItemIdList = integrationOfDaily.getEditState().stream()
				.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());
		List<ItemValue> itemValueList = new ArrayList<>();
		if (!attendanceItemIdList.isEmpty()) {
			itemValueList = converter.convert(attendanceItemIdList);
		}

		// 任意項目の計算
		integrationOfDaily.setAnyItemValue(Optional.of(
				AnyItemValueOfDaily.caluculationAnyItem(companyId, employeeId, targetDate, optionalItems, formulaList,
						formulaOrderList, empCondition, Optional.of(converter), bsEmploymentHistOpt, resultProcedure).getAnyItem()));

		IntegrationOfDaily calcResultIntegrationOfDaily = integrationOfDaily;

		if (!itemValueList.isEmpty()) {
			converter.setData(integrationOfDaily);
			converter.merge(itemValueList);
			// 手修正された項目の値を計算前に戻す
			calcResultIntegrationOfDaily.setAnyItemValue(converter.anyItems());
		}

		return calcResultIntegrationOfDaily;
	}

	private Optional<PredetermineTimeSetForCalc> getPredByPersonInfo(Optional<WorkTimeCode> workTimeCode,
			MasterShareContainer<String> shareContainer, WorkType workType) {
		if (!workTimeCode.isPresent())
			return Optional.empty();
		// val predSetting =
		// predetemineTimeSetRepository.findByWorkTimeCode(AppContexts.user().companyId(),
		// workTimeCode.get().toString());
		val predSetting = getPredetermineTimeSetFromShareContainer(shareContainer, AppContexts.user().companyId(),
				workTimeCode.get().toString());
		if (!predSetting.isPresent())
			return Optional.empty();
		return Optional.of(PredetermineTimeSetForCalc.convertFromAggregatePremiumTime(predSetting.get(), workType));

	}

	private WorkType getWorkTypeByWorkInfo(Optional<WorkInfoOfDailyPerformance> otherDayWorkInfo, WorkType nowWorkType,
			MasterShareContainer<String> shareContainer) {
		if (otherDayWorkInfo.isPresent()) {
			WorkTypeCode workTypeCode = otherDayWorkInfo.get().getWorkInformation().getRecordInfo().getWorkTypeCode();
			String typeCode = workTypeCode.v().toString();
			// val findedWorkType =
			// this.workTypeRepository.findByPK(AppContexts.user().companyId(), typeCode);
			val findedWorkType = getWorkTypeFromShareContainer(shareContainer, AppContexts.user().companyId(),
					typeCode);
			val useWorkType = findedWorkType.orElse(nowWorkType);
			return useWorkType;
		} else {
			return nowWorkType;
		}
	}

	private static Optional<TimeLeavingOfDailyAttd> correctStamp(
			Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyPerformance, String employeeId,
			GeneralDate targetDate) {

		/*出退勤がない場合は時刻0：00で作っておく*/
		if (!timeLeavingOfDailyPerformance.isPresent()) {
			WorkStamp attendance = new WorkStamp(new   TimeWithDayAttr(0),
					new WorkLocationCD("01"), TimeChangeMeans.AUTOMATIC_SET,null);
			WorkStamp leaving = new WorkStamp(new  TimeWithDayAttr(0), 
					new WorkLocationCD("01"), TimeChangeMeans.AUTOMATIC_SET,null);
			TimeActualStamp stamp = new TimeActualStamp(attendance, leaving, 1);
			TimeLeavingWork timeLeavingWork = new TimeLeavingWork(new WorkNo(1), stamp, stamp);
			List<TimeLeavingWork> timeLeavingWorkList = new ArrayList<>();
			timeLeavingWorkList.add(timeLeavingWork);
			timeLeavingOfDailyPerformance = Optional.of(
					new TimeLeavingOfDailyAttd( timeLeavingWorkList,new WorkTimes(1) ));
		}
		return timeLeavingOfDailyPerformance;
	}

	/**
	 * 出勤、振出、休出なのに打刻が漏れているか判定する
	 * 
	 * @return 打刻がもれている
	 */
	private boolean shouldTimeALLZero(IntegrationOfDaily integrationOfDaily, WorkType workType) {
		return (workType.getDailyWork().isWeekDayOrHolidayWork()
				&& !checkAttendanceLeaveState(integrationOfDaily.getAttendanceLeave()));
	}

	/**
	 * 共有コンテナを使った勤務種類の取得
	 * 
	 * @param shareContainer
	 * @param WorkTypeCode
	 * @param companyId
	 * @return
	 */
	private Optional<WorkType> getWorkTypeFromShareContainer(MasterShareContainer<String> shareContainer,
			String companyId, String WorkTypeCode) {
		// val x = shareContainer.getShared("WorkType" + WorkTypeCode);
		val workType = shareContainer.getShared("WorkType" + WorkTypeCode,
				() -> workTypeRepository.findByPK(companyId, WorkTypeCode));
		if (workType.isPresent()) {
			return Optional.of(workType.get().clone());
		}
		return Optional.empty();
	}

	/**
	 * 共有コンテナを使った就業時間帯の取得
	 * 
	 * @param shareContainer
	 * @param companyId
	 * @param workTimeCode
	 * @return
	 */
	private Optional<WorkTimeSetting> getWorkTimeSettingFromShareContainer(MasterShareContainer<String> shareContainer,
			String companyId, String workTimeCode) {
		val workTimeSetting = shareContainer.getShared("WorkTime" + workTimeCode,
				() -> workTimeSettingRepository.findByCode(companyId, workTimeCode));
		if (workTimeSetting.isPresent()) {
			return Optional.of(workTimeSetting.get().clone());
		}
		return Optional.empty();
	}

	/**
	 * 共有コンテナを使った所定時間帯設定の取得
	 * 
	 * @param shareContainer
	 * @param companyId
	 * @param workTimeCode
	 * @return
	 */
	private Optional<PredetemineTimeSetting> getPredetermineTimeSetFromShareContainer(
			MasterShareContainer<String> shareContainer, String companyId, String workTimeCode) {
		val predSet = shareContainer.getShared("PredetemineSet" + workTimeCode,
				() -> predetemineTimeSetRepository.findByWorkTimeCode(companyId, workTimeCode));
		if (predSet.isPresent()) {
			return Optional.of(predSet.get().clone());
		}
		return Optional.empty();
	}
	
	/**
	 * 共有コンテナを使ったフレ就業時間帯の取得
	 * 
	 * @return
	 */
	private Optional<FlexWorkSetting> getFlexWorkSetOptFromShareContainer(MasterShareContainer<String> shareContainer,
			String companyId, String workTimeCode) {
		val flexWorkSetOpt = shareContainer.getShared("FLEX_WORK" + companyId + workTimeCode,
				() -> flexWorkSettingRepository.find(companyId, workTimeCode));
		if (flexWorkSetOpt.isPresent()) {
			return Optional.of(flexWorkSetOpt.get().clone());
		}
		return Optional.empty();
	}

	/**
	 * 共有コンテナを使った固定就業時間帯の取得
	 * 
	 * @return
	 */
	private Optional<FixedWorkSetting> getFixedWorkSettingFromShareContainer(
			MasterShareContainer<String> shareContainer, String companyId, String workTimeCode) {
		val fixedWorkSet = shareContainer.getShared("FIXED_WORK" + companyId + workTimeCode,
				() -> fixedWorkSettingRepository.findByKey(companyId, workTimeCode));
		if (fixedWorkSet.isPresent()) {
			return fixedWorkSet.map(f -> f.clone());
		}
		return Optional.empty();
	}
	
	/**
	 * 共有コンテナを使った流動就業時間帯の取得
	 * 
	 * @return
	 */
	private Optional<FlowWorkSetting> getFlowWorkSettingFromShareContainer(
			MasterShareContainer<String> shareContainer, String companyId, String workTimeCode) {
		val flowWorkSet = shareContainer.getShared("FLOW_WORK" + companyId + workTimeCode,
				() -> flowWorkSettingRepository.find(companyId, workTimeCode));
		if (flowWorkSet.isPresent()) {
			return flowWorkSet.map(f -> f.clone());
		}
		return Optional.empty();
	}

	/**
	 * @param shareContainer
	 * @param companyId
	 * @param workTimeSetting
	 * @return
	 */
	private Optional<IntegrationOfWorkTime> createIntegrationOfWorkTimeToForm(
			MasterShareContainer<String> shareContainer,
			String companyId,
			WorkTimeSetting workTimeSetting) {
		switch(workTimeSetting.getWorkTimeDivision().getWorkTimeForm()) {
			case FIXED:	
				Optional<FixedWorkSetting> fixedWorkSetting = this.getFixedWorkSettingFromShareContainer(
						shareContainer,
						companyId,
						workTimeSetting.getWorktimeCode().toString());
				if(!fixedWorkSetting.isPresent())
					return Optional.empty();
				
				//固定勤務で統合就業時間帯を作成
				return Optional.of(new IntegrationOfWorkTime(workTimeSetting.getWorktimeCode(), workTimeSetting, fixedWorkSetting.get()));
				
			case FLEX:
				Optional<FlexWorkSetting> flexWorkSetting = this.getFlexWorkSetOptFromShareContainer(
						shareContainer,
						companyId,
						workTimeSetting.getWorktimeCode().toString());
				if(!flexWorkSetting.isPresent())
					return Optional.empty();
				
				//フレックス勤務で統合就業時間帯を作成
				return Optional.of(new IntegrationOfWorkTime(workTimeSetting.getWorktimeCode(), workTimeSetting, flexWorkSetting.get()));
				
			case FLOW:
				Optional<FlowWorkSetting> flowWorkSetting = this.getFlowWorkSettingFromShareContainer(
						shareContainer,
						companyId,
						workTimeSetting.getWorktimeCode().toString());
				if(!flowWorkSetting.isPresent())
					return Optional.empty();
				
				//流動勤務で統合就業時間帯を作成
				return Optional.of(new IntegrationOfWorkTime(workTimeSetting.getWorktimeCode(), workTimeSetting, flowWorkSetting.get()));
				
			case TIMEDIFFERENCE:
				throw new RuntimeException("Unimplemented");/*時差勤務はまだ実装しない。2020/5/19 渡邉*/
				
			default:
				throw new RuntimeException("Non-conformity No Work");
		}
	}
	
	private Optional<IntegrationOfWorkTime> createIntegrationOfWorkTime(
			MasterShareContainer<String> shareContainer,
			String companyId,
			ManagePerPersonDailySet personCommonSetting,
			WorkInfoOfDailyAttendance workInfo){
		/* 勤務種類の取得 */
		Optional<WorkType> workType = this.getWorkTypeFromShareContainer(shareContainer, companyId, workInfo.getRecordInfo().getWorkTypeCode().v());
		
		Optional<WorkTimeCode> workTimeCode = this.decisionWorkTimeCode(workInfo, personCommonSetting, workType);
		
		/* 就業時間帯勤務区分 */
		// 1日休日の場合、就業時間帯コードはnullであるので、
		// all0を計算させるため(実績が計算できなくても、予定時間を計算する必要がある
		if (!workTimeCode.isPresent())
			return Optional.empty();
		
		Optional<WorkTimeSetting> workTimeSetting = this.getWorkTimeSettingFromShareContainer(shareContainer, companyId, workTimeCode.get().toString());
		if (!workTimeSetting.isPresent())
			return Optional.empty();
		
		return this.createIntegrationOfWorkTimeToForm(shareContainer, companyId, workTimeSetting.get());
	}
	
	/**
	 * 申告時間帯の作成
	 * @param converter 日別実績コンバータ
	 * @param calcRangeRecord １日の計算範囲（実績用）
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeSheetAtr 実働or予定時間帯作成から呼び出されたか
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @param justCorrectionAtr ジャスト補正区分
	 * @param yesterDayInfo 前日の勤務情報
	 * @param tomorrowDayInfo 翌日の勤務情報
	 * @param workTypeOpt 当日の勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯（実績用）
	 * @param schedulePerformance 予定実績
	 * @return 申告時間帯作成結果
	 */
	private DeclareTimezoneResult createDeclare(
			DailyRecordToAttendanceItemConverter converter,
			Optional<CalculationRangeOfOneDay> calcRangeRecord,
			IntegrationOfDaily integrationOfDaily,
			TimeSheetAtr timeSheetAtr,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting,
			JustCorrectionAtr justCorrectionAtr,
			Optional<WorkInfoOfDailyPerformance> yesterDayInfo,
			Optional<WorkInfoOfDailyPerformance> tomorrowDayInfo,
			Optional<WorkType> workTypeOpt,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			Optional<SchedulePerformance> schedulePerformance) {
		
		DeclareTimezoneResult result = new DeclareTimezoneResult();
		
		// 実績の時間帯が未作成の時、処理しない
		if (!calcRangeRecord.isPresent()) return result;
		// 勤務種類がない時、処理しない
		if (!workTypeOpt.isPresent()) return result;
		// 就業時間帯を確認する
		if (!integrationOfWorkTime.isPresent()) return result;
		// 勤務形態を取得する
		WorkTimeDivision workTimeDivision = integrationOfWorkTime.get().getWorkTimeSetting().getWorkTimeDivision();
		if (workTimeDivision.isFlex()) return result;
		// 申告設定の取得
		if (!companyCommonSetting.getDeclareSet().isPresent()) return result;
		DeclareSet declareSet = companyCommonSetting.getDeclareSet().get();
		if (declareSet.getUsageAtr() == NotUseAtr.NOT_USE) return result;
		
		String companyId = AppContexts.user().companyId();					// 会社ID
		WorkType workType = workTypeOpt.get();								// 勤務種類
		WorkTimeCode workTimeCode = integrationOfWorkTime.get().getCode();	// 就業時間帯コード
		
		// 日別勤怠（Work）をcloneする　→　申告用:日別実績(Work)
		IntegrationOfDaily itgOfDailyForDeclare = converter.setData(integrationOfDaily).toDomain();
		// 統合就業時間帯を作成する　→　申告用:統合就業時間帯
		Optional<IntegrationOfWorkTime> itgOfWorkTimeForDeclareOpt = this.createIntegrationOfWorkTime(
				companyCommonSetting.getShareContainer(),
				companyId,
				personCommonSetting,
				itgOfDailyForDeclare.getWorkInformation());
		if (!itgOfWorkTimeForDeclareOpt.isPresent()) return result;
		IntegrationOfWorkTime itgOfWorkTimeForDeclare = itgOfWorkTimeForDeclareOpt.get();
		// 所定時間設定の取得
		Optional<PredetemineTimeSetting> predTimeSet = getPredetermineTimeSetFromShareContainer(
				companyCommonSetting.getShareContainer(), companyId, workTimeCode.v());
		// ※　休日の場合など、もともと働く日じゃない場合は所定時間を0にしたい
		if (!predTimeSet.isPresent()) {
			predTimeSet = Optional.of(new PredetemineTimeSetting(companyId, new AttendanceTime(0),
					workTimeCode,
					new PredetermineTime(
							new BreakDownTimeDay(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0)),
							new BreakDownTimeDay(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0))),
					new PrescribedTimezoneSetting(new TimeWithDayAttr(0), new TimeWithDayAttr(0),
							Collections.emptyList()),
					new TimeWithDayAttr(0), false));

		}
		// 申告計算範囲の作成
		DeclareCalcRange declareCalcRange = DeclareCalcRange.create(
				companyId, workType, itgOfWorkTimeForDeclare, itgOfDailyForDeclare,
				calcRangeRecord.get(), declareSet, predTimeSet, companyCommonSetting, personCommonSetting);
		// 申告時間がない時、処理しない
		if (!declareCalcRange.getAttdLeave().getAttdOvertime().isPresent() &&
				!declareCalcRange.getAttdLeave().getLeaveOvertime().isPresent()) return result;
		// 申告エラーチェック
		if (declareSet.checkError(declareCalcRange.isHolidayWork(), declareCalcRange.getAttdLeave())) return result;
		// 残業休出枠設定を調整する
		declareSet.adjustOvertimeHolidayWorkFrameSet(
				itgOfWorkTimeForDeclare, itgOfDailyForDeclare.getCalAttr(), declareCalcRange, workType);
		// 出退勤時刻を申告処理用に調整する
		if (itgOfDailyForDeclare.getAttendanceLeave().isPresent()){
			declareCalcRange.adjustAttdLeaveClock(
					itgOfDailyForDeclare.getAttendanceLeave().get().getTimeLeavingWorks(), workType);
		}
		// デフォルト設定のインスタオンスを生成する
		if (itgOfWorkTimeForDeclare.getFixedWorkSetting().isPresent()){
			itgOfWorkTimeForDeclare.getFixedWorkSetting().get().setCommonSetting(
					WorkTimezoneCommonSet.generateDefault());
		}
		if (itgOfWorkTimeForDeclare.getFlowWorkSetting().isPresent()){
			itgOfWorkTimeForDeclare.getFlowWorkSetting().get().setCommonSetting(
					WorkTimezoneCommonSet.generateDefault());
		}
		// 時間帯の作成
		Optional<CalculationRangeOfOneDay> calcRangeDeclare = createRecord(
				itgOfDailyForDeclare,
				TimeSheetAtr.RECORD,
				companyCommonSetting,
				personCommonSetting,
				JustCorrectionAtr.NOT_USE,
				yesterDayInfo,
				tomorrowDayInfo,
				Optional.of(workType),
				Optional.of(itgOfWorkTimeForDeclare),
				schedulePerformance);
		// 申告時間帯作成結果を返す
		result.setCalcRangeOfOneDay(calcRangeDeclare);
		result.setDeclareCalcRange(Optional.of(declareCalcRange));
		return result;
	}
}