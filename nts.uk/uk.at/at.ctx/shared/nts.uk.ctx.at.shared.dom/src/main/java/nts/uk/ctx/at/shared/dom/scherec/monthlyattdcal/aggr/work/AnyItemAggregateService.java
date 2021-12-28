package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyAmountMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimeMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimesMonth;
import nts.uk.ctx.at.shared.dom.common.days.AttendanceDaysMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.anyitem.AnyItemAggrResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AggregateAnyItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.TermsOfUseForOptItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalcResultOfAnyItem;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**　任意項目を集計　*/
public class AnyItemAggregateService {

	/** ○任意項目を集計 */
	public static List<AnyItemOfMonthly> aggregate(Require require, CacheCarrier cacheCarrier, String cid, 
			String sid, YearMonth ym, ClosureId closureId, ClosureDate closureDate,
			DatePeriod period, MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalcDailys, MonthlyOldDatas monthlyOldDatas,
			List<EditStateOfMonthlyPerformance> editStates, List<AttendanceTimeOfWeekly> attendanceWeeks,
			Optional<AttendanceTimeOfMonthly> attendanceTime) {
		
		Map<Integer, Map<Integer, AnyItemAggrResult>> anyItemCustomizeValue = new HashMap<>();
		/** 大塚モードを確認する */
		if (AppContexts.optionLicense().customize().ootsuka()) {
			// 大塚カスタマイズの集計
			anyItemCustomizeValue = aggregateCustomizeForOtsuka(require, cacheCarrier, ym, closureId, companySets);
		} else {
			// 任意項目カスタマイズ値 ※ 最初のInteger=0（月結果）、1～（各週結果（週No））
			anyItemCustomizeValue.put(0, new HashMap<>()); // 月結果
		}

		// 月別実績の任意項目を集計
		val anyItems = aggregateAnyItem(require, period, sid, ym, closureId, closureDate, anyItemCustomizeValue, 
								companySets, employeeSets, attendanceWeeks, monthlyCalcDailys, attendanceTime,
								monthlyOldDatas.getAnyItemList());

		// 手修正された項目を元に戻す （任意項目用）
		return undoRetouchValuesForAnyItems(require, sid, ym, closureId, closureDate,
											anyItems, monthlyOldDatas, editStates);
	}
	
	/**
	 * 月別実績の任意項目を集計
	 *
	 * @param monthPeriod 月の期間
	 * @param anyItemCustomizeValue 任意項目カスタマイズ値
	 */
	private static List<AnyItemOfMonthly> aggregateAnyItem(RequireM3 require, DatePeriod monthPeriod,
			String sid, YearMonth ym, ClosureId closureId, ClosureDate closureDate,
			Map<Integer, Map<Integer, AnyItemAggrResult>> anyItemCustomizeValue,
			MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			List<AttendanceTimeOfWeekly> attendanceWeeks, MonthlyCalculatingDailys monthlyCalcDailys,
			Optional<AttendanceTimeOfMonthly> attendanceTime, List<AnyItemOfMonthly> oldItemLists) {
		
		List<AnyItemOfMonthly> result = new ArrayList<>();
		
		// 週単位の期間を取得
		for(val attendanceTimeWeek : attendanceWeeks) {
			
			// 週ごとの集計
			val weekResults = aggregateAnyItemAnyPeriod(attendanceTimeWeek.getPeriod(), 
					monthlyCalcDailys, companySets, employeeSets);
					
			for (val weekResult : weekResults.values()) {
				attendanceTimeWeek.getAnyItem().getAnyItemValues().put(weekResult.getOptionalItemNo(),
						AggregateAnyItem.of(weekResult.getOptionalItemNo(), weekResult.getAnyTime(),
								weekResult.getAnyTimes(), weekResult.getAnyAmount()));
			}
		}

		// 月ごとの集計
		val monthResults = aggregateAnyItemMonth(require, sid, ym, closureId, closureDate, monthPeriod, 
				anyItemCustomizeValue.get(0), monthlyCalcDailys, companySets, employeeSets, attendanceTime,
				oldItemLists);
		
		for (val monthResult : monthResults.values()) {
			
			result.removeIf(ao -> ao.getEmployeeId().equals(sid) && ao.getClosureId().equals(closureId)
					&& ao.getClosureDate().equals(closureDate) && ao.getYearMonth().equals(ym)
					&& ao.getAnyItemId() == monthResult.getOptionalItemNo());
			
			result.add(AnyItemOfMonthly.of(sid, ym, closureId, closureDate, monthResult));
		}
		
		return result;
	}

	private static AnyItemAggrResult monthlyCalc(RequireM5 require, String sid, YearMonth ym,
			ClosureId closureId, ClosureDate closureDate, Map<Integer, AnyItemAggrResult> results, 
			OptionalItem optionalItem, Optional<AttendanceTimeOfMonthly> attendanceTime,
			MonAggrCompanySettings companySets) {

		List<AnyItemOfMonthly> anyItems = results.entrySet().stream()
				.map(c -> AnyItemOfMonthly.of(sid, ym, closureId, closureDate, c.getValue()))
				.collect(Collectors.toList());
				
		int optionalItemNo = optionalItem.getOptionalItemNo().v();

		// 初期化
		AnyItemAggrResult result = AnyItemAggrResult.of(optionalItemNo, optionalItem);

		// 「実績区分」を判断
		if (attendanceTime.isPresent()) {

			// 月別実績 計算処理
			result = AnyItemAggrResult.calcFromMonthly(require, optionalItemNo, 
					optionalItem, attendanceTime.get(), anyItems, companySets, PerformanceAtr.MONTHLY_PERFORMANCE);
		}

		return result;
	}
	
	/** 集計処理 (週、期間別用)*/
	public static Map<Integer, AnyItemAggrResult> aggregateAnyItemAnyPeriod(DatePeriod period,
			MonthlyCalculatingDailys monthlyCalc, MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets) {

		Map<Integer, AnyItemAggrResult> results = new HashMap<>();

		// 任意項目ごとに集計する
		Map<Integer, AggregateAnyItem> anyItemTotals = createAggregateAnyItem(period, monthlyCalc);

		// 任意項目を取得
		for (val optionalItem : companySets.getOptionalItemMap().values()) {
			Integer optionalItemNo = optionalItem.getOptionalItemNo().v();

			// 利用条件の判定
			Optional<EmpCondition> empCondition = Optional.empty();
			if (companySets.getEmpConditionMap().containsKey(optionalItemNo)) {
				empCondition = Optional.of(companySets.getEmpConditionMap().get(optionalItemNo));
			}
			val bsEmploymentHistOpt = employeeSets.getEmployment(period.end());
			
			if (optionalItem.checkTermsOfUseMonth(empCondition, bsEmploymentHistOpt) == TermsOfUseForOptItem.DAILY_VTOTAL) { // 日別縦計する
				
				// 日別実績 縦計処理
				AnyItemAggrResult result = AnyItemAggrResult.calcFromDailys(optionalItemNo, optionalItem, anyItemTotals);

				/** ○縦計の結果をセット */
				results.put(optionalItemNo, result);
			} else {

				/** ○縦計の結果をセット */
				results.put(optionalItemNo, AnyItemAggrResult.of(optionalItemNo, 
						Optional.of(new AnyTimeMonth(0)), Optional.of(new AnyTimesMonth(0d)), Optional.of(new AnyAmountMonth(0))));
			}
		}

		return results;
	}

	private static Map<Integer, AggregateAnyItem> createAggregateAnyItem(DatePeriod period, MonthlyCalculatingDailys monthlyCalc) {
		
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
		
		return anyItemTotals;
	}

	public static Map<Integer, AnyItemAggrResult> aggregateAnyItemMonth(RequireM5 require, String sid, YearMonth ym,
			ClosureId closureId, ClosureDate closureDate, DatePeriod period,
			Map<Integer, AnyItemAggrResult> anyItemCustomizeValue,
			MonthlyCalculatingDailys monthlyCalc, MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets, Optional<AttendanceTimeOfMonthly> attendanceTime, 
			List<AnyItemOfMonthly> oldItemLists) {

		Map<Integer, AnyItemAggrResult> results = oldItemLists.stream()
				.map(c -> AnyItemAggrResult.of(c.getAnyItemId(), c.getTime(), c.getTimes(), c.getAmount()))
				.collect(Collectors.toMap(c -> c.getOptionalItemNo(), c -> c));

		Map<Integer, AggregateAnyItem> anyItemTotals = createAggregateAnyItem(period, monthlyCalc);

		// 任意項目を取得
		for (val optionalItem : companySets.getOptionalItemMap().values()) {
			Integer optionalItemNo = optionalItem.getOptionalItemNo().v();

			/** 月間集計　＆＆　大塚モードを確認する */
			if (AppContexts.optionLicense().customize().ootsuka()) {
				// 大塚カスタマイズ （月別実績の任意項目←任意項目カスタマイズ値）
				if (anyItemCustomizeValue != null) {
					if (anyItemCustomizeValue.containsKey(optionalItemNo)) {
						results.put(optionalItemNo, anyItemCustomizeValue.get(optionalItemNo));
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
			AnyItemAggrResult result;
			switch(optionalItem.checkTermsOfUseMonth(empCondition, bsEmploymentHistOpt)){
			case USE: // 利用する
				
				result = monthlyCalc(require, sid, ym, closureId, closureDate, results, 
						optionalItem, attendanceTime, companySets);
				
				results.put(optionalItemNo, result);
				break;
			case DAILY_VTOTAL: // 日別縦計する
				
				// 日別実績 縦計処理
				result = AnyItemAggrResult.calcFromDailys(optionalItemNo, optionalItem, anyItemTotals);

				/** 上限下限チェック */
				result = limitCheck(optionalItem, optionalItemNo, result);

				results.put(optionalItemNo, result);
				break;
			case NOT_USE: // 利用しない
			default:
				/** ○縦計の結果をセット */
				result = AnyItemAggrResult.of(optionalItemNo, 
						Optional.of(new AnyTimeMonth(0)), Optional.of(new AnyTimesMonth(0d)), Optional.of(new AnyAmountMonth(0)));
				
				results.putIfAbsent(optionalItemNo, result);
			}
		}

		return results;
	}

	/** 上限下限チェック */
	private static AnyItemAggrResult limitCheck(OptionalItem optionalItem,
			Integer optionalItemNo, AnyItemAggrResult result) {
		val checkedResult = optionalItem.getInputControlSetting().getCalcResultRange().checkRange(new CalcResultOfAnyItem(optionalItem.getOptionalItemNo(),
																						result.getAnyTimes().map(c -> c.v()),
																						result.getAnyTime().map(c -> BigDecimal.valueOf(c.v())),
																						result.getAnyAmount().map(c -> BigDecimal.valueOf(c.v()))),
																		optionalItem, PerformanceAtr.MONTHLY_PERFORMANCE);

		return AnyItemAggrResult.of(optionalItemNo,
										checkedResult.getTime().map(c -> new AnyTimeMonth(c.intValue())),
										checkedResult.getCount().map(c -> new AnyTimesMonth(c.doubleValue())),
										checkedResult.getMoney().map(c -> new AnyAmountMonth(c.intValue())));
	}
	
	/**
	 * 大塚カスタマイズ （任意項目集計）
	 */
	private static Map<Integer, Map<Integer, AnyItemAggrResult>> aggregateCustomizeForOtsuka(RequireM1 require,
			CacheCarrier cacheCarrier, YearMonth yearMonth, ClosureId closureId, MonAggrCompanySettings companySets) {

		// 任意項目カスタマイズ値 ※ 最初のInteger=0（月結果）、1～（各週結果（週No））
		Map<Integer, Map<Integer, AnyItemAggrResult>> results = new HashMap<>();
		results.put(0, new HashMap<>()); // 月結果

		// 月ごとの集計
		AnyItemAggrResult monthResult = getPredWorkingDays(require, cacheCarrier, yearMonth, closureId, companySets);
		results.get(0).putIfAbsent(monthResult.getOptionalItemNo(), monthResult);

		// 任意項目カスタマイズ値を返す
		return results;
	}
	
	/**
	 * 手修正された項目を元に戻す （任意項目用）
	 *
	 */
	private static List<AnyItemOfMonthly> undoRetouchValuesForAnyItems(RequireM2 require, 
			String sid, YearMonth ym, ClosureId closureId, ClosureDate closureDate,
			List<AnyItemOfMonthly> anyItems, MonthlyOldDatas monthlyOldDatas,
			List<EditStateOfMonthlyPerformance> editStates) {

		val itemIds = editStates.stream().map(c -> c.getAttendanceItemId()).collect(Collectors.toList()); 
		
		// 既存データを確認する
		if (monthlyOldDatas.getAnyItemList().isEmpty() || itemIds.isEmpty())
			return anyItems ;
		
		val converter = require.createMonthlyConverter();
		converter.withBase(sid, ym, closureId, closureDate);
		converter.withAnyItem(monthlyOldDatas.getAnyItemList());
		val oldValues = converter.convert(itemIds);

		converter.withAnyItem(anyItems);
		converter.merge(oldValues);

		// いずれかの手修正値を戻した時、戻した後の任意項目を返す
		return  converter.toAnyItems();
	}
	
	/**
	 * 計画所定労働日数
	 *
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 任意項目集計結果
	 */
	private static AnyItemAggrResult getPredWorkingDays(RequireM4 require, CacheCarrier cacheCarrier,
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
		double predWorkingDays = require.monthAttendanceDays(cacheCarrier, period, companySets.getAllWorkTypeMap()).v();

		// 任意項目69へ格納
		return AnyItemAggrResult.of(69, Optional.empty(), Optional.of(new AnyTimesMonth(predWorkingDays)), Optional.empty());
	}
	
	public static interface Require extends RequireM1, RequireM2, RequireM3 {
		
	}
	
	public static interface RequireM1 extends RequireM4{
		
	}
	
	public static interface RequireM2 {
		
		MonthlyRecordToAttendanceItemConverter createMonthlyConverter();
	}
	
	public static interface RequireM3 extends RequireM5 {
		
	}
	
	public static interface RequireM5 extends AnyItemAggrResult.RequireM1 {
		
	}
	
	public static interface RequireM4 {
		
		AttendanceDaysMonth monthAttendanceDays(CacheCarrier cacheCarrier, DatePeriod period, Map<String, WorkType> workTypeMap);
	}
}
