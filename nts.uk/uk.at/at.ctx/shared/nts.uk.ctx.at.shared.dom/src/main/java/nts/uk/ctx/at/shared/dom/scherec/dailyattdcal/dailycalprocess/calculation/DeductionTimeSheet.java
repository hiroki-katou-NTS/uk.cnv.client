package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.FluidFixedAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.shorttimework.CalcOfShortTimeWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.BreakClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.ShortTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.WorkingBreakTimeAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.LateTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.RestTimeOfficeWorkCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.common.TotalRoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.common.childcareset.ShortTimeWorkGetRange;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 控除時間帯
 * @author keisuke_hoshina
 */
@RequiredArgsConstructor
@Getter
public class DeductionTimeSheet {
	// 控除用
	private final List<TimeSheetOfDeductionItem> forDeductionTimeZoneList;
	// 計上用
	private final List<TimeSheetOfDeductionItem> forRecordTimeZoneList;
	//休憩
	private final BreakTimeOfDailyAttd breakTimeOfDailyList;
	//外出
	private final Optional<OutingTimeOfDailyAttd> dailyGoOutSheet;
	//短時間
	private final List<ShortWorkingTimeSheet> shortTimeSheets;
	
	/**
	 * 控除時間帯の作成(補正処理用の固定休憩の処理)
	 * @param dedAtr 控除区分
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param predetermineTimeSetForCalc 所定時間設定(計算用クラス)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	public static List<TimeSheetOfDeductionItem> createDedctionTimeSheet(
			DeductionAtr dedAtr,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		/* 控除時間帯取得 控除時間帯リストへコピー */
		List<TimeSheetOfDeductionItem> useDedTimeSheet = collectDeductionTimesForCorrect(
				dedAtr,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				oneDayOfRange,
				attendanceLeaveWork,
				companyCommonSetting,
				personCommonSetting);

		/* 重複部分補正処理 */
		useDedTimeSheet = new DeductionTimeSheetAdjustDuplicationTime(useDedTimeSheet).reCreate(
				integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet(),
				integrationOfWorkTime.getRestClockManageAtr(),
				FluidFixedAtr.of(integrationOfWorkTime.getFlowWorkRestTimezone(todayWorkType)),
				integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeDailyAtr());

		/* 控除でない外出削除 */
		List<TimeSheetOfDeductionItem> goOutDeletedList = new ArrayList<TimeSheetOfDeductionItem>();
		for (TimeSheetOfDeductionItem timesheet : useDedTimeSheet) {
			if (dedAtr.isAppropriate() || 
					(!(timesheet.getDeductionAtr().isGoOut() && timesheet.getGoOutReason().get().isPublicOrCmpensation()))) {
				goOutDeletedList.add(timesheet);
			}
		}
		return goOutDeletedList;
	}

	/**
	 * 控除時間に該当する項目を集め控除時間帯を作成する
	 * アルゴリズム：控除時間帯の作成（計算用）
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @param betweenWorkTimeSheets 非勤務時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除時間帯
	 */
	public static DeductionTimeSheet createDeductionTimeForCalc(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			TimeLeavingOfDailyAttd attendanceLeaveWork,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		/** 計上用控除時間帯の取得 */
		val calc = getRecordForCalc(todayWorkType, integrationOfWorkTime,
				integrationOfDaily, oneDayOfRange, attendanceLeaveWork, betweenWorkTimeSheets,
				companyCommonSetting, personCommonSetting);
		
		/** 控除用控除時間帯の取得 */
		val deduct = getDeductionForCalc(todayWorkType, integrationOfWorkTime,
				integrationOfDaily, oneDayOfRange, attendanceLeaveWork, betweenWorkTimeSheets,
				companyCommonSetting, personCommonSetting);
		
		return new DeductionTimeSheet(deduct, calc, integrationOfDaily.getBreakTime(), integrationOfDaily.getOutingTime(), 
						integrationOfDaily.getShortTime().map(c -> c.getShortWorkingTimeSheets()).orElse(new ArrayList<>()));
	}
	
	/**
	 * 計上用の取得（計算用）
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param betweenWorkTimeSheets 非勤務時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	private static List<TimeSheetOfDeductionItem> getRecordForCalc(
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			TimeLeavingOfDailyAttd attendanceLeaveWork,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {

		//控除時間帯の取得
		List<TimeSheetOfDeductionItem> deductionTimeSheet = collectDeductionTimesForCalc(
				DeductionAtr.Appropriate,
				workType,
				integrationOfWorkTime,
				integrationOfDaily,
				oneDayOfRange,
				attendanceLeaveWork,
				betweenWorkTimeSheets,
				companyCommonSetting,
				personCommonSetting);
		
		if(integrationOfWorkTime.getCommonSetting().getGoOutSet().getTotalRoundingSet().getFrameStraddRoundingSet().isTotalAndRounding()) {
			// 丸め処理（未完成）
			deductionTimeSheet = rounding(
					DeductionAtr.Appropriate,
					deductionTimeSheet,
					integrationOfWorkTime.getCommonSetting().getGoOutSet().getTotalRoundingSet(),
					new TimeWithDayAttr(480), //仮で固定値を渡している。丸め処理見直し時に要修正。
					new TimeWithDayAttr(1020), //仮で固定値を渡している。丸め処理見直し時に要修正。
					oneDayOfRange);
		}
		return deductionTimeSheet;
	}
	
	/**
	 * 控除用の取得（計算用）
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別勤怠(Work)
	 * @param oneDayOfRange 1日の範囲
	 * @param attendanceLeaveWork 出退勤
	 * @param betweenWorkTimeSheets 非勤務時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	private static List<TimeSheetOfDeductionItem> getDeductionForCalc(
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			TimeLeavingOfDailyAttd attendanceLeaveWork,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {

		//控除時間帯の取得
		return collectDeductionTimesForCalc(
				DeductionAtr.Deduction,
				workType,
				integrationOfWorkTime,
				integrationOfDaily,
				oneDayOfRange,
				attendanceLeaveWork,
				betweenWorkTimeSheets,
				companyCommonSetting,
				personCommonSetting);
	}
	
	/**
	 * 控除時間帯の取得 (計算用)
	 * @param deductionAtr 控除区分
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別勤怠(WORK)
	 * @param oneDayOfRange 1日の範囲
	 * @param attendanceLeaveWork 出退勤
	 * @param betweenWorkTimeSheets 非勤務時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	private static List<TimeSheetOfDeductionItem> collectDeductionTimesForCalc(DeductionAtr deductionAtr,
			WorkType todayWorkType, IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily, TimeSpanForDailyCalc oneDayOfRange,
			TimeLeavingOfDailyAttd attendanceLeaveWork, Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		List<TimeSheetOfDeductionItem> sheetList = new ArrayList<>();
		
		// 休日系でない時
		if (!todayWorkType.getDecisionAttendanceHolidayAttr()){
			/** 休憩時間帯取得 */
			sheetList.addAll(getBreakTimeDeductionForCalc(integrationOfDaily));
			
			/** 勤務間休憩時間帯を取得 */
			betweenWorkTimeSheets.ifPresent(bwt -> sheetList.add(bwt));
			
			/** 外出時間帯取得 */
			val goOutDeduct = integrationOfDaily.getOutingTime().map(c -> c.removeUnuseItemBaseOnAtr(
					deductionAtr,
					integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet(),
					integrationOfWorkTime.getFlowWorkRestTimezone(todayWorkType),
					integrationOfWorkTime.getFlowWorkRestSettingDetail(),
					integrationOfWorkTime.getCommonSetting().getStampSet().getRoundingTime()))
					.orElseGet(() -> new ArrayList<>());
			sheetList.addAll(goOutDeduct);
		}
		
		/** 短時間勤務時間帯を取得 */
		sheetList.addAll(getShortTimeWorkSheet(
				integrationOfWorkTime, integrationOfDaily, Optional.of(attendanceLeaveWork), deductionAtr, todayWorkType,
				companyCommonSetting, personCommonSetting));
		
		/** ソート処理 */
		sheetList.sort((first, second) -> first.getTimeSheet().getTimeSpan().getStart().compareTo(
											second.getTimeSheet().getTimeSpan().getStart()));
		/** 計算範囲による絞り込み */
		val reNewSheetList = refineCalcRange(sheetList, oneDayOfRange,
				integrationOfWorkTime.getCommonRestSetting().getCalculateMethod(),
				attendanceLeaveWork, deductionAtr);
		
		/** 控除時間帯同士の重複部分を補正 */
		return new DeductionTimeSheetAdjustDuplicationTime(reNewSheetList).reCreate(
										integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet(),
										integrationOfWorkTime.getRestClockManageAtr(),
										todayWorkType.getDecisionAttendanceHolidayAttr()
												? FluidFixedAtr.FixedWork //1日休日系の場合は区分自体必要ない為、固定値を渡している
												: FluidFixedAtr.of(integrationOfWorkTime.getFlowWorkRestTimezone(todayWorkType)),
										integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeDailyAtr());
	}

	private static List<TimeSheetOfDeductionItem> getBreakTimeDeductionForCalc(IntegrationOfDaily integrationOfDaily) {
		return integrationOfDaily.getBreakTime().changeAllTimeSheetToDeductionItem();
	}

	/** 休憩時間帯の取得 */
	private static List<TimeSheetOfDeductionItem> getBreakTimeDeductionForCorrect(WorkType workType, 
			IntegrationOfWorkTime integrationOfWorkTime) {
		
		if (integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().isfluidorFlex()) {
			
			return getBreakSetForFlow(workType, integrationOfWorkTime);
		}
		
		/** ○就業時間帯から参照した休憩時間帯を取得 */
		return integrationOfWorkTime.getBreakTimeZone(workType).stream().map(convertBreakTimeSheetToDeduction())
																		.collect(Collectors.toList());
	} 
	
	/** 流動勤務の休憩設定 */
	private static List<TimeSheetOfDeductionItem> getBreakSetForFlow(WorkType workType, 
			IntegrationOfWorkTime integrationOfWorkTime) {
		/** ○就業時間帯から休憩設定を取得 */
		val restTimeZone = integrationOfWorkTime.getFlowWorkRestTimezone(workType).get();
		val restSetDetail = integrationOfWorkTime.getFlowWorkRestSettingDetail().get();
		
		if (!restTimeZone.isFixRestTime() || restSetDetail.getFlowFixedRestSetting().getCalculateMethod().isStampWithoutReference()) {

			/** 休憩時間帯を固定にする＝falseの場合 */
			/** 計算方法=参照せずに打刻する　の場合 */
			return new ArrayList<>();
		}
		
		/** 就業時間帯から休憩時間帯を取得する */
		return integrationOfWorkTime.getBreakTimeZone(workType).stream().map(convertBreakTimeSheetToDeduction())
																		.collect(Collectors.toList());
	}
	
	private static Function<? super TimeSpanForCalc, ? extends TimeSheetOfDeductionItem> convertBreakTimeSheetToDeduction() {
		return c -> TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(new TimeSpanForDailyCalc(c.getStart(), c.getEnd()),
																					new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
																					Collections.emptyList(),
																					Collections.emptyList(),
																					WorkingBreakTimeAtr.NOTWORKING,
																					Finally.empty(),
																					Finally.of(BreakClassification.BREAK),
																					Optional.empty(),
																					DeductionClassification.BREAK,
																					Optional.empty(),
																					false);
	}
	
	/**
	 * 控除時間に該当する項目を集め控除時間帯を作成する
	 * アルゴリズム：控除時間帯の取得
	 * 補正用処理
	 * @param dedAtr 控除区分
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	public static List<TimeSheetOfDeductionItem> collectDeductionTimesForCorrect(
			DeductionAtr dedAtr,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		List<TimeSheetOfDeductionItem> sheetList = new ArrayList<TimeSheetOfDeductionItem>();
		/* 休憩時間帯取得 */
		sheetList.addAll(getBreakTimeDeductionForCorrect(todayWorkType, integrationOfWorkTime));
		
		/* 外出時間帯取得 */
		if(integrationOfDaily.getOutingTime().isPresent()) {
			sheetList.addAll(integrationOfDaily.getOutingTime().get().removeUnuseItemBaseOnAtr(
					dedAtr,
					integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet(),
					integrationOfWorkTime.getFlowWorkRestTimezone(todayWorkType),
					integrationOfWorkTime.getFlowWorkRestSettingDetail(),
					integrationOfWorkTime.getCommonSetting().getStampSet().getRoundingTime()));
		}
		
		/* 短時間勤務時間帯を取得 */
		sheetList.addAll(getShortTimeWorkSheet(
				integrationOfWorkTime, integrationOfDaily, attendanceLeaveWork, dedAtr, todayWorkType,
				companyCommonSetting, personCommonSetting));
		
		/* ソート処理 */
		sheetList = sheetList.stream()
				.sorted((first, second) -> first.getTimeSheet().getTimeSpan().getStart().compareTo(second.getTimeSheet().getTimeSpan().getStart()))
				.collect(Collectors.toList());
		
		/* 計算範囲による絞り込み */
		return refineCalcRangeForCorrect(sheetList, oneDayOfRange);
	}
	
	private static List<TimeSheetOfDeductionItem> refineCalcRangeForCorrect(List<TimeSheetOfDeductionItem> dedTimeSheets,
			TimeSpanForDailyCalc oneDayRange) {
		
		return dedTimeSheets.stream().map(dedTimeSheet -> {
			return oneDayRange.getDuplicatedWith(dedTimeSheet.getTimeSheet()).map(timeSheet -> {
				return TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(timeSheet,
						dedTimeSheet.getRounding(), dedTimeSheet.getRecordedTimeSheet(), dedTimeSheet.getDeductionTimeSheet(),
						dedTimeSheet.getWorkingBreakAtr(), dedTimeSheet.getGoOutReason(), dedTimeSheet.getBreakAtr(), 
						dedTimeSheet.getShortTimeSheetAtr(), dedTimeSheet.getDeductionAtr(), dedTimeSheet.getChildCareAtr(),
						dedTimeSheet.isRecordOutside());
			}).orElse(null);
		}).filter(c -> c != null).collect(Collectors.toList());
	}

	/**
	 * 短時間勤務時間帯の取得
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別勤怠(WORK)
	 * @param attendanceLeaveWork 日別勤怠の出退勤
	 * @param dedAtr 控除区分
	 * @param todayWorkType 勤務区分
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	private static List<TimeSheetOfDeductionItem> getShortTimeWorkSheet(
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			DeductionAtr dedAtr,
			WorkType todayWorkType,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		// 休出かどうかの判断
		if (todayWorkType.isHolidayWork()){
			return new ArrayList<>();
		}
		return integrationOfDaily.getShortTime().map(st -> {
			List<TimeSheetOfDeductionItem> deduct = new ArrayList<>();
			st.getShortWorkingTimeSheets().stream().forEach(c -> {
				List<TimeSpanForDailyCalc> checkResults = checkByDeductAtr(
						c, dedAtr, companyCommonSetting.getCalcShortTimeWork().get(),
						integrationOfWorkTime.getCommonSetting(),
						attendanceLeaveWork);
				checkResults.stream().forEach(d -> deduct.add(createDeductTimeForShortTime(
						attendanceLeaveWork, c, d)));
			});
			return deduct;
		}).orElseGet(() -> new ArrayList<>());
	}

	/**
	 * 控除区分による判断
	 * @param shortTimeWorkSheet 短時間勤務時間帯
	 * @param dedAtr 控除区分
	 * @param calcOfShortTimeWork 短時間勤務の計算
	 * @param commonSet 就業時間帯の共通設定
	 * @param attendanceLeaveWork 日別勤怠の出退勤
	 * @return 日別計算用時間帯(List)
	 */
	private static List<TimeSpanForDailyCalc> checkByDeductAtr(
			ShortWorkingTimeSheet shortTimeWorkSheet,
			DeductionAtr dedAtr,
			CalcOfShortTimeWork calcOfShortTimeWork,
			WorkTimezoneCommonSet commonSet,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork){
		
		List<TimeSpanForDailyCalc> results = new ArrayList<>();
	
		// 育児介護区分の確認
		ChildCareAtr childCareAtr = shortTimeWorkSheet.getChildCareAttr();
		
		// 控除区分を確認する
		ShortTimeWorkGetRange checkResult = ShortTimeWorkGetRange.NOT_GET;
		if (dedAtr == DeductionAtr.Appropriate){
			// 計上
			// 内数外数による取得範囲の判断
			checkResult = calcOfShortTimeWork.checkGetRangeByWithinOut(childCareAtr, commonSet);
		}
		else if (dedAtr == DeductionAtr.Deduction){
			// 控除
			// 勤務扱いによる取得範囲の判断
			checkResult = commonSet.getShortTimeWorkSet().checkGetRangeByWorkUse(childCareAtr);
		}
		// 短時間勤務取得範囲を確認する
		switch (checkResult){
		case NOT_GET:
			break;
		case NORMAL_GET:
			// 結果　←　開始～終了
			results.add(new TimeSpanForDailyCalc(shortTimeWorkSheet.getStartTime(), shortTimeWorkSheet.getEndTime()));
			break;
		case WITHOUT_ATTENDANCE_LEAVE:
			// 出退勤時刻と重複していない時間帯の取得
			if(attendanceLeaveWork.isPresent()) {
				results.addAll(attendanceLeaveWork.get().getNotDuplicateSpan(
						new TimeSpanForDailyCalc(shortTimeWorkSheet.getStartTime(), shortTimeWorkSheet.getEndTime())));
			}else {
				results.add(new TimeSpanForDailyCalc(shortTimeWorkSheet.getStartTime(), shortTimeWorkSheet.getEndTime()));
			}
			
			break;
		}
		// 結果を返す
		return results;
	}

	/**
	 * 控除項目の時間帯を作成する
	 * @param attendanceLeaveWork 日別勤怠の出退勤
	 * @param sts 短時間勤務時間帯
	 * @param timeSpan 時間帯
	 * @return 控除項目の時間帯
	 */
	private static TimeSheetOfDeductionItem createDeductTimeForShortTime(
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			ShortWorkingTimeSheet sts,
			TimeSpanForDailyCalc timeSpan) {
		
		return TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(
				timeSpan,
				new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
				new ArrayList<>(),
				new ArrayList<>(),
				WorkingBreakTimeAtr.NOTWORKING,
				Finally.empty(),
				Finally.empty(),
				Optional.of(decisionShortTimeAtr(attendanceLeaveWork.map(x -> x.getTimeLeavingWorks()).orElse(new ArrayList<>()), sts)),
				DeductionClassification.CHILD_CARE,
				Optional.of(sts.getChildCareAttr()),
				false);
	}
	
	/**
	 * 計算範囲による絞り込みを行うためのループ
	 * @param dedTimeSheets 控除項目の時間帯
	 * @param oneDayRange 1日の範囲
	 * @return 控除項目の時間帯リスト
	 */
	private static List<TimeSheetOfDeductionItem> refineCalcRange(List<TimeSheetOfDeductionItem> dedTimeSheets,
			TimeSpanForDailyCalc oneDayRange, RestTimeOfficeWorkCalcMethod calcMethod,
			TimeLeavingOfDailyAttd attendanceLeaveWork, DeductionAtr dedAtr) {
		
		List<TimeSheetOfDeductionItem> sheetList = new ArrayList<TimeSheetOfDeductionItem>();
		for (TimeSheetOfDeductionItem timeSheet : dedTimeSheets) {
			switch (timeSheet.getDeductionAtr()) {
			case NON_RECORD:
			case CHILD_CARE:
			case GO_OUT:
				Optional<TimeSpanForDailyCalc> duplicateGoOutSheet = oneDayRange.getDuplicatedWith(timeSheet.getTimeSheet());
				if (duplicateGoOutSheet.isPresent()) {
					/* ここで入れる控除、加給、特定日、深夜は duplicateGoOutSheetと同じ範囲に絞り込む */
					sheetList.add(TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(duplicateGoOutSheet.get(),
							timeSheet.getRounding(), timeSheet.getRecordedTimeSheet(), timeSheet.getDeductionTimeSheet(),
							timeSheet.getWorkingBreakAtr(),
							timeSheet.getGoOutReason(), timeSheet.getBreakAtr(), 
							timeSheet.getShortTimeSheetAtr(),timeSheet.getDeductionAtr(),timeSheet.getChildCareAtr(),
							timeSheet.isRecordOutside()));

				}
				break;
				
			case BREAK:
				List<TimeSheetOfDeductionItem> duplicateBreakSheet = timeSheet.getBreakCalcRange(
						attendanceLeaveWork.getTimeLeavingWorks(), calcMethod,
						oneDayRange.getDuplicatedWith(timeSheet.getTimeSheet()),
						dedAtr);
				
					sheetList.addAll(duplicateBreakSheet);
				break;
			default:
				throw new RuntimeException("unknown deductionAtr:" + timeSheet.getDeductionAtr());
			}
		}
		return sheetList;
	}
	
	/**
	 * 算出された外出時間の合計を取得する
	 * @param dedAtr 控除区分
	 * @return 控除合計時間List
	 */
	public List<DeductionTotalTime> getTotalGoOutTime(DeductionAtr dedAtr) {
		List<DeductionTotalTime> dedTotalTimeList = new ArrayList<>();
		switch (dedAtr) {
		case Appropriate:
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forDeductionTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isPrivate())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forDeductionTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isCompensation())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forDeductionTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isPublic())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forDeductionTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isUnion())
						.collect(Collectors.toList()), true));
			break;
		case Deduction:
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forRecordTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isPrivate())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forRecordTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isCompensation())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forRecordTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isPublic())
						.collect(Collectors.toList()), true));
			dedTotalTimeList.add(getDeductionTotalTime(
					this.forRecordTimeZoneList.stream()
						.filter(tc -> tc.getDeductionAtr().isGoOut())
						.filter(tc -> tc.getGoOutReason().get().isUnion())
						.collect(Collectors.toList()), true));
			break;
		default:
			throw new RuntimeException("unknown DeductionAtr" + dedAtr);
		}
		return dedTotalTimeList;
	}

	/**
	 * 法定内・外、相殺時間から合計時間算出
	 * @param deductionItemList 控除項目の時間帯(List)
	 * @param deductOffset 相殺時間控除区分
	 * @return 控除合計時間
	 */
	public DeductionTotalTime getDeductionTotalTime(
			List<TimeSheetOfDeductionItem> deductionItemList,
			boolean deductOffset) {

		int statMinutes = 0;		// 法定内
		int exStatMinutes = 0;		// 法定外
		for (TimeSheetOfDeductionItem item : deductionItemList){
			AttendanceTime time = item.calcTotalTime(deductOffset ? NotUseAtr.USE : NotUseAtr.NOT_USE, NotUseAtr.USE);
			// 2021.7.14 shuichi_ishida
			// 本来は、法内・外の振り分けが必要？　従来から振り分けが機能していなかったため、暫定的に法内に全て加算。
			// 元々は、控除項目の時間帯（または継承元）に、属性「法内外区分」があり、その区分で判断していた模様。
			statMinutes += time.valueAsMinutes();
		}

		return DeductionTotalTime.of(
				TimeWithCalculation.sameTime(new AttendanceTime(statMinutes + exStatMinutes)),
				TimeWithCalculation.sameTime(new AttendanceTime(statMinutes)),
				TimeWithCalculation.sameTime(new AttendanceTime(exStatMinutes)));
	}

	/**
	 * 計算を行う範囲に存在する控除時間帯の抽出
	 * @param workTimeSpan 計算範囲
	 * @return 計算範囲内に存在する控除時間帯
	 */
	public List<TimeSheetOfDeductionItem> getCalcRange(TimeSpanForDailyCalc workTimeSpan) {
		return forDeductionTimeZoneList.stream().filter(tc -> workTimeSpan.getTimeSpan().contains(tc.getTimeSheet().getTimeSpan()))
				.collect(Collectors.toList());
	}

	/**
	 * 法定内区分を法定外へ変更する
	 * @return 法定内区分変更後の控除時間帯
	 */
	public List<TimeSheetOfDeductionItem> replaceStatutoryAtrToExcess() {
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		for (TimeSheetOfDeductionItem deductionTimeSheet : forDeductionTimeZoneList) {
			returnList.add(deductionTimeSheet.createWithExcessAtr());
		}
		return returnList;
	}

	/**
	 * 控除時間帯の仮確定(流動用)
	 * アルゴリズム：控除時間帯の作成
	 * @param dedAtr 控除区分
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param oneDayOfRange 日別計算用時間帯
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param predetermineTimeSetForCalc 所定時間設定(計算用クラス)
	 * @param lateTimeSheet 遅刻時間帯(List)
	 * @param calcRange 1日の計算範囲
	 * @param correctWithEndTime 退勤が含まれているかどうか
	 * @param betweenWorkTimeSheets 控除項目の時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personCommonSetting 社員設定管理
	 * @return 控除項目の時間帯(List)
	 */
	public static List<TimeSheetOfDeductionItem> provisionalDecisionOfDeductionTimeSheet(
			DeductionAtr dedAtr,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeSpanForDailyCalc oneDayOfRange,
			Optional<TimeLeavingOfDailyAttd> attendanceLeaveWork,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc, 
			List<LateTimeSheet> lateTimeSheet,
			CalculationRangeOfOneDay calcRange,
			boolean correctWithEndTime,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		// 1日半日出勤・1日休日系の判定
		if(todayWorkType.checkWorkDay() == WorkStyle.ONE_DAY_REST) {
			return new ArrayList<>();
		}

		// 1日半日出勤・1日休日系の判定
		if(todayWorkType.getAttendanceHolidayAttr().isHoliday())
			return new ArrayList<>();
		
		// 固定休憩か流動休憩か確認する
		if (integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm() == WorkTimeForm.FIXED
				|| integrationOfWorkTime.getFlowWorkRestTimezone(todayWorkType).get().isFixRestTime()) {// 固定休憩の場合
			return createDedctionTimeSheet(
					dedAtr,
					todayWorkType,
					integrationOfWorkTime,
					integrationOfDaily,
					oneDayOfRange,
					attendanceLeaveWork,
					predetermineTimeSetForCalc,
					companyCommonSetting,
					personCommonSetting);
		} else {// 流動休憩の場合

			if (!attendanceLeaveWork.isPresent())
				return new ArrayList<>();
			/** シフトから計算 */
			return createDedctionTimeSheetFlow(dedAtr, todayWorkType, integrationOfWorkTime, integrationOfDaily,
					oneDayOfRange, lateTimeSheet, predetermineTimeSetForCalc, calcRange, correctWithEndTime,
					betweenWorkTimeSheets, companyCommonSetting, personCommonSetting);

		}
	}
	
	/** 控除時間帯の作成 */
	private static List<TimeSheetOfDeductionItem> createDedctionTimeSheetFlow(
			DeductionAtr deductionAtr,
			WorkType workType, 
			IntegrationOfWorkTime workTime,
			IntegrationOfDaily dailyRecord,
			TimeSpanForDailyCalc oneDayOfRange, 
			List<LateTimeSheet> lateTimeSheet,
			PredetermineTimeSetForCalc predetermineForCalc, 
			CalculationRangeOfOneDay calcRange,
			boolean correctWithEndTime,
			Optional<TimeSheetOfDeductionItem> betweenWorkTimeSheets,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personCommonSetting) {
		
		/** ○計算範囲の取得 */
		
		/** △控除時間帯の取得 */
		if (!dailyRecord.getAttendanceLeave().isPresent()) return Collections.emptyList();
		val deductionTimeSheet = collectDeductionTimesForCorrect(deductionAtr, workType, workTime,
				dailyRecord, oneDayOfRange, dailyRecord.getAttendanceLeave(),
				companyCommonSetting, personCommonSetting);
		
		/** パラメータ。勤務間を控除時間帯に入れる */
//		betweenWorkTimeSheets.ifPresent(c -> deductionTimeSheet.add(c));
		
		/** △控除時間帯同士の重複部分を補正 */
		val correctedDeductionTimeSheet = new DeductionTimeSheetAdjustDuplicationTime(deductionTimeSheet).reCreate(
												workTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeMethodSet(),
												workTime.getRestClockManageAtr(),
												FluidFixedAtr.of(workTime.getFlowWorkRestTimezone(workType)),
												workTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeDailyAtr());
		
		/** ○流動休憩時間帯を取得 */
		val flowRestTimeSheet = workTime.getFlowWorkRestTimezone(workType)
										.map(c -> c.getFlowRestTimezone().getFlowRestSet(oneDayOfRange, predetermineForCalc))
										.orElseGet(() -> new ArrayList<>());
		
		/** △休憩計算開始時刻を取得 */
		val timeLeave = calcRange.getAttendanceLeavingWork().getAttendanceLeavingWork(1);
		val startBreakTime = getStartBreakTime(lateTimeSheet, workTime, oneDayOfRange, workType,
				timeLeave, calcRange, dailyRecord, predetermineForCalc);
		
		val fluidCalc = new DeductionTotalTimeForFluidCalc(startBreakTime);
		
		val restTimeSheet = flowRestTimeSheet.stream().map(ts -> {
			
			/** △流動休憩時間帯の作成 */
			return fluidCalc.createDeductionFluidRestTime(deductionAtr, calcRange.getAttendanceLeavingWork(),
							fluidCalc.getBreakStartTime(), ts, fluidCalc.getDeductionTotal(), 
							correctedDeductionTimeSheet, workTime, workType, startBreakTime, 
							correctWithEndTime, betweenWorkTimeSheets);
		}).flatMap(List::stream).collect(Collectors.toList());
		
		/** ○控除時間帯(List)に休憩時間帯リストを追加 */
		correctedDeductionTimeSheet.addAll(restTimeSheet);
		
		/** ○控除時間帯をソート */
		correctedDeductionTimeSheet.sort((c1, c2) -> c1.getTimeSheet().getStart().compareTo(c2.getTimeSheet().getStart()));
		
		/** 控除時間帯(List)を返す */
		return correctedDeductionTimeSheet;
	}

	/** △休憩計算開始時刻を取得 */
	private static TimeWithDayAttr getStartBreakTime(List<LateTimeSheet> lateTimeSheet, 
			IntegrationOfWorkTime workTime, TimeSpanForDailyCalc oneDayOfRange, WorkType workType, 
			Optional<TimeLeavingWork> timeLeave, CalculationRangeOfOneDay calcRange,
			IntegrationOfDaily integrationOfDaily, PredetermineTimeSetForCalc predetermineTimeSet) {
		
		/** 遅刻が存在するか確認 */
		val firstLateTime =  lateTimeSheet.stream().filter(c -> c.getWorkNo() == 1).findFirst();
		if(!firstLateTime.isPresent()) {
			
			val startTime = timeLeave.flatMap(c -> c.getAttendanceTime()).orElse(oneDayOfRange.getStart());
			
			if (workTime.getWorkTimeSetting().getWorkTimeDivision().isFlow()) { /** 流動勤務の場合 */
				/** 出勤時刻を予定開始時刻にする */
				calcRange.setScheduleStartTimeForFlow(
						integrationOfDaily.getWorkInformation(),
						workTime.getFlowWorkSetting().get().getFlowSetting().getCalculateSetting());
				
				Optional<TimeLeavingWork> firstTimeLeave = calcRange.getAttendanceLeavingWork().getAttendanceLeavingWork(new WorkNo(1));
				
				/** 計算範囲を判断 */
				val within = calcRange.createWithinWorkTimeFrameIncludingCalculationRange(workType, workTime,
						firstTimeLeave.orElse(getTimeLeaveWork(integrationOfDaily)), predetermineTimeSet);
				
				/** 計算開始時刻を取得 */
				return within.getTimeSheet().getStart();
				
			} else if(workTime.getWorkTimeSetting().getWorkTimeDivision().isFlex()) { /** フレックス勤務用　の場合 */
				
				/** ○就業時間帯の計算開始時刻を取得 */
				val preTimeStart = workTime.getFirstStartTimeOfFlex(workType);
				
				/** ○出勤時刻と計算開始時刻から休憩計算開始時刻を判断 */
				return startTime.compareTo(preTimeStart) >= 0 ? startTime : preTimeStart;
			} else {
				
				/** フレックス勤務、流動勤務ではない場合はthrow Exception*/
				throw new RuntimeException("フレックス勤務、流動勤務が必要");
			}
		}
		
		/** ○終了時刻を休憩計算開始時刻とする */
		return firstLateTime.map(c -> c.getForDeducationTimeSheet()
							.map(t -> t.getAfterRoundingAsLate().getEnd())
							.orElseGet(() -> new TimeWithDayAttr(0)))
				.get();
	}

	private static nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork getTimeLeaveWork(
			IntegrationOfDaily integrationOfDaily) {
		if (!integrationOfDaily.getAttendanceLeave().isPresent()){
			return new TimeLeavingWork(new WorkNo(1), null, null);
		}
		val timeLeaving = integrationOfDaily.getAttendanceLeave().get();
		val timeLeave1 = timeLeaving.getAttendanceLeavingWork(1);
		val timeLeave2 = timeLeaving.getAttendanceLeavingWork(2);
		val attendance = timeLeave1.flatMap(c -> c.getAttendanceStamp());
		val leave = timeLeave2.isPresent() ? timeLeave2.flatMap(c -> c.getLeaveStamp()) 
										: timeLeave1.flatMap(c -> c.getLeaveStamp());
		
		return new TimeLeavingWork(new WorkNo(1), attendance.orElse(null), leave.orElse(null));
	}

	/**
	 * 指定時間帯に含まれる控除時間帯リストを取得
	 * @param timeSpan 時間帯
	 * @param atr 控除区分
	 * @return 控除項目の時間帯リスト(控除区分に従ってList取得)
	 */
	public List<TimeSheetOfDeductionItem> getDupliRangeTimeSheet(TimeSpanForDailyCalc timeSpan, DeductionAtr atr) {
		// 控除区分に合うListを取得
		List<TimeSheetOfDeductionItem> dedList = getDedListWithAtr(atr);
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		for (TimeSheetOfDeductionItem timeSheet : dedList) {
			// 重複している時間帯を返す
			val dupCalcRange = timeSheet.getTimeSheet().getDuplicatedWith(timeSpan);
			if (dupCalcRange.isPresent()) {
				if(timeSheet.isRecordOutside()) {
					//「勤務外を計上する==true」の場合には絞り込まない
					returnList.add(timeSheet.clone());
					continue;
				}
				// 処理中の「控除項目の時間帯」を重複した時間帯で作り直す
				TimeSheetOfDeductionItem divideStartTime = timeSheet.reCreateOwn(dupCalcRange.get().getTimeSpan().getStart(), false);
				TimeSheetOfDeductionItem correctAfterTimeSheet = divideStartTime.reCreateOwn(dupCalcRange.get().getTimeSpan().getEnd(), true);
				returnList.add(correctAfterTimeSheet);
			}
		}
		// 結果を返す
		return returnList;
	}
	
	/**
	 * 控除区分に従って控除リスト取得
	 * @param atr
	 * @return
	 */
	public List<TimeSheetOfDeductionItem> getDedListWithAtr(DeductionAtr atr) {
		if (atr.isDeduction()) {
			return this.forDeductionTimeZoneList;
		} else {
			return this.forRecordTimeZoneList;
		}
	}
	
	/**
	 * 短時間勤務区分の判断
	 * @param timeLeaves　出退勤
	 * @param shortTimeSheet 短時間勤務時間帯
	 * @return
	 */
	public static ShortTimeSheetAtr decisionShortTimeAtr(List<TimeLeavingWork> timeLeaves, ShortWorkingTimeSheet shortTimeSheet) {

		//if(shortTimeSheet.getStartTime() == null || shortTimeSheet.getEndTime() == null) return returnEnum; 
		for(TimeLeavingWork tlw : timeLeaves) {
			TimeSpanForDailyCalc shortTimeSpan = new TimeSpanForDailyCalc(shortTimeSheet.getStartTime(), shortTimeSheet.getEndTime());
			//短時間.終了 <= 出勤
			//短時間.開始 <= 出勤 <= 短時間.終了
			if(tlw.getTimespan().getStart().greaterThan(shortTimeSheet.getEndTime())
			 ||shortTimeSpan.contains(tlw.getTimespan().getStart())) {
				return ShortTimeSheetAtr.BEFORE_ATTENDANCE;
			}
			//退勤 <= 短時間.開始
			//短時間.開始 <= 退勤 <= 短時間.終了
			else if(shortTimeSheet.getStartTime().greaterThan(tlw.getTimespan().getEnd())
				  ||shortTimeSpan.contains(tlw.getTimespan().getEnd())) {
				return ShortTimeSheetAtr.AFTER_LEAVING;
			}
		}
		return ShortTimeSheetAtr.WORKING_TIME;
	}

	/**
	 * 丸め処理
	 * @return
	 */
	public static List<TimeSheetOfDeductionItem> rounding(DeductionAtr dedAtr,List<TimeSheetOfDeductionItem> timeSheetOfDeductionItemList,TotalRoundingSet goOutSet,
														 TimeWithDayAttr mostFastWithinFrameOclock,TimeWithDayAttr mostLateWithinFrameOclock,
														 TimeSpanForDailyCalc oneDayRange){
		//if(goOutSet.ge){
//		if(false) {
//			val correctList = perRounding(timeSheetOfDeductionItemList,mostFastWithinFrameOclock,mostLateWithinFrameOclock,oneDayRange);
//			return correctList;
//		}
		return timeSheetOfDeductionItemList;
	}
	
	/**
	 * 計上時間の丸め設定.丸め方法 = 個別丸め　の分岐後処理は
	 * ここで行う。
	 * @param timeSheetOfDeductionItemList
	 * @param mostFastWithinFrameOclock　一番前の就業時間の開始時刻
	 * @param mostLateWithinFrameOclock 一番後ろの就業時間の終了時刻
	 * @return
	 */
	public static List<TimeSheetOfDeductionItem> perRounding(List<TimeSheetOfDeductionItem> timeSheetOfDeductionItemList,TimeWithDayAttr mostFastWithinFrameOclock,TimeWithDayAttr mostLateWithinFrameOclock,
															 TimeSpanForDailyCalc oneDayRange){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		//早出と就業時間帯.開始を跨いでる物の分割
		List<TimeSheetOfDeductionItem> containedMostFast = timeSheetOfDeductionItemList.stream().filter(tc -> tc.getTimeSheet().getTimeSpan().contains(mostFastWithinFrameOclock)).collect(Collectors.toList());
		//↑のリストに対して独自丸め実行
		returnList.addAll(ownRounding(containedMostFast, mostFastWithinFrameOclock));
		
		//就業時間帯.終了と残業を跨いでる物の分割
		List<TimeSheetOfDeductionItem> containedMostLate = timeSheetOfDeductionItemList.stream().filter(tc -> tc.getTimeSheet().getTimeSpan().contains(mostLateWithinFrameOclock)).collect(Collectors.toList());
		//↑のリストに対して独自丸め実行
		returnList.addAll(ownRounding(containedMostLate, mostLateWithinFrameOclock));
		
		List<TimeSheetOfDeductionItem> recreateList = new ArrayList<>();
		for(TimeSheetOfDeductionItem item : timeSheetOfDeductionItemList) {
			if(!containedMostFast.contains(item) && !containedMostLate.contains(item) ) {
				recreateList.add(item);
			}
		}
		recreateList.addAll(returnList);
		recreateList = recreateList.stream().sorted((first,second) -> first.getTimeSheet().getTimeSpan().getStart().compareTo(second.getTimeSheet().getTimeSpan().getEnd())).collect(Collectors.toList());
		
		
		//休憩・外出時間帯の抜き出し & 各控除時間帯を丸め処理
		//recreateList.stream().filter(tc -> tc.getDeductionAtr().isGoOut()).;
		
		//各控除時間帯を丸目後時刻補正
		List<TimeSheetOfDeductionItem> correctedTimeList = correctTimeAfterCorrect(returnList,oneDayRange,mostFastWithinFrameOclock,mostLateWithinFrameOclock);
		//外出・休憩丸め設定をクリア
		correctedTimeList.forEach(tc -> {
			if(tc.getDeductionAtr().isBreak() || tc.getDeductionAtr().isGoOut())
				tc.getRounding().setDefaultDataRoundingDown();;
		});
		return correctedTimeList;
	}
	
	/**
	 * 対象の時間帯を基準点の前後時間帯へと切り離す 
	 * @param containedList　対象時間帯
	 * @param baseOclock　基準時間
	 * @return　前後時間帯
	 */
	private static List<TimeSheetOfDeductionItem> ownRounding(List<TimeSheetOfDeductionItem> containedList,TimeWithDayAttr baseOclock){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		containedList.forEach(tc ->{
			//残業と就業時間帯を跨いでいる物の分割(開始～基準点)
			val devidedTimeSheet1 = divideBaseTime(tc,baseOclock,true);
			//残業と就業時間帯を跨いでいる物の分割(基準点～終了)
			val devidedTimeSheet2 = divideBaseTime(tc,baseOclock,false);
			
			returnList.add(devidedTimeSheet1);
			returnList.add(devidedTimeSheet2);
		});
		return returnList;
	}
	
	/**
	 * 基準点を基に、分割
	 * @param baseTimeSheet　分割する時間帯
	 * @param baseTime　基準時間
	 * @param isBefore　基準時間より前に変換する
	 * @return　変換後時間帯
	 */
	private static TimeSheetOfDeductionItem divideBaseTime(TimeSheetOfDeductionItem baseTimeSheet,TimeWithDayAttr baseTime,boolean isBefore) {
		//isBeforeを基に、開始～基準　or 基準～終了　の時間帯へ変換
		val devideTimeSheet = baseTimeSheet.reCreateOwn(baseTime, isBefore);
		//丸め(↑の開始からの距離を丸める)
		val roundingValue1 = devideTimeSheet.getRounding().round(devideTimeSheet.getTimeSheet().getTimeSpan().lengthAsMinutes());
		//丸め後に再作成(開始 + ↑の丸め後の値を基準点とし、開始～開始＋丸め後値で再作成する)
		return devideTimeSheet.reCreateOwn(devideTimeSheet.getTimeSheet().getTimeSpan().getStart().forwardByMinutes(roundingValue1), isBefore);
	}

	/**
	 * 丸め後の重複部分を補正
	 * @param returnList
	 * @param oneDayRange
	 * @return
	 */
	private static List<TimeSheetOfDeductionItem> correctTimeAfterCorrect(List<TimeSheetOfDeductionItem> returnList,TimeSpanForDailyCalc oneDayRange,TimeWithDayAttr startOclock, TimeWithDayAttr endOclock) {
		//returnListの末尾1個手前までループ
		for(int beforeNo = 0 ; beforeNo < returnList.size() - 1 ; beforeNo++) {
			TimeSpanForDailyCalc maxCorrectRange = getMaxCorrectRange(returnList.get(beforeNo),oneDayRange,startOclock,endOclock);
			TimeWithDayAttr baseTime = maxCorrectRange.getTimeSpan().getStart();
			if(returnList.get(beforeNo).getTimeSheet().getTimeSpan().contains(baseTime)) {
				
				//最大範囲を超えている分手前にずらす & 控除時間帯リストを更新
				returnList = moveBack(returnList,beforeNo,maxCorrectRange);
			}
			val nextTimeSheet = returnList.get(beforeNo + 1);
			//重複の判断処理
			if(returnList.get(beforeNo).getTimeSheet().getDuplicatedWith(nextTimeSheet.getTimeSheet()).isPresent()) {
				//重複している分次の時間帯を後ろにずらす
				returnList.set(beforeNo + 1, nextTimeSheet.cloneWithNewTimeSpan(Optional.of(returnList.get(beforeNo).getTimeSheet().reviseToAvoidDuplicatingWith(nextTimeSheet.getTimeSheet()))));
			}
		}
		return returnList;
	}

	/**
	 * 手前にずらす処理
	 * @param returnList　処理対象となる控除項目時間帯リスト
	 * @param listNo 処理対象のリスト番号
	 * @param maxCorrectRange 最大補正時間
	 * @return　補正後時間
	 */
	private static List<TimeSheetOfDeductionItem> moveBack(List<TimeSheetOfDeductionItem> returnList, int listNo,TimeSpanForDailyCalc maxCorrectRange) {
		TimeSheetOfDeductionItem targetTimeSheet = returnList.get(listNo);
		Optional<TimeSpanForDailyCalc> dupTimeSpan = targetTimeSheet.getTimeSheet().getDuplicatedWith(maxCorrectRange);
		//手前にずらす
		targetTimeSheet = targetTimeSheet.cloneWithNewTimeSpan(Optional.of(targetTimeSheet.getTimeSheet().shiftBack(dupTimeSpan.orElse(new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(0))).getTimeSpan().lengthAsMinutes()))); 
		returnList.set(listNo, targetTimeSheet);
		//手前件数分ループ
		for(int nowListNo = listNo - 1 ; 0 <= nowListNo ; nowListNo--) {
			//手前の時間帯取得
			val duplicateRange = targetTimeSheet.getTimeSheet().getTimeSpan().getDuplicatedWith(returnList.get(nowListNo).getTimeSheet().getTimeSpan());
			//一様Optionalチェック(EAには無し)
			if(duplicateRange.isPresent()) {
				//手前にずらす
				TimeSpanForDailyCalc afterMoveTimeSpan = returnList.get(nowListNo).getTimeSheet().shiftBack(duplicateRange.get().lengthAsMinutes());
				//↑でずらした結果、最大範囲を含んでいるか
				if(afterMoveTimeSpan.getTimeSpan().contains(maxCorrectRange.getTimeSpan())) {
					//時間帯補正
					afterMoveTimeSpan = new TimeSpanForDailyCalc(maxCorrectRange.getTimeSpan().getStart(), afterMoveTimeSpan.getTimeSpan().getEnd());
				}
				//時間帯のセット
				returnList.set(nowListNo, returnList.get(nowListNo).cloneWithNewTimeSpan(Optional.of(afterMoveTimeSpan)));
			}
		}
		
		return returnList;
	}

	/**
	 * 最大補正範囲を取得
	 * @param oneDayRange 1日の範囲
	 * @return 最大補正範囲
	 */
	private static TimeSpanForDailyCalc getMaxCorrectRange(TimeSheetOfDeductionItem deductionTimeSheet ,TimeSpanForDailyCalc oneDayRange,TimeWithDayAttr startOclock, TimeWithDayAttr endOclock) {
		//就業時間内.開始 > 控除時間帯.開始
		if(startOclock.greaterThan(deductionTimeSheet.getTimeSheet().getTimeSpan().getStart())) {
			return new TimeSpanForDailyCalc(oneDayRange.getTimeSpan().getStart(), startOclock);
		}
		//>就業時間内.終了 > 控除時間帯.開始
		if(endOclock.greaterThan(deductionTimeSheet.getTimeSheet().getTimeSpan().getStart())){
			return new TimeSpanForDailyCalc(oneDayRange.getTimeSpan().getStart(),startOclock);
		}
		else {
			return new TimeSpanForDailyCalc(endOclock, oneDayRange.getTimeSpan().getEnd());
		}
	}
	
	/**
	 * 指定条件の時間帯部分を除く
	 * @param deductionAtr 控除区分
	 * @param deductionClass 控除種別
	 * @param timeSpan 時間帯
	 * @return 控除時間帯
	 */
	public DeductionTimeSheet exceptTimeSheet(
			DeductionAtr deductionAtr,
			DeductionClassification deductionClass,
			TimeSpanForDailyCalc timeSpan){

		// 処理後控除用リスト
		List<TimeSheetOfDeductionItem> afterDudList = this.forDeductionTimeZoneList;
		// 処理後計上用リスト
		List<TimeSheetOfDeductionItem> afterRecList = this.forRecordTimeZoneList;
		switch (deductionAtr){
		case Deduction:
			afterDudList = this.getTimeSheetListExceptTimeSheet(DeductionAtr.Deduction, deductionClass, timeSpan);
			break;
		case Appropriate:
			afterRecList = this.getTimeSheetListExceptTimeSheet(DeductionAtr.Appropriate, deductionClass, timeSpan);
			break;
		}
		return new DeductionTimeSheet(
				afterDudList,
				afterRecList,
				this.breakTimeOfDailyList,
				this.dailyGoOutSheet,
				this.shortTimeSheets);
	}
	
	/**
	 * 指定条件の時間帯部分を除いた時間帯リストを取得
	 * @param deductionAtr 控除区分
	 * @param deductionClass 控除種別
	 * @param timeSpan 時間帯
	 * @return 結果時間帯リスト
	 */
	private List<TimeSheetOfDeductionItem> getTimeSheetListExceptTimeSheet(
			DeductionAtr deductionAtr,
			DeductionClassification deductionClass,
			TimeSpanForDailyCalc timeSpan){
	
		// 対象時間帯リスト
		List<TimeSheetOfDeductionItem> target = this.forDeductionTimeZoneList;
		if (deductionAtr == DeductionAtr.Appropriate) target = this.forRecordTimeZoneList;
		// 結果時間帯リスト
		List<TimeSheetOfDeductionItem> result = new ArrayList<>();
		
		result.addAll(target.stream()
				.filter(c -> !c.getDeductionAtr().equals(deductionClass)).collect(Collectors.toList()));
		
		// 対象種別時間帯リスト
		List<TimeSheetOfDeductionItem> targetClass = target.stream()
				.filter(c -> c.getDeductionAtr().equals(deductionClass)).collect(Collectors.toList());
		for (TimeSheetOfDeductionItem targetItem : targetClass){
			// 重複外時間帯リスト
			List<TimeSpanForDailyCalc> notDupList = targetItem.getTimeSheet().getNotDuplicationWith(timeSpan);
			result.addAll(notDupList.stream()
							.map(c -> targetItem.cloneWithNewTimeSpan(Optional.of(c))).collect(Collectors.toList()));
		}
		
		return result.stream()
				.sorted((a, b) -> a.getTimeSheet().getStart().compareTo(b.getTimeSheet().getStart()))
				.collect(Collectors.toList());
	}
}
