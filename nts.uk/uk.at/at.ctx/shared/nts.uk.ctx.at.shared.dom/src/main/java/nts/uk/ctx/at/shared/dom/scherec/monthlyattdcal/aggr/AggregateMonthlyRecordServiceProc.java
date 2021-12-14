package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
//import lombok.extern.slf4j.Slf4j;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyAmountMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimeMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimesMonth;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.GetRemainingNumberPublicHolidayService;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsRecRemainMngOfInPeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.GetDaysForCalcAttdRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffRemainMngOfInPeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.JobTitleId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UnitAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.roundingset.RoundingSetOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.AggregateAttendanceTimeValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyOldDatas;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.anyitem.AnyItemAggrResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.excessoutside.ExcessOutsideWorkMng;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AggregateAffiliationInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AggregateAnyItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyAggregateAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ErrorType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.Flex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.aggframe.OuenAggregateFrameSetOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.totalcount.TotalCountByPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.VerticalTotalOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalcResultOfAnyItem;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.GetPeriodExcluseEntryRetireTime;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 処理：ドメインサービス：月別実績を集計する
 *
 * @author shuichi_ishida
 */
@Getter
/* @Slf4j */
public class AggregateMonthlyRecordServiceProc {

	/** 集計結果 */
	private AggregateMonthlyRecordValue aggregateResult;
	/** エラー情報 */
	private Map<String, MonthlyAggregationErrorInfo> errorInfos;
	/** 編集状態リスト */
	private List<EditStateOfMonthlyPerformance> editStates;

	/** 会社ID */
	@Setter
	private String companyId;
	/** 社員ID */
	@Setter
	private String employeeId;
	/** 年月 */
	private YearMonth yearMonth;
	/** 締めID */
	private ClosureId closureId;
	/** 締め日 */
	private ClosureDate closureDate;

	/** 月別集計で必要な会社別設定 */
	@Setter
	private MonAggrCompanySettings companySets;
	/** 月別集計で必要な社員別設定 */
	private MonAggrEmployeeSettings employeeSets;
	/** 月の計算中の日別実績データ */
	@Setter
	private MonthlyCalculatingDailys monthlyCalculatingDailys;
	/** 集計前の月別実績データ */
	private MonthlyOldDatas monthlyOldDatas;
	/** 労働条件項目 */
	private List<WorkingConditionItem> workingConditionItems;
	/** 労働条件 */
	private Map<String, DatePeriod> workingConditions;
	/** 週NO管理 */
	private Map<YearMonth, Integer> weekNoMap;
	/** 手修正あり */
	private boolean isRetouch;
	/** 暫定残数データ */
	private Map<GeneralDate, DailyInterimRemainMngData> dailyInterimRemainMngs;
	/** 暫定残数データ上書きフラグ */
	private boolean isOverWriteRemain;

	public AggregateMonthlyRecordServiceProc() {
	}

	/**
	 * 集計処理
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param datePeriod 期間
	 * @param prevAggrResult 前回集計結果 （年休積立年休の集計結果）
	 * @param prevAbsRecResultOpt 前回集計結果 （振休振出の集計結果）
	 * @param prevBreakDayOffResultOpt 前回集計結果 （代休の集計結果）
	 * @param prevSpecialLeaveResultMap 前回集計結果 （特別休暇の集計結果）
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param dailyWorksOpt 日別実績(WORK)List
	 * @param monthlyWorkOpt 月別実績(WORK)
	 * @param remainingProcAtr 残数処理フラグ
	 * @return 集計結果
	 */
	public AggregateMonthlyRecordValue aggregate(RequireM15 require, CacheCarrier cacheCarrier, String companyId,
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate, DatePeriod datePeriod,
			Optional<AbsRecRemainMngOfInPeriod> prevAbsRecResultOpt,
			Optional<BreakDayOffRemainMngOfInPeriod> prevBreakDayOffResultOpt,
			MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			Optional<List<IntegrationOfDaily>> dailyWorksOpt, Optional<IntegrationOfMonthly> monthlyWorkOpt,
			Boolean remainingProcAtr) {

		this.aggregateResult = new AggregateMonthlyRecordValue();
		this.errorInfos = new HashMap<>();
		this.editStates = new ArrayList<>();

		this.companyId = companyId;
		this.employeeId = employeeId;
		this.yearMonth = yearMonth;
		this.closureId = closureId;
		this.closureDate = closureDate;
		this.weekNoMap = new HashMap<>();
		this.isRetouch = false;
		this.dailyInterimRemainMngs = new HashMap<>();
		this.isOverWriteRemain = false;

		ConcurrentStopwatches.start("12100:集計期間ごと準備：");

		this.companySets = companySets;
		this.employeeSets = employeeSets;

		// 社員を取得する
		EmployeeImport employee = this.employeeSets.getEmployee();

		// 入社前、退職後を期間から除く → 一か月の集計期間
		DatePeriod monthPeriod = GetPeriodExcluseEntryRetireTime.getPeriodExcluseEntryRetireTime(new GetPeriodExcluseEntryRetireTime.Require() {

			@Override
			public EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId) {
				return employee;
			}
		}, cacheCarrier, this.employeeId, datePeriod).orElse(null);

		if (monthPeriod == null) {
			// 処理期間全体が、入社前または退職後の時
			return this.aggregateResult;
		}

		// 前月の36協定の集計がありえるため、前月分のデータも読み込んでおく （Redmine#107701）
		DatePeriod loadPeriod = new DatePeriod(monthPeriod.start(), monthPeriod.end());
		if (monthPeriod.start().after(employee.getEntryDate())) { // 開始日が入社日より後の時のみ、前月を読み込む
			loadPeriod = new DatePeriod(monthPeriod.start().addMonths(-1), monthPeriod.end());
		}

		// 計算に必要なデータを準備する
		this.monthlyCalculatingDailys = MonthlyCalculatingDailys.loadData(require,
				employeeId, loadPeriod, dailyWorksOpt, employeeSets);

		// 集計前の月別実績データを確認する
		this.monthlyOldDatas = MonthlyOldDatas.loadData(require, employeeId, yearMonth,
				closureId, closureDate, monthlyWorkOpt);

		// 「労働条件項目」を取得
		val workingConditions = require.workingCondition(employeeId, monthPeriod);
		val workingConditionItems = workingConditions.stream().map(c -> c.getWorkingConditionItem()).collect(Collectors.toList());
		if (workingConditionItems.isEmpty()) {
			this.aggregateResult.addErrorInfos("001", new ErrMessageContent(TextResource.localize("Msg_430")));
			return this.aggregateResult;
		}

		// 同じ労働制の履歴を統合
		this.IntegrateHistoryOfSameWorkSys(require, workingConditionItems);

		// 所属情報の作成
		val affiliationInfo = this.createAffiliationInfo(monthPeriod, workingConditions);

		if (affiliationInfo == null) {
			for (val errorInfo : this.errorInfos.values()) {
				this.aggregateResult.getErrorInfos().putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}
			return this.aggregateResult;
		}
		this.aggregateResult.setAffiliationInfo(Optional.of(affiliationInfo));

		// 残数と集計の処理を並列で実行
		{
			MutableValue<RuntimeException> excepAggregation = new MutableValue<>();

			// 日別修正等の画面から実行されている場合、集計処理を非同期で実行
			try {
				this.aggregationProcess(require, cacheCarrier, monthPeriod);
			} catch (RuntimeException ex) {
				excepAggregation.set(ex);
			}

			// 残数処理
			// こちらはDB書き込みをしているので非同期化できない
			this.remainingProcess(require, cacheCarrier, monthPeriod, companyId, employeeId, yearMonth, closureId,   closureDate,
					companySets, employeeSets, monthlyCalculatingDailys, datePeriod, remainingProcAtr);

			// 集計処理で例外が起きていたらここでthrow
			if (excepAggregation.optional().isPresent()) {
				throw excepAggregation.get();
			}
		}

		if (this.aggregateResult.getAttendanceTime().isPresent()) {

			/** 応援作業の集計 */
			this.aggregateResult.getAttendanceTime().get().aggregateOuen(
					createAttendanceTimeOfMonthlyRequire(require, dailyWorksOpt),
					employeeId, loadPeriod);
		}

		// 月別実績の編集状態 取得
		this.editStates = require.monthEditStates(this.employeeId, this.yearMonth, this.closureId, this.closureDate);

		if (this.aggregateResult.getAttendanceTime().isPresent()) {
			AttendanceTimeOfMonthly attendanceTime = this.aggregateResult.getAttendanceTime().get();

			// 手修正された項目を元に戻す （勤怠時間用）
			attendanceTime = this.undoRetouchValuesForAttendanceTime(require, attendanceTime, this.monthlyOldDatas);

			if (this.isRetouch) {
				val converter = require.createMonthlyConverter();
				val edittedItemIds = this.editStates.stream().map(c -> c.getAttendanceItemId()).collect(Collectors.toList());
				val oldValues = converter.withAttendanceTime(attendanceTime).convert(edittedItemIds);

				// 手修正を戻してから計算必要な項目を再度計算
				val reCalcedAt = this.recalcAttendanceTime(attendanceTime); 
				
				/** 手修正された項目を元に戻す */
				converter.withAttendanceTime(reCalcedAt).merge(oldValues);
				val lastAggrAt = converter.toAttendanceTime();
				
				this.aggregateResult.setAttendanceTime(lastAggrAt);
			}
		}

		ConcurrentStopwatches.start("12300:36協定時間：");

		// 基本計算結果を確認する
		Optional<MonthlyCalculation> basicCalced = Optional.empty();
		if (this.aggregateResult.getAttendanceTime().isPresent()) {
			basicCalced = Optional.of(this.aggregateResult.getAttendanceTime().get().getMonthlyCalculation());
		}

		// 36協定時間の集計
		{
			// 36協定時間の集計
			MonthlyCalculation monthlyCalculationForAgreement = new MonthlyCalculation();

			val agreementTime = monthlyCalculationForAgreement.aggregateAgreementTime(require, cacheCarrier,
					this.companyId, this.employeeId, this.yearMonth, this.closureId, this.closureDate, monthPeriod,
					Optional.empty(), Optional.empty(), Optional.empty(), this.companySets, this.employeeSets,
					this.monthlyCalculatingDailys, this.monthlyOldDatas, basicCalced);

			if (agreementTime.getAgreementTime().isPresent()) {

				this.aggregateResult.setAgreementTime(agreementTime.getAgreementTime());
			} else {
				if (!agreementTime.getError().isEmpty()) {
					val error = agreementTime.getError().get(0);
					this.aggregateResult.addErrorInfos(error.getResourceId(), error.getMessage());
				}
			}

			// 36協定運用設定を取得
			if (this.companySets.getAgreementOperationSet().isPresent()) {
				AgreementOperationSetting agreementOpeSet = this.companySets.getAgreementOperationSet().get();

				if (!this.closureDate.equals(agreementOpeSet.getClosureDate()) && // 締めが異なる
																						// かつ
						monthPeriod.start().after(employee.getEntryDate())) { // 開始日が入社日より後の時

					// 集計期間を一ヶ月手前にずらす
					YearMonth prevYM = this.yearMonth.addMonths(-1);
					GeneralDate prevEnd = monthPeriod.start().addDays(-1);
					GeneralDate prevStart = prevEnd.addMonths(-1).addDays(1);
					DatePeriod prevPeriod = new DatePeriod(prevStart, prevEnd);

					// 36協定時間の集計
					MonthlyOldDatas prevOldDatas = MonthlyOldDatas.loadData(require, employeeId, prevYM, closureId, closureDate,
							Optional.empty());
					MonthlyCalculation prevCalculationForAgreement = new MonthlyCalculation();

					val prevAgreTime = prevCalculationForAgreement.aggregateAgreementTime(require,
							cacheCarrier, this.companyId, this.employeeId, prevYM, this.closureId, this.closureDate,
							prevPeriod, Optional.empty(), Optional.empty(), Optional.empty(), this.companySets,
							this.employeeSets, this.monthlyCalculatingDailys, prevOldDatas, Optional.empty());

					if (prevAgreTime.getAgreementTime().isPresent()) {

						this.aggregateResult.setPrevAgreementTime(prevAgreTime.getAgreementTime());
					} else {
						if (!prevAgreTime.getError().isEmpty()) {
							val error = prevAgreTime.getError().get(0);
							this.aggregateResult.addErrorInfos(error.getResourceId(), error.getMessage());
						}
					}
				}
			}
		}

		ConcurrentStopwatches.stop("12300:36協定時間：");
		ConcurrentStopwatches.start("12500:任意項目：");

		Map<Integer, Map<Integer, AnyItemAggrResult>> anyItemCustomizeValue = new HashMap<>();
		/** 大塚モードを確認する */
		if (AppContexts.optionLicense().customize().ootsuka()) {
			// 大塚カスタマイズの集計
			anyItemCustomizeValue = this
					.aggregateCustomizeForOtsuka(require, cacheCarrier, this.yearMonth, this.closureId, this.companySets);
		} else {
			// 任意項目カスタマイズ値 ※ 最初のInteger=0（月結果）、1～（各週結果（週No））
			anyItemCustomizeValue.put(0, new HashMap<>()); // 月結果
		}

		// 月別実績の任意項目を集計
		this.aggregateAnyItem(require, monthPeriod, anyItemCustomizeValue);

		// 手修正された項目を元に戻す （任意項目用）
		this.undoRetouchValuesForAnyItems(require, this.monthlyOldDatas);

		ConcurrentStopwatches.stop("12500:任意項目：");

		// 戻り値にエラー情報を移送
		for (val errorInfo : this.errorInfos.values()) {
			this.aggregateResult.getErrorInfos().putIfAbsent(errorInfo.getResourceId(), errorInfo);
		}

		return this.aggregateResult;
	}

	/**
	 * 集計処理
	 *
	 * @param monthPeriod
	 */
	private void aggregationProcess(RequireM13 require, CacheCarrier cacheCarrier, DatePeriod monthPeriod) {
		ConcurrentStopwatches.stop("12100:集計期間ごと準備：");

		// 項目の数だけループ
		List<Flex> perErrorsForFlex = new ArrayList<>();
		for (val workingConditionItem : this.workingConditionItems) {

			ConcurrentStopwatches.start("12200:労働条件ごと：");

			// 「労働条件」の該当履歴から期間を取得
			val historyId = workingConditionItem.getHistoryId();
			if (!this.workingConditions.containsKey(historyId))
				continue;

			// 処理期間を計算 （一か月の集計期間と労働条件履歴期間の重複を確認する）
			val term = this.workingConditions.get(historyId);
			DatePeriod aggrPeriod = GetPeriodExcluseEntryRetireTime.confirmProcPeriod(monthPeriod, term).orElse(null);
			if (aggrPeriod == null) {
				// 履歴の期間と重複がない時
				continue;
			}

			// 月別実績の勤怠時間を集計
			val aggregateResult = this.aggregateAttendanceTime(require, cacheCarrier, aggrPeriod, workingConditionItem);
			val attendanceTime = aggregateResult.getAttendanceTime();
			if (attendanceTime == null)
				continue;

			// 社員の月別実績のエラー（フレックス不足補填）を確認する
			val resultPerErrorsForFlex = attendanceTime.getMonthlyCalculation().getFlexTime().getPerErrors();
			for (val resultPerError : resultPerErrorsForFlex) {
				if (!perErrorsForFlex.contains(resultPerError))
					perErrorsForFlex.add(resultPerError);
			}

			// データを合算する
			if (this.aggregateResult.getAttendanceTime().isPresent()) {
				val calcedAttendanceTime = this.aggregateResult.getAttendanceTime().get();
				attendanceTime.sum(calcedAttendanceTime);
			}

			// 計算中のエラー情報の取得
			val monthlyCalculation = attendanceTime.getMonthlyCalculation();
			for (val errorInfo : monthlyCalculation.getErrorInfos()) {
				this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}

			// 計算結果を戻り値に蓄積
			this.aggregateResult.setAttendanceTime(Optional.of(attendanceTime));
			this.aggregateResult.getAttendanceTimeWeeks().addAll(aggregateResult.getAttendanceTimeWeeks());

			ConcurrentStopwatches.stop("12200:労働条件ごと：");
		}
		
		if (this.aggregateResult.getAttendanceTime().isPresent()) {
			/** 月別実績の時間項目を丸める */
			val rounded = this.companySets.getRoundingSet().round(require, this.aggregateResult.getAttendanceTime().get());
			this.aggregateResult.setAttendanceTime(Optional.of(rounded));
		}

		// 社員の月別実績のエラー（フレックス不足補填）を出力する
		for (val perError : perErrorsForFlex) {
			this.aggregateResult.getPerErrors().add(new EmployeeMonthlyPerError(ErrorType.FLEX, this.yearMonth,
					this.employeeId, this.closureId, this.closureDate, perError, null, null));
		}

		// 合算後のチェック処理
		this.checkAfterSum(monthPeriod);
	}

	/**
	 * 残数処理
	 * @param monthPeriod 1か月の集計期間
	 * @param datePeriod 期間（実行期間）
	 * @param remainingProcAtr 残数処理フラグ
	 */
	private void remainingProcess(RequireM8 require,CacheCarrier cacheCarrier, DatePeriod period,
			String companyId, String employeeId, YearMonth yearMonth, ClosureId closureId,   ClosureDate closureDate,
			MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalculatingDailys,
			DatePeriod datePeriod, Boolean remainingProcAtr) {

		ConcurrentStopwatches.start("12400:残数処理：");

		// 当月の判断
		boolean isCurrentMonth = false;
		if (this.companySets.getCurrentMonthPeriodMap().containsKey(this.closureId.value)) {
			DatePeriod currentPeriod = this.companySets.getCurrentMonthPeriodMap().get(this.closureId.value);
			// 当月の締め期間と実行期間を比較し、重複した期間を取り出す
			// 重複期間があれば、当月
			if (GetPeriodExcluseEntryRetireTime.confirmProcPeriod(currentPeriod, datePeriod).isPresent())
				isCurrentMonth = true;
		}
		if (remainingProcAtr == true)
			isCurrentMonth = true;

		// 2019.4.25 UPD shuichi_ishida Redmine#107271(1)(EA.3359)
		// 残数処理を実行する当月判断方法を変更
		// 集計開始日を締め開始日をする時だけ、残数処理を実行する （集計期間の初月（＝締めの当月）だけ実行する）
		// if (this.employeeSets.isNoCheckStartDate()){
		// 実行期間が当月の締め期間に含まれる時だけ、残数処理を実行する
		if (isCurrentMonth) {

			// 残数処理
			this.remainingProcess(require, cacheCarrier, datePeriod, companyId, employeeId, yearMonth, closureId,
					closureDate, companySets, employeeSets, monthlyCalculatingDailys,  InterimRemainMngMode.MONTHLY, true);
		}

		ConcurrentStopwatches.stop("12400:残数処理：");
	}

	/**
	 * 同じ労働制の履歴を統合
	 *
	 * @param target 労働条件項目リスト （統合前）
	 * @return 労働条件項目リスト （統合後）
	 */
	private void IntegrateHistoryOfSameWorkSys(RequireM14 require, List<WorkingConditionItem> target) {

		this.workingConditionItems = new ArrayList<>();
		this.workingConditions = new HashMap<>();

		val itrTarget = target.listIterator();
		while (itrTarget.hasNext()) {

			// 要素[n]を取得
			WorkingConditionItem startItem = itrTarget.next();
			val startHistoryId = startItem.getHistoryId();
			val startConditionOpt = require.workingCondition(startHistoryId);
			if (!startConditionOpt.isPresent())
				continue;
			val startCondition = startConditionOpt.get();
			if (startCondition.getDateHistoryItem().isEmpty())
				continue;
			DatePeriod startPeriod = startCondition.getDateHistoryItem().get(0).span();

			// 要素[n]と要素[n+1]以降を順次比較
			WorkingConditionItem endItem = null;
			while (itrTarget.hasNext()) {
				WorkingConditionItem nextItem = target.get(itrTarget.nextIndex());
				if (startItem.getLaborSystem() != nextItem.getLaborSystem()
						|| startItem.getHourlyPaymentAtr() != nextItem.getHourlyPaymentAtr()) {

					// 労働制または時給者区分が異なる履歴が見つかった時点で、労働条件の統合をやめる
					break;
				}

				// 労働制と時給者区分が同じ履歴の要素を順次取得
				endItem = itrTarget.next();
			}

			// 次の要素がなくなった、または、異なる履歴が見つかれば、集計要素を確定する
			if (endItem == null) {
				this.workingConditionItems.add(startItem);
				this.workingConditions.putIfAbsent(startHistoryId, startPeriod);
				continue;
			}
			val endHistoryId = endItem.getHistoryId();
			val endConditionOpt = require.workingCondition(endHistoryId);
			if (!endConditionOpt.isPresent())
				continue;
			;
			val endCondition = endConditionOpt.get();
			if (endCondition.getDateHistoryItem().isEmpty())
				continue;
			this.workingConditionItems.add(endItem);
			this.workingConditions.putIfAbsent(endHistoryId,
					new DatePeriod(startPeriod.start(), endCondition.getDateHistoryItem().get(0).end()));
		}
	}

	/**
	 * 月別実績の勤怠時間を集計
	 *
	 * @param monthPeriod 月の期間
	 * @param anyItemCustomizeValue 任意項目カスタマイズ値
	 */
	private AggregateAttendanceTimeValue aggregateAttendanceTime(RequireM13 require, CacheCarrier cacheCarrier,
			DatePeriod datePeriod, WorkingConditionItem workingConditionItem) {

		AggregateAttendanceTimeValue result = new AggregateAttendanceTimeValue();

		// 週Noを確認する
		this.weekNoMap.putIfAbsent(this.yearMonth, 0);
		val startWeekNo = this.weekNoMap.get(this.yearMonth) + 1;

		// 労働制を確認する
		val workingSystem = workingConditionItem.getLaborSystem();

		ConcurrentStopwatches.start("12210:集計準備：");

		// 月別実績の勤怠時間 初期設定
		val attendanceTime = new AttendanceTimeOfMonthly(this.employeeId, this.yearMonth, this.closureId,
				this.closureDate, datePeriod);
		attendanceTime.prepareAggregation(require, cacheCarrier, this.companyId, datePeriod,
				workingConditionItem, startWeekNo, this.companySets, this.employeeSets, this.monthlyCalculatingDailys,
				this.monthlyOldDatas);
		val monthlyCalculation = attendanceTime.getMonthlyCalculation();
		if (monthlyCalculation.getErrorInfos().size() > 0) {
			for (val errorInfo : monthlyCalculation.getErrorInfos()) {
				this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}
			return result;
		}

		ConcurrentStopwatches.stop("12210:集計準備：");
		ConcurrentStopwatches.start("12220:月の計算：");

		// 月の計算
		monthlyCalculation.aggregate(require, cacheCarrier, datePeriod, MonthlyAggregateAtr.MONTHLY, Optional.empty(),
				Optional.empty(), Optional.empty());

		ConcurrentStopwatches.stop("12220:月の計算：");
		ConcurrentStopwatches.start("12230:縦計：");

		// 縦計
		{
			// 週単位の期間を取得
			for (val attendanceTimeWeek : attendanceTime.getMonthlyCalculation().getAttendanceTimeWeeks()) {
				DatePeriod weekPeriod = attendanceTimeWeek.getPeriod();

				// 週の縦計
				val verticalTotalWeek = attendanceTimeWeek.getVerticalTotal();
				verticalTotalWeek.verticalTotal(require, this.companyId, this.employeeId, weekPeriod,
						workingSystem, this.companySets, this.employeeSets, this.monthlyCalculatingDailys);
			}

			// 月の縦計
			val verticalTotal = attendanceTime.getVerticalTotal();
			verticalTotal.verticalTotal(require, this.companyId, this.employeeId, datePeriod, workingSystem,
					this.companySets, this.employeeSets, this.monthlyCalculatingDailys);
		}

		ConcurrentStopwatches.stop("12230:縦計：");
		ConcurrentStopwatches.start("12240:時間外超過：");

		// 時間外超過
		ExcessOutsideWorkMng excessOutsideWorkMng = new ExcessOutsideWorkMng(monthlyCalculation);
		excessOutsideWorkMng.aggregate(require, cacheCarrier);
		if (excessOutsideWorkMng.getErrorInfos().size() > 0) {
			for (val errorInfo : excessOutsideWorkMng.getErrorInfos()) {
				this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}
		}
		attendanceTime.setExcessOutsideWork(excessOutsideWorkMng.getExcessOutsideWork());

		ConcurrentStopwatches.stop("12240:時間外超過：");
		ConcurrentStopwatches.start("12250:回数集計：");

		// 回数集計
		{
			// 週単位の期間を取得
			for (val attendanceTimeWeek : attendanceTime.getMonthlyCalculation().getAttendanceTimeWeeks()) {
				DatePeriod weekPeriod = attendanceTimeWeek.getPeriod();

				// 週の回数集計
				val totalCountWeek = attendanceTimeWeek.getTotalCount();
				totalCountWeek.totalize(require, this.companyId, this.employeeId, weekPeriod, this.companySets,
						this.monthlyCalculatingDailys);
				if (totalCountWeek.getErrorInfos().size() > 0) {
					for (val errorInfo : totalCountWeek.getErrorInfos()) {
						this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
					}
				}
			}

			// 月の回数集計
			val totalCount = attendanceTime.getTotalCount();
			totalCount.totalize(require, this.companyId, this.employeeId, datePeriod, this.companySets,
					this.monthlyCalculatingDailys);
			if (totalCount.getErrorInfos().size() > 0) {
				for (val errorInfo : totalCount.getErrorInfos()) {
					this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
				}
			}
		}

		ConcurrentStopwatches.stop("12250:回数集計：");

		// 集計結果を返す
		result.setAttendanceTime(attendanceTime);
		for (val attendanceTimeWeek : attendanceTime.getMonthlyCalculation().getAttendanceTimeWeeks()) {
			val nowWeekNo = this.weekNoMap.get(this.yearMonth);
			if (nowWeekNo < attendanceTimeWeek.getWeekNo()) {
				this.weekNoMap.put(this.yearMonth, attendanceTimeWeek.getWeekNo());
			}
			result.getAttendanceTimeWeeks().add(attendanceTimeWeek);
		}
		return result;
	}

	/**
	 * 月別実績の任意項目を集計
	 *
	 * @param monthPeriod
	 *            月の期間
	 * @param anyItemCustomizeValue
	 *            任意項目カスタマイズ値
	 */
	private void aggregateAnyItem(RequireM12 require, DatePeriod monthPeriod,
			Map<Integer, Map<Integer, AnyItemAggrResult>> anyItemCustomizeValue) {

		// 週単位の期間を取得
		ListIterator<AttendanceTimeOfWeekly> itrWeeks = this.aggregateResult.getAttendanceTimeWeeks().listIterator();
		while (itrWeeks.hasNext()) {
			AttendanceTimeOfWeekly attendanceTimeWeek = itrWeeks.next();

			// 週ごとの集計
			val weekResults = this.aggregateAnyItemPeriod(require, attendanceTimeWeek.getPeriod(), true,
					anyItemCustomizeValue.get(attendanceTimeWeek.getWeekNo()));
			for (val weekResult : weekResults.values()) {
				attendanceTimeWeek.getAnyItem().getAnyItemValues().put(weekResult.getOptionalItemNo(),
						AggregateAnyItem.of(weekResult.getOptionalItemNo(), weekResult.getAnyTime(),
								weekResult.getAnyTimes(), weekResult.getAnyAmount()));
			}
			itrWeeks.set(attendanceTimeWeek);
		}

		// 月ごとの集計
		val monthResults = this.aggregateAnyItemPeriod(require, monthPeriod, false, anyItemCustomizeValue.get(0));
		for (val monthResult : monthResults.values()) {
			this.aggregateResult.putAnyItemOrUpdate(AnyItemOfMonthly.of(this.employeeId, this.yearMonth, this.closureId,
					this.closureDate, monthResult));
		}
	}

	/**
	 * 大塚カスタマイズ （任意項目集計）
	 *
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 任意項目カスタマイズ値
	 */
	private Map<Integer, Map<Integer, AnyItemAggrResult>> aggregateCustomizeForOtsuka(RequireM11 require, CacheCarrier cacheCarrier,
			YearMonth yearMonth, ClosureId closureId, MonAggrCompanySettings companySets) {

		// 任意項目カスタマイズ値 ※ 最初のInteger=0（月結果）、1～（各週結果（週No））
		Map<Integer, Map<Integer, AnyItemAggrResult>> results = new HashMap<>();
		results.put(0, new HashMap<>()); // 月結果

		// 月ごとの集計
		AnyItemAggrResult monthResult = this.getPredWorkingDays(require, cacheCarrier, yearMonth, closureId, companySets);
		results.get(0).putIfAbsent(monthResult.getOptionalItemNo(), monthResult);

		// 任意項目カスタマイズ値を返す
		return results;
	}

	/**
	 * 任意項目期間集計
	 *
	 * @param period 期間
	 * @param isWeek 週間集計
	 * @param anyItemCustomizeValue 任意項目カスタマイズ値
	 * @return 任意項目集計結果
	 */
	private Map<Integer, AnyItemAggrResult> aggregateAnyItemPeriod(RequireM12 require, DatePeriod period, boolean isWeek,
			Map<Integer, AnyItemAggrResult> anyItemCustomizeValue) {
		List<AnyItemOfMonthly> anyItems = new ArrayList<>();

		return aggregateAnyItemPeriod(period, isWeek, false, anyItemCustomizeValue,
				this.monthlyCalculatingDailys, this.companySets, this.employeeSets,
				(optionalItem) -> monthlyCalc(require, anyItems, optionalItem),
				(anyItemAggrResult) -> anyItems.add(AnyItemOfMonthly.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate, anyItemAggrResult)));
	}

	public static Map<Integer, AnyItemAggrResult> aggregateAnyItemPeriod(DatePeriod period,
			boolean isWeek, boolean isPeriodAggr, Map<Integer, AnyItemAggrResult> anyItemCustomizeValue,
			MonthlyCalculatingDailys monthlyCalc, MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets, Function<OptionalItem, AnyItemAggrResult> monthlyUseGetter,
			Consumer<AnyItemAggrResult> consumer) {

		Map<Integer, AnyItemAggrResult> results = new HashMap<>();

		// 任意項目ごとに集計する
		Map<Integer, AggregateAnyItem> anyItemTotals = new HashMap<>();
		for (val anyItemValueOfDaily : monthlyCalc.getAnyItemValueOfDailyList().entrySet()){
			if (!period.contains(anyItemValueOfDaily.getKey())) continue;
			if (anyItemValueOfDaily.getValue().getItems() == null) continue;
			val ymd = anyItemValueOfDaily.getKey();
			for (val item : anyItemValueOfDaily.getValue().getItems()){
				if (item.getItemNo() == null) continue;
				Integer itemNo = item.getItemNo().v();

				if (period.contains(ymd)) {
					anyItemTotals.putIfAbsent(itemNo, new AggregateAnyItem(itemNo));
					anyItemTotals.get(itemNo).addFromDaily(item);
				}
			}
		}

		// 任意項目を取得
		for (val optionalItem : companySets.getOptionalItemMap().values()) {
			Integer optionalItemNo = optionalItem.getOptionalItemNo().v();

			/** 月間集計　＆＆　大塚モードを確認する */
			if (!isWeek && !isPeriodAggr && AppContexts.optionLicense().customize().ootsuka()) {
				// 大塚カスタマイズ （月別実績の任意項目←任意項目カスタマイズ値）
				if (anyItemCustomizeValue != null) {
					if (anyItemCustomizeValue.containsKey(optionalItemNo)) {
						results.put(optionalItemNo, anyItemCustomizeValue.get(optionalItemNo));
						consumer.accept(anyItemCustomizeValue.get(optionalItemNo));
						continue;
					}
				}
			}

			// 利用条件の判定
			Optional<EmpCondition> empCondition = Optional.empty();
			if (companySets.getEmpConditionMap().containsKey(optionalItemNo)) {
				empCondition = Optional.of(companySets.getEmpConditionMap().get(optionalItemNo));
			}
			val bsEmploymentHistOpt = employeeSets.getEmployment(period.end());
			switch(optionalItem.checkTermsOfUseMonth(empCondition, bsEmploymentHistOpt)){
			case USE:
				// 利用する
				{
					if (isWeek || isPeriodAggr) {
						break;
					}

					AnyItemAggrResult result = monthlyUseGetter.apply(optionalItem);
					results.put(optionalItemNo, result);
					break;
				}
			case DAILY_VTOTAL:
				// 日別縦計する
				{
					// 日別実績 縦計処理
					AnyItemAggrResult result = AnyItemAggrResult.calcFromDailys(optionalItemNo, optionalItem, anyItemTotals);

					if (!isWeek && !isPeriodAggr) {
						/** 上限下限チェック */
						result = limitCheck(optionalItem, optionalItemNo, result);
					}

					consumer.accept(result);

					results.put(optionalItemNo, result);
					break;
				}
			case NOT_USE:
				// 利用しない
				break;
			}
		}

		return results;
	}

	private AnyItemAggrResult monthlyCalc(RequireM12 require, List<AnyItemOfMonthly> anyItems, OptionalItem optionalItem) {

		int optionalItemNo = optionalItem.getOptionalItemNo().v();

		// 初期化
		AnyItemAggrResult result = AnyItemAggrResult.of(optionalItemNo, optionalItem);

		// 「実績区分」を判断
		if (this.aggregateResult.getAttendanceTime().isPresent()) {
			val attendanceTime = this.aggregateResult.getAttendanceTime().get();

			// 月別実績 計算処理
			result = AnyItemAggrResult.calcFromMonthly(require, optionalItemNo, optionalItem, attendanceTime, anyItems, this.companySets);
		}

		anyItems.add(AnyItemOfMonthly.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate, result));

		return result;
	}

	/** 上限下限チェック */
	private static AnyItemAggrResult limitCheck(OptionalItem optionalItem,
			Integer optionalItemNo, AnyItemAggrResult result) {
		val checkedResult = optionalItem.getCalcResultRange().checkRange(new CalcResultOfAnyItem(optionalItem.getOptionalItemNo(),
																						result.getAnyTimes().map(c -> c.v()),
																						result.getAnyTime().map(c -> BigDecimal.valueOf(c.v())),
																						result.getAnyAmount().map(c -> BigDecimal.valueOf(c.v()))),
																		optionalItem);

		return AnyItemAggrResult.of(optionalItemNo,
										checkedResult.getTime().map(c -> new AnyTimeMonth(c.intValue())),
										checkedResult.getCount().map(c -> new AnyTimesMonth(c.doubleValue())),
										checkedResult.getMoney().map(c -> new AnyAmountMonth(c.intValue())));
	}


	/**
	 * 計画所定労働日数
	 *
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 任意項目集計結果
	 */
	private AnyItemAggrResult getPredWorkingDays(RequireM11 require, CacheCarrier cacheCarrier,
			YearMonth yearMonth, ClosureId closureId, MonAggrCompanySettings companySets) {

		AnyItemAggrResult emptyResult = AnyItemAggrResult.of(69, Optional.empty(), Optional.of(new AnyTimesMonth(0.0)), Optional.empty());

		// 指定した年月の締め期間を取得する
		DatePeriod period = null;
		{
			// 対象の締めを取得する
			if (!companySets.getClosureMap().containsKey(closureId.value))
				return emptyResult;
			Closure closure = companySets.getClosureMap().get(closureId.value);

			// 指定した年月の期間をすべて取得する
			List<DatePeriod> periods = closure.getPeriodByYearMonth(yearMonth);
			if (periods.size() == 0)
				return emptyResult;

			// 期間を合算する
			GeneralDate startDate = periods.get(0).start();
			GeneralDate endDate = periods.get(0).end();
			if (periods.size() == 2) {
				if (startDate.after(periods.get(1).start()))
					startDate = periods.get(1).start();
				if (endDate.before(periods.get(1).end()))
					endDate = periods.get(1).end();
			}
			period = new DatePeriod(startDate, endDate);
		}

		// RQ608：指定期間の所定労働日数を取得する(大塚用)
		double predWorkingDays = require.monthAttendanceDays(cacheCarrier, period, this.companySets.getAllWorkTypeMap()).v();

		// 任意項目69へ格納
		return AnyItemAggrResult.of(69, Optional.empty(), Optional.of(new AnyTimesMonth(predWorkingDays)), Optional.empty());
	}

	/**
	 * 合算後のチェック処理
	 *
	 * @param period 期間
	 */
	private void checkAfterSum(DatePeriod period) {

		// 「日別実績の勤怠時間」を取得する
		if (!this.monthlyCalculatingDailys.isExistDailyRecord(period)) {
			val errorInfo = new MonthlyAggregationErrorInfo("018",
					new ErrMessageContent(TextResource.localize("Msg_1376")));
			this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			return;
		}

		if (this.aggregateResult.getAttendanceTime().isPresent()) {
			val attendanceTime = this.aggregateResult.getAttendanceTime().get();

			// フレックス時間を確認する
			val flexTime = attendanceTime.getMonthlyCalculation().getFlexTime();
			int flexMinutes = flexTime.getFlexTime().getFlexTime().getTime().v();
			if (flexMinutes < 0) {
				this.aggregateResult.getPerErrors().add(new EmployeeMonthlyPerError(ErrorType.FLEX_SUPP, this.yearMonth,
						this.employeeId, this.closureId, this.closureDate, null, null, null));
			}
		}

		// ※ 乖離フラグは、縦計の合算と同時に再チェックされるため、ここでは不要。
		// ※ 平均時間関連は、縦計の合算と同時に再計算されるため、ここでは不要。
	}

	/**
	 * 手修正された項目を元に戻す （勤怠時間用）
	 *
	 * @param attendanceTime 月別実績の勤怠時間
	 * @param monthlyOldDatas 集計前の月別実績データ
	 * @return 月別実績の勤怠時間
	 */
	private AttendanceTimeOfMonthly undoRetouchValuesForAttendanceTime(RequireM10 require,
			AttendanceTimeOfMonthly attendanceTime, MonthlyOldDatas monthlyOldDatas) {

		this.isRetouch = false;

		// 既存データを確認する
		val oldDataOpt = monthlyOldDatas.getAttendanceTime();
		if (!oldDataOpt.isPresent())
			return attendanceTime;
		val oldConverter = require.createMonthlyConverter();
		MonthlyRecordToAttendanceItemConverter oldItemConvert = oldConverter.withAttendanceTime(oldDataOpt.get());
		if (monthlyOldDatas.getAnnualLeaveRemain().isPresent()) {
			oldItemConvert = oldItemConvert.withAnnLeave(monthlyOldDatas.getAnnualLeaveRemain().get());
		}
		if (monthlyOldDatas.getReserveLeaveRemain().isPresent()) {
			oldItemConvert = oldItemConvert.withRsvLeave(monthlyOldDatas.getReserveLeaveRemain().get());
		}
		if (monthlyOldDatas.getAbsenceLeaveRemain().isPresent()) {
			oldItemConvert = oldItemConvert.withAbsenceLeave(monthlyOldDatas.getAbsenceLeaveRemain().get());
		}
		if (monthlyOldDatas.getMonthlyDayoffRemain().isPresent()) {
			oldItemConvert = oldItemConvert.withDayOff(monthlyOldDatas.getMonthlyDayoffRemain().get());
		}
		oldItemConvert = oldItemConvert.withSpecialLeave(monthlyOldDatas.getSpecialLeaveRemainList());

		// 計算後データを確認
		val monthlyConverter = require.createMonthlyConverter();
		MonthlyRecordToAttendanceItemConverter convert = monthlyConverter.withAttendanceTime(attendanceTime);
		if (this.aggregateResult.getAnnLeaRemNumEachMonthList().size() > 0) {
			convert = convert.withAnnLeave(this.aggregateResult.getAnnLeaRemNumEachMonthList().get(0));
		}
		if (this.aggregateResult.getRsvLeaRemNumEachMonthList().size() > 0) {
			convert = convert.withRsvLeave(this.aggregateResult.getRsvLeaRemNumEachMonthList().get(0));
		}
		if (this.aggregateResult.getAbsenceLeaveRemainList().size() > 0) {
			convert = convert.withAbsenceLeave(this.aggregateResult.getAbsenceLeaveRemainList().get(0));
		}
		if (this.aggregateResult.getMonthlyDayoffRemainList().size() > 0) {
			convert = convert.withDayOff(this.aggregateResult.getMonthlyDayoffRemainList().get(0));
		}
		if (this.aggregateResult.getSpecialLeaveRemainList().size() > 0) {
			convert = convert.withSpecialLeave(this.aggregateResult.getSpecialLeaveRemainList());
		}

		// 月別実績の編集状態を取得
		for (val editState : this.editStates) {

			// 勤怠項目IDから項目を判断
			val itemValueOpt = oldItemConvert.convert(editState.getAttendanceItemId());
			if (!itemValueOpt.isPresent())
				continue;
			val itemValue = itemValueOpt.get();
			if (itemValue.value() == null)
				continue;

			// 該当する勤怠項目IDの値を計算前に戻す
			convert.merge(itemValue);
			this.isRetouch = true;
		}

		// いずれかの手修正値を戻した時、戻した後の勤怠時間を返す
		if (this.isRetouch) {
			val convertedOpt = convert.toAttendanceTime();
			if (convertedOpt.isPresent()) {
				val retouchedTime = convertedOpt.get();
				retouchedTime.getMonthlyCalculation().copySettings(attendanceTime.getMonthlyCalculation());

				val convertedAnnLeaveOpt = convert.toAnnLeave();
				if (convertedAnnLeaveOpt.isPresent()) {
					this.aggregateResult.getAnnLeaRemNumEachMonthList().clear();
					this.aggregateResult.getAnnLeaRemNumEachMonthList().add(convertedAnnLeaveOpt.get());
				}
				val convertedRsvLeaveOpt = convert.toRsvLeave();
				if (convertedRsvLeaveOpt.isPresent()) {
					this.aggregateResult.getRsvLeaRemNumEachMonthList().clear();
					this.aggregateResult.getRsvLeaRemNumEachMonthList().add(convertedRsvLeaveOpt.get());
				}
				val convertedAbsLeaveOpt = convert.toAbsenceLeave();
				if (convertedAbsLeaveOpt.isPresent()) {
					this.aggregateResult.getAbsenceLeaveRemainList().clear();
					this.aggregateResult.getAbsenceLeaveRemainList().add(convertedAbsLeaveOpt.get());
				}
				val convertedDayOffOpt = convert.toDayOff();
				if (convertedDayOffOpt.isPresent()) {
					this.aggregateResult.getMonthlyDayoffRemainList().clear();
					this.aggregateResult.getMonthlyDayoffRemainList().add(convertedDayOffOpt.get());
				}
				val convertedSpcLeaveList = convert.toSpecialHoliday();
				if (convertedSpcLeaveList.size() > 0) {
					this.aggregateResult.getSpecialLeaveRemainList().clear();
					this.aggregateResult.getSpecialLeaveRemainList().addAll(convertedSpcLeaveList);
				}
				return retouchedTime;
			}
		}
		return attendanceTime;
	}

	/**
	 * 手修正を戻してから計算必要な項目を再度計算
	 *
	 * @param attendanceTime 月別実績の勤怠時間
	 * @return 月別実績の勤怠時間
	 */
	private AttendanceTimeOfMonthly recalcAttendanceTime(AttendanceTimeOfMonthly attendanceTime) {

		val monthlyCalculation = attendanceTime.getMonthlyCalculation();

		// 残業合計時間を集計する
		monthlyCalculation.getAggregateTime().getOverTime().recalcTotal();

		// 休出合計時間を集計する
		monthlyCalculation.getAggregateTime().getHolidayWorkTime().recalcTotal();

		// 総労働時間と36協定時間の再計算
		monthlyCalculation.recalcTotal();

		return attendanceTime;
	}

	/**
	 * 手修正された項目を元に戻す （任意項目用）
	 *
	 * @param monthlyOldDatas 集計前の月別実績データ
	 */
	private void undoRetouchValuesForAnyItems(RequireM10 require, MonthlyOldDatas monthlyOldDatas) {

		this.isRetouch = false;

		// 既存データを確認する
		val oldDataList = monthlyOldDatas.getAnyItemList();
		if (oldDataList.size() == 0)
			return;
		val oldConverter = require.createMonthlyConverter();
		val oldItemConvert = oldConverter.withAnyItem(oldDataList);

		// 計算後データを確認
		val monthlyConverter = require.createMonthlyConverter();
		MonthlyRecordToAttendanceItemConverter convert = monthlyConverter
				.withAttendanceTime(this.aggregateResult.getAttendanceTime().get());
		convert = convert.withAnyItem(this.aggregateResult.getAnyItemList());

		// 月別実績の編集状態を取得
		for (val editState : this.editStates) {

			// 勤怠項目IDから項目を判断
			val itemValueOpt = oldItemConvert.convert(editState.getAttendanceItemId());
			if (!itemValueOpt.isPresent())
				continue;
			val itemValue = itemValueOpt.get();
			if (itemValue.value() == null)
				continue;

			// 該当する勤怠項目IDの値を計算前に戻す
			convert.merge(itemValue);
			this.isRetouch = true;
		}

		// いずれかの手修正値を戻した時、戻した後の任意項目を返す
		if (this.isRetouch) {
			val convertedList = convert.toAnyItems();
			this.aggregateResult.setAnyItemList(convertedList);
		}
	}

	/**
	 * 残数処理
	 *
	 * @param period
	 *            期間
	 * @param interimRemainMngMode
	 *            暫定残数データ管理モード
	 * @param isCalcAttendanceRate
	 *            出勤率計算フラグ
	 */
	private void remainingProcess(
			RequireM8 require, CacheCarrier cacheCarrier, DatePeriod period,
			String companyId, String employeeId, YearMonth yearMonth, ClosureId closureId,   ClosureDate closureDate,
			MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalculatingDailys,
			InterimRemainMngMode interimRemainMngMode, boolean isCalcAttendanceRate) {

		// 年休、積休
		// 振休
		// 代休
		// 特別休暇
		val output = require.aggregation(cacheCarrier, period, companyId, employeeId, yearMonth, closureId, closureDate,
				companySets, employeeSets, monthlyCalculatingDailys, interimRemainMngMode, isCalcAttendanceRate);

		// 年休、積休
		this.aggregateResult.getAnnLeaRemNumEachMonthList().addAll(output.getAnnLeaRemNumEachMonthList());
		this.aggregateResult.getRsvLeaRemNumEachMonthList().addAll(output.getRsvLeaRemNumEachMonthList());
		// 振休
		this.aggregateResult.getAbsenceLeaveRemainList().addAll(output.getAbsenceLeaveRemainList());
		// 代休
		this.aggregateResult.getMonthlyDayoffRemainList().addAll(output.getMonthlyDayoffRemainList());
		// 特別休暇
		this.aggregateResult.getSpecialLeaveRemainList().addAll(output.getSpecialLeaveRemainList());
		// 子の看護
		this.aggregateResult.getChildHdRemainList().addAll(output.getChildHdRemainList());
		// 介護
		this.aggregateResult.getCareHdRemainList().addAll(output.getCareHdRemainList());
		//公休
		this.aggregateResult.getPublicRemainList().addAll(output.getPublicRemainList());

		// エラー一覧
		this.aggregateResult.getPerErrors().addAll(output.getPerErrors());
	}

	/**
	 * Workを考慮した月次処理用の暫定残数管理データを作成する
	 *
	 * @param period
	 *            期間
	 */
	public List<DailyInterimRemainMngData> createDailyInterimRemainMngs(RequireM7 require, CacheCarrier cacheCarrier, DatePeriod period) {

		return require.createDailyInterimRemainMngs(cacheCarrier, this.companyId,this.employeeId,period,
													this.companySets,this.monthlyCalculatingDailys);

	}

	/**
	 * 所属情報の作成
	 *
	 * @param datePeriod 期間
	 * @return 月別実績の所属情報
	 */
	private AffiliationInfoOfMonthly createAffiliationInfo(DatePeriod datePeriod, 
			List<WorkingConditionItemWithPeriod> workConditions) {

		List<String> employeeIds = new ArrayList<>();
		employeeIds.add(this.employeeId);

		// 月初の所属情報を取得
		boolean isExistStartWorkInfo = false;
		val workInfo = this.monthlyCalculatingDailys.getWorkInfoOfDailyMap().entrySet().stream().filter(
				x -> x.getKey().afterOrEquals(datePeriod.start()) && x.getKey().beforeOrEquals(datePeriod.end()))
				.collect(Collectors.toList()).stream().sorted((x, y) -> x.getKey().compareTo(y.getKey())).findFirst();

		if (workInfo.isPresent()) {
			isExistStartWorkInfo = true;
		}
		val workInfoOfDailyList = monthlyCalculatingDailys.getAffiInfoOfDailyMap();
		if (workInfoOfDailyList.isEmpty()) {
			if (isExistStartWorkInfo) {
				val errorInfo = new MonthlyAggregationErrorInfo("003",
						new ErrMessageContent(TextResource.localize("Msg_1157")));
				this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}
			return null;
		}
		val firstInfoOfDaily = workInfoOfDailyList.entrySet().stream().filter(
				x -> x.getKey().afterOrEquals(datePeriod.start()) && x.getKey().beforeOrEquals(datePeriod.end()))
				.collect(Collectors.toList()).stream().sorted((x, y) -> x.getKey().compareTo(y.getKey())).findFirst().map(x -> x.getValue()).orElse(null);
		//val firstInfoOfDaily = workInfoOfDailyList.get(datePeriod.start());
		if (firstInfoOfDaily == null) {
			if (isExistStartWorkInfo) {
				val errorInfo = new MonthlyAggregationErrorInfo("003",
						new ErrMessageContent(TextResource.localize("Msg_1157")));
				this.errorInfos.putIfAbsent(errorInfo.getResourceId(), errorInfo);
			}
			return null;
		}
		
		/** 最終労働条件を取得する */
		val latsWorkCondition = workConditions.stream().filter(c -> c.getDatePeriod().contains(datePeriod.end()))
				.findFirst().map(c -> c.getWorkingConditionItem()).get();
		
		// 月初の情報を作成
		val firstInfo = AggregateAffiliationInfo.of(
				firstInfoOfDaily.getEmploymentCode(),
				new WorkplaceId(firstInfoOfDaily.getWplID()),
				new JobTitleId(firstInfoOfDaily.getJobTitleID()),
				firstInfoOfDaily.getClsCode(),
				firstInfoOfDaily.getBusinessTypeCode());

		// 月末がシステム日付以降の場合、月初の情報を月末の情報とする
		if (datePeriod.end().after(GeneralDate.today())) {

			// 月別実績の所属情報を返す
			return AffiliationInfoOfMonthly.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate,
					firstInfo, firstInfo, latsWorkCondition.getContractTime());
		}

		// 月末の所属情報を取得
		val lastInfoOfDaily = workInfoOfDailyList.get(datePeriod.end());
		if (lastInfoOfDaily == null) {

			// 月別実績の所属情報を返す （エラーにせず、月末に月初の情報を入れる）
			return AffiliationInfoOfMonthly.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate,
					firstInfo, firstInfo, latsWorkCondition.getContractTime());
		}

		// 月末の情報を作成
		val lastInfo = AggregateAffiliationInfo.of(
				lastInfoOfDaily.getEmploymentCode(),
				new WorkplaceId(lastInfoOfDaily.getWplID()),
				new JobTitleId(lastInfoOfDaily.getJobTitleID()),
				lastInfoOfDaily.getClsCode(),
				lastInfoOfDaily.getBusinessTypeCode());

		// 月別実績の所属情報を返す
		return AffiliationInfoOfMonthly.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate, firstInfo,
				lastInfo, latsWorkCondition.getContractTime());
	}

	public static interface RequireM13 extends AttendanceTimeOfMonthly.RequireM1, TotalCountByPeriod.RequireM1,
		MonthlyCalculation.RequireM4, VerticalTotalOfMonthly.RequireM1, ExcessOutsideWorkMng.RequireM5, RoundingSetOfMonthly.Require {

	}

	public static interface RequireM2 {

//		List<WorkTypeOfDailyPerformance> dailyWorkTypes(List<String> employeeId, DatePeriod ymd);

//		Optional<WorkTypeOfDailyPerformance> dailyWorkType(String employeeId, GeneralDate ymd);

		Optional<AffiliationInforOfDailyAttd> dailyAffiliationInfor(String employeeId, GeneralDate ymd);

		Map<GeneralDate, AffiliationInforOfDailyAttd> dailyAffiliationInfors(List<String> employeeId, DatePeriod ymd);

	}

	public static interface RequireM11 {

		AttendanceDaysMonth monthAttendanceDays(CacheCarrier cacheCarrier, DatePeriod period, Map<String, WorkType> workTypeMap);
	}

	public static interface RequireM12 extends AnyItemAggrResult.RequireM1 {

	}

	public static interface RequireM10 {

		MonthlyRecordToAttendanceItemConverter createMonthlyConverter();
	}

	public static interface RequireM8 extends RequireM7, RequireM6, RequireM5, RequireM4, RequireM3, RequireM16{

//		EmployeeImport employee(CacheCarrier cacheCarrier, String empId);
//
//		List<Closure> closure(String companyId);

		AggregateMonthlyRecordValue aggregation(CacheCarrier cacheCarrier, DatePeriod period,
				String companyId, String employeeId, YearMonth yearMonth, ClosureId closureId,   ClosureDate closureDate,
				MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
				MonthlyCalculatingDailys monthlyCalculatingDailys,
				InterimRemainMngMode interimRemainMngMode, boolean isCalcAttendanceRate);

		/** 特別休暇基本情報 */
		Optional<SpecialLeaveBasicInfo> specialLeaveBasicInfo(String sid, int spLeaveCD, UnitAtr use);

//		/** 所属会社履歴 */
//		List<AffCompanyHistImport> listAffCompanyHistImport(List listAppId, DatePeriod period);

		/** 締め状態管理 */
		Optional<ClosureStatusManagement> latestClosureStatusManagement(String employeeId);
	}

	public static interface RequireM7 {

		List<DailyInterimRemainMngData> createDailyInterimRemainMngs(CacheCarrier cacheCarrier, String companyId, String employeeId,
				DatePeriod period, MonAggrCompanySettings comSetting, MonthlyCalculatingDailys dailys);
	}

	public static interface RequireM6 extends GetDaysForCalcAttdRate.RequireM2
		/* ,GetAnnAndRsvRemNumWithinPeriod.RequireM2 */ {
	}

	public static interface RequireM16 extends GetRemainingNumberPublicHolidayService.RequireM1{
		
	}

	public static interface RequireM5 extends NumberCompensatoryLeavePeriodQuery.Require{
	}

	public static interface RequireM4 extends NumberRemainVacationLeaveRangeQuery.Require {
	}

//	public static interface RequireM3 extends SpecialLeaveManagementService.RequireM5 {
	public static interface RequireM3 {
		List<SpecialHoliday> specialHoliday(String companyId);
	}

	public static interface RequireM14 {

		Optional<WorkingCondition> workingCondition(String historyId);
	}

	public static interface RequireM15 extends MonthlyCalculatingDailys.RequireM4, RequireM13,
		MonthlyOldDatas.RequireM1, RequireM14, RequireM2, RequireM8, RequireM10,
		MonthlyCalculation.RequireM2, AttendanceTimeOfMonthly.RequireM2, RequireM11, RequireM12 {

		List<WorkingConditionItemWithPeriod> workingCondition(String employeeId, DatePeriod datePeriod);

		List<EditStateOfMonthlyPerformance> monthEditStates(String employeeId, YearMonth yearMonth, ClosureId closureId,
				ClosureDate closureDate);

//		ManagedExecutorService getExecutorService();
	}

	private AttendanceTimeOfMonthly.RequireM2 createAttendanceTimeOfMonthlyRequire(
			RequireM15 require, Optional<List<IntegrationOfDaily>> dailyWorksOpt) {

		return new AttendanceTimeOfMonthly.RequireM2() {

			private Optional<IntegrationOfDaily> findDaily(String empId, GeneralDate ymd) {

				return dailyWorksOpt.flatMap(dws -> {
					return dws.stream().filter(dw -> empId.equals(dw.getEmployeeId())
							&& ymd.equals(dw.getYmd())).findFirst();
				});
			}

			@Override
			public boolean isUseWorkLayer(String companyId) {
				return require.isUseWorkLayer(companyId);
			}

			@Override
			public List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailyAttendance(String empId, GeneralDate ymd) {
				val daily = findDaily(empId, ymd);

				return daily.map(dw -> dw.getOuenTimeSheet())
						.orElseGet(() -> require.ouenWorkTimeSheetOfDailyAttendance(empId, ymd));
			}

			@Override
			public List<OuenWorkTimeOfDailyAttendance> ouenWorkTimeOfDailyAttendance(String empId, GeneralDate ymd) {
				val daily = findDaily(empId, ymd);

				return daily.map(dw -> dw.getOuenTime())
						.orElseGet(() -> require.ouenWorkTimeOfDailyAttendance(empId, ymd));
			}

			@Override
			public Optional<OuenAggregateFrameSetOfMonthly> ouenAggregateFrameSetOfMonthly(String companyId) {
				return require.ouenAggregateFrameSetOfMonthly(companyId);
			}
		};
	}
}