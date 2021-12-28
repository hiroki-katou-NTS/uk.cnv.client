package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.LaborCostAndTime;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.LaborCostsTotalizationService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.shr.com.enumcommon.NotUseAtr;
/**
 * 職場計の人件費・時間カテゴリを集計する UTコード
 * @author lan_lt
 *
 */
@RunWith(JMockit.class)
public class CountLaborCostTimeServiceTest {

	@Injectable
	private CountLaborCostTimeService.Require require;

	/**
	 * input:	時間外時間 = しない
	 * 			合計	= しない
	 * output:	empty
	 */
	@Test
	public void getBudget_budget_of_laborCostAndTime_all_not_use(@Injectable TargetOrgIdenInfor targetOrg) {

		DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 30));

		Map<AggregationUnitOfLaborCosts, LaborCostAndTime> targetLabor = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>();
		targetLabor.put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.of(NotUseAtr.NOT_USE)));//時間外時間 = しない
		targetLabor.put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.of(NotUseAtr.NOT_USE)));//合計 = しない

		Map<GeneralDate, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class, "getBudget", require, targetOrg, targetLabor, datePeriod);

		assertThat(result).isEmpty();
	}

	/**
	 * input:	時間外時間 = empty
	 * 			合計	= empty
	 * output:	empty
	 */
	@Test
	public void getBudget_budget_of_laborCostAndTime_all_empty(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable DatePeriod datePeriod
	) {

		Map<AggregationUnitOfLaborCosts, LaborCostAndTime> targetLabor = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>();
		targetLabor.put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.empty())); //時間外時間 = empty
		targetLabor.put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.empty())); //合計 = empty

		Map<GeneralDate, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class, "getBudget", require, targetOrg, targetLabor, datePeriod);

		assertThat(result).isEmpty();
	}

	/**
	 * input:	期間　＝　「2021/01/01 -> 2021/01/03」
	 * output:	この期間は予算リスト　＝　empty
	 */
	@Test
	public void getBudget_get_budget_empty(@Injectable TargetOrgIdenInfor targetOrg) {

		DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 3));
		Map<AggregationUnitOfLaborCosts, LaborCostAndTime> targetLabor = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>();
		targetLabor.put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.of(NotUseAtr.USE)));
		targetLabor.put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.of(NotUseAtr.USE)));

		new Expectations() {
			{
				require.getLaborCostBudgetList((TargetOrgIdenInfor) any, (DatePeriod) any);
			}
		};

		Map<GeneralDate, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class, "getBudget", require, targetOrg, targetLabor, datePeriod);
		assertThat(result).hasSize(3);
		assertThat(result.entrySet())
				.extracting(c -> c.getKey(), c -> c.getValue())
				.containsExactlyInAnyOrder(
							tuple(GeneralDate.ymd(2021, 1, 1), BigDecimal.ZERO)
						,	tuple(GeneralDate.ymd(2021, 1, 2), BigDecimal.ZERO)
						,	tuple(GeneralDate.ymd(2021, 1, 3), BigDecimal.ZERO)
						);
	}

	/**
	 * 対象：	期間	= 	[2021/1/1 -> 2021/1/5]
	 * 		予算リスト　= [2021/1/1 -> 2021/1/2] と　[2021/1/5]のデータだけがあります。
	 * 期待：　[2021/1/1 -> 2021/1/2] と　[2021/1/5]は予算があります。
	 * 		他の日の予算が0です。
	 */
	@Test
	public void getBudgetList(@Injectable TargetOrgIdenInfor targetOrg) {

		val targetLabor = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {
			private static final long serialVersionUID = 1L;
			{
				put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.of(NotUseAtr.USE)));
				put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.of(NotUseAtr.USE)));
			}
		};

		//期間  = [2021/1/1 -> 2021/1/3]
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 5));

		//予算リスト　= [2021/1/1, 2021/1/2, 2021/1/5]のデータだけがあります。
		List<LaborCostBudget> budgets = Arrays.asList(
						Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 1), 10)
					,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 2), 20)
					,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 5), 50));

		new Expectations() {
			{
				require.getLaborCostBudgetList((TargetOrgIdenInfor) any, (DatePeriod) any);
				result = budgets;
			}
		};

		Map<GeneralDate, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class, "getBudget", require, targetOrg, targetLabor, datePeriod);

		assertThat(result).hasSize(5);

		assertThat(result.entrySet())
				.extracting(c -> c.getKey(), c -> c.getValue())
				.contains(
							tuple(GeneralDate.ymd(2021, 1, 1), BigDecimal.valueOf(10))
						,	tuple(GeneralDate.ymd(2021, 1, 2), BigDecimal.valueOf(20))
						,	tuple(GeneralDate.ymd(2021, 1, 3), BigDecimal.ZERO)
						,	tuple(GeneralDate.ymd(2021, 1, 4), BigDecimal.ZERO)
						,	tuple(GeneralDate.ymd(2021, 1, 5), BigDecimal.valueOf(50))
						);
	}

	/**
	 * 対象：		期間	= 	[2021/1/1 -> 2021/1/2], この期間は 全部予算があります。
	 * 期待：		この期間は 全部予算があります。
	 */
	@Test
	public void getBudget_all_day_have_labor_cost_time(@Injectable TargetOrgIdenInfor targetOrg) {

		val targetLabor = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {
			private static final long serialVersionUID = 1L;
			{
				put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.of(NotUseAtr.USE)));
				put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.of(NotUseAtr.USE)));
			}
		};

		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 3));

		List<LaborCostBudget> budgets = Arrays.asList(
					Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 1), 10)
				,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 2), 20)
				,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 3), 30)
				);

		new Expectations() {
			{
				require.getLaborCostBudgetList((TargetOrgIdenInfor) any, (DatePeriod) any);
				result = budgets;
			}
		};

		Map<GeneralDate, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class, "getBudget"
				, require, targetOrg, targetLabor, datePeriod);

		assertThat(result).hasSize(3);

		assertThat(result.entrySet())
				.extracting(c -> c.getKey(), c -> c.getValue())
				.contains(
							tuple(GeneralDate.ymd(2021, 1, 1), BigDecimal.valueOf(10))
						,	tuple(GeneralDate.ymd(2021, 1, 2), BigDecimal.valueOf(20))
						,	tuple(GeneralDate.ymd(2021, 1, 3), BigDecimal.valueOf(30))
						);
	}



	/**
	 * 項目種類ごとに集計する
	 * input:	職場計の人件費項目種類 = 金額
	 * output:	項目種類 = 金額	人件費・時間を集計する#金額を集計する  の回数　= 1
	 * 							人件費・時間を集計する#時間を集計する の回数 = 0
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void countEachItemType_amounts(@Injectable List<AttendanceTimeOfDailyAttendance> dailyAttendance) {

		// 集計結果(金額)
		@SuppressWarnings("serial")
		val amounts = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>() {
			{
				put( AggregationUnitOfLaborCosts.TOTAL,		BigDecimal.valueOf(500) );
				put( AggregationUnitOfLaborCosts.WITHIN,	BigDecimal.valueOf(200) );
				put( AggregationUnitOfLaborCosts.EXTRA,		BigDecimal.valueOf(300) );
			}
		};
		// 集計単位リスト
		val targets = new ArrayList<>( amounts.keySet() );

		new Expectations(LaborCostsTotalizationService.class) {
			{
				//金額
				LaborCostsTotalizationService.totalizeAmounts(targets, (List<AttendanceTimeOfDailyAttendance>)any);
				times = 1;
				result = amounts;

				//時間
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>)any, (List<AttendanceTimeOfDailyAttendance>)any);
				times = 0;
			}
		};


		// 実行
		Map<LaborCostAggregationUnit, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class
					, "countEachItemType", targets, LaborCostItemType.AMOUNT, dailyAttendance
				);


		// 検証：集計単位が、Inputの集計単位リストと一致すること
		assertThat( result.keySet() )
				.extracting( LaborCostAggregationUnit::getUnit )
				.containsExactlyInAnyOrderElementsOf( targets );

		// 検証：項目種類が、Inputの項目種類と一致すること
		assertThat( result.keySet() )
				.extracting( LaborCostAggregationUnit::getItemType )
				.containsOnly(LaborCostItemType.AMOUNT);

		// 検証：Valueが、結果と正しいこと
		result.entrySet().forEach( entry -> {
			assertThat( entry.getValue() ).isEqualTo( amounts.get(entry.getKey().getUnit()) );
		});

	}

	/**
	 * 項目種類ごとに集計する
	 * input:	職場計の人件費項目種類 = 	時間
	 * output:	項目種類 = 時間	人件費・時間を集計する#金額を集計する  の回数　= 0
	 * 							人件費・時間を集計する#時間を集計する の回数 = 1
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void countEachItemType_times(@Injectable List<AttendanceTimeOfDailyAttendance> dailyAttendance) {

		// 集計結果(時間)
		@SuppressWarnings("serial")
		val laborTime = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>() {
			{
				put( AggregationUnitOfLaborCosts.TOTAL,		BigDecimal.valueOf(500) );
				put( AggregationUnitOfLaborCosts.WITHIN,	BigDecimal.valueOf(200) );
				put( AggregationUnitOfLaborCosts.EXTRA,		BigDecimal.valueOf(300) );
			}
		};
		// 集計単位リスト
		val targets = new ArrayList<>( laborTime.keySet() );

		new Expectations(LaborCostsTotalizationService.class) {
			{
				//金額
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>)any, (List<AttendanceTimeOfDailyAttendance>)any);
				times = 0;

				//時間
				LaborCostsTotalizationService.totalizeTimes(targets, (List<AttendanceTimeOfDailyAttendance>)any);
				times = 1;
				result = laborTime;
			}
		};


		// 実行
		Map<LaborCostAggregationUnit, BigDecimal>  result = NtsAssert.Invoke.staticMethod(CountLaborCostTimeService.class
					, "countEachItemType", targets, LaborCostItemType.TIME, dailyAttendance
				);


		// 検証：集計単位が、Inputの集計単位リストと一致すること
		assertThat( result.keySet() )
				.extracting( LaborCostAggregationUnit::getUnit )
				.containsExactlyInAnyOrderElementsOf( targets );

		// 検証：項目種類が、Inputの項目種類と一致すること
		assertThat( result.keySet() )
				.extracting( LaborCostAggregationUnit::getItemType )
				.containsOnly(LaborCostItemType.TIME);

		// 検証：Valueが、結果と正しいこと
		result.entrySet().forEach( entry -> {
			assertThat( entry.getValue() ).isEqualTo( laborTime.get(entry.getKey().getUnit()) );
		});

	}



	/**
	 * 人件費項目を集計する not empty
	 * 人件費項目リスト中に集計対象がある
	 * input:	項目種類　＝　金額
	 * 			[人件費・時間]リスト中に
	 * 			人件費・時間の集計単位	=	EXTRAは集計対象ではない
	 * 			人件費・時間の集計単位	=	「TOTAL」と「WITHIN」　⇒	集計対象
	 * output:	「TOTAL」と「WITHIN」が集計します
	 */
	@Test
	public void countLaborCostAndTime_have_target_count(
			@Injectable AttendanceTimeOfDailyAttendance dailyAtt
		,	@Injectable LaborCostAndTime settingTotal
		,	@Injectable LaborCostAndTime settingWithin
		,	@Injectable LaborCostAndTime settingExtra
	) {

		// 項目種類
		val itemType = LaborCostItemType.AMOUNT;

		// 集計単位
		val targetUnits = Arrays.asList( AggregationUnitOfLaborCosts.TOTAL, AggregationUnitOfLaborCosts.WITHIN );
		// 人件費・時間の集計対象
		@SuppressWarnings("serial")
		val setting = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {{
			put( AggregationUnitOfLaborCosts.TOTAL,		settingTotal );		// 合計
			put( AggregationUnitOfLaborCosts.WITHIN,	settingWithin );	// 就業時間
			put( AggregationUnitOfLaborCosts.EXTRA,		settingExtra );		// 時間外時間
		}};
		new Expectations() {{
			// 合計→対象
			settingTotal.isTargetAggregation(itemType);
			result = targetUnits.contains(AggregationUnitOfLaborCosts.TOTAL);
			// 就業時間→対象
			settingWithin.isTargetAggregation(itemType);
			result = targetUnits.contains(AggregationUnitOfLaborCosts.WITHIN);
			// 時間外時間→対象外
			settingExtra.isTargetAggregation(itemType);
			result = targetUnits.contains(AggregationUnitOfLaborCosts.EXTRA);
		}};

		// 日別勤怠時間リスト
		@SuppressWarnings("serial")
		val dlyAtdByDate = new HashMap<GeneralDate, List<AttendanceTimeOfDailyAttendance>>() {{
			put( GeneralDate.today(), Arrays.asList(dailyAtt) );
		}};

		// Mock: 集計結果
		@SuppressWarnings("serial")
		val totalizeResult = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>() {{
			put( AggregationUnitOfLaborCosts.TOTAL,		BigDecimal.valueOf(600) );
			put( AggregationUnitOfLaborCosts.WITHIN,	BigDecimal.valueOf(480) );
		}};
		new Expectations(LaborCostsTotalizationService.class) {{
			// 検証：メソッド呼び出し時の集計単位リストが正しいこと
			@SuppressWarnings("unchecked") val anyDlyAtds = (List<AttendanceTimeOfDailyAttendance>)any;
			LaborCostsTotalizationService.totalizeAmounts(targetUnits, anyDlyAtds);
			times = dlyAtdByDate.size();
			result = totalizeResult;
		}};


		// 実行
		Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>> result = NtsAssert.Invoke.staticMethod(
					CountLaborCostTimeService.class
					, "countLaborCost"
						, setting, itemType, dlyAtdByDate
				);


		// 検証：人件費・時間の集計対象が存在する→Emptyではない
		assertThat( result ).isNotEmpty();
		// 検証：実行結果の年月日(Key)が日別勤怠時間リストの年月日(Key)と等しい
		assertThat( result.keySet() ).containsExactlyInAnyOrderElementsOf( dlyAtdByDate.keySet() );

		val aggregateResult = result.get(GeneralDate.today());
		{
			// 検証：集計結果の項目種類(Key->項目種類)が指定した項目種類である
			assertThat( aggregateResult.keySet() )
				.extracting( LaborCostAggregationUnit::getItemType )
				.containsOnly( itemType );
			// 検証：集計結果の集計単位(Key->集計単位)と値(Value)が正しい
			assertThat( aggregateResult.entrySet() )
				.extracting(
						e -> e.getKey().getUnit()
					,	e -> e.getValue()
				).containsExactlyInAnyOrder(
						tuple( AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(600) )
					,	tuple( AggregationUnitOfLaborCosts.WITHIN,	BigDecimal.valueOf(480) )
				);
		}

	}

	/**
	 * 人件費項目リストは全部集計対象ではないです。
	 * 計対象がなければOutputがMap.emptyになること
	 */
	@Test
	public void countLaborCostAndTime_all_not_target_count(
				@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2
			,	@Injectable LaborCostAndTime settingTotal
			,	@Injectable LaborCostAndTime settingWithin
			,	@Injectable LaborCostAndTime settingExtra
	) {

		// 項目種類
		val itemType = LaborCostItemType.AMOUNT;

		// 人件費・時間の集計対象
		@SuppressWarnings("serial")
		val setting = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {{
			put( AggregationUnitOfLaborCosts.TOTAL,		settingTotal );		// 合計
			put( AggregationUnitOfLaborCosts.WITHIN,	settingWithin );	// 就業時間
			put( AggregationUnitOfLaborCosts.EXTRA,		settingExtra );		// 時間外時間
		}};
		new Expectations() {{
			// 合計→対象外
			settingTotal.isTargetAggregation(itemType);
			result = false;
			// 就業時間→対象外
			settingWithin.isTargetAggregation(itemType);
			result = false;
			// 時間外時間→対象外
			settingExtra.isTargetAggregation(itemType);
			result = false;
		}};

		// 日別勤怠時間リスト
		@SuppressWarnings("serial")
		val dlyAtdByDate = new HashMap<GeneralDate, List<AttendanceTimeOfDailyAttendance>>() {{
			put( GeneralDate.today(), Arrays.asList( dailyAtt1 ) );
			put( GeneralDate.today().increase(), Arrays.asList( dailyAtt2 ) );
		}};


		// 実行
		Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>> result = NtsAssert.Invoke.staticMethod(
					CountLaborCostTimeService.class
					, "countLaborCost"
						, setting, itemType, dlyAtdByDate
				);


		// 検証：人件費・時間の集計対象が存在しない→Empty
		assertThat( result ).isEmpty();

	}



	/**
	 * 1.Input.日別勤怠リストのうち、Input.期間に含まれないものは集計されないこと
	 * 期間 = 2021/01/01 -> 2021/01/03
	 * 日別勤怠= 2021/01/01 -> 2021/01/06
	 * output:	2021/01/01 -> 2021/01/03: 集計されます
	 * 			2021/01/04 -> 2021/01/06: 集計されないです。
	 */
	@Test
	public void aggregate_not_aggregate(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2
			,	@Injectable LaborCostAndTime laborCost) {

		//期間 = 2021/01/01 -> 2021/01/03
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 3));

		//日別: 2021/01/01 -> 2021/01/06
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), dailyAtt1)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 3), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 4), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 5), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 6), dailyAtt2)
		);

		//人件費・時間の集計対象
		val targetLaborCost = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {
			private static final long serialVersionUID = 1L;
			{
				put(AggregationUnitOfLaborCosts.TOTAL, laborCost);
				put(AggregationUnitOfLaborCosts.EXTRA, laborCost);
				put(AggregationUnitOfLaborCosts.WITHIN, laborCost );
			}
		};

		val result = CountLaborCostTimeService.aggregate(require, targetOrg, datePeriod, targetLaborCost, dailyWorks);

		assertThat(result.keySet()).containsAll(datePeriod.datesBetween());

	}

	/**
	 * 2．Input.期間に日別勤怠がない場合は、emptyで集計されること
	 * 期間 = 2021/01/01 -> 2021/01/06
	 * 日別勤怠= 2021/01/01 -> 2021/01/03
	 * output:	2021/01/01 -> 2021/01/03: 集計されます
	 * 			2021/01/04 -> 2021/01/06: emptyで集計されます。
	 */
	@Test
	public void aggregate_have_not_daily_in_datePeriod(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2
			,	@Injectable LaborCostAndTime laborCost) {

		//期間 = 2021/01/01 -> 2021/01/06
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 6));

		//日別勤怠: 2021/01/02 -> 2021/01/04
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), dailyAtt1)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 3), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 4), dailyAtt2)
		);

		//人件費・時間の集計対象
		val targetLaborCost = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>() {
			private static final long serialVersionUID = 1L;
			{
				put(AggregationUnitOfLaborCosts.TOTAL, laborCost);
				put(AggregationUnitOfLaborCosts.EXTRA, laborCost);
				put(AggregationUnitOfLaborCosts.WITHIN, laborCost );
			}
		};

		val result = CountLaborCostTimeService.aggregate(require, targetOrg, datePeriod, targetLaborCost, dailyWorks);

		//keyの年月日をチェックします
		assertThat(result.keySet()).containsAll(datePeriod.datesBetween());
		//2021/01/01 emptyで集計されます
		assertThat(result.get(GeneralDate.ymd(2021, 1, 1))).isEmpty();
		//2021/01/05 emptyで集計されます
		assertThat(result.get(GeneralDate.ymd(2021, 1, 5))).isEmpty();
		//2021/01/06 emptyで集計されます
		assertThat(result.get(GeneralDate.ymd(2021, 1, 6))).isEmpty();
	}

	/**
	 * 予算がない、人件費項目だけ。
	 * 期間 = 2021/01/01 -> 2021/01/02
	 * 日別勤怠= 2021/01/01 -> 2021/01/2
	 * 期待：	2021/01/01 人件費項目がある、予算がない
	 * 		2021/01/02 人件費項目がある、予算がない
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void aggregate_budget_empty_have_labor_count_time(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2
	) {

		//期間 = 2021/01/01 -> 2021/01/02
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 2));

		//日別: 2021/01/01 -> 2021/01/02
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), dailyAtt1)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), dailyAtt2)
			);
		@SuppressWarnings("serial")
		Map<GeneralDate, List<AttendanceTimeOfDailyAttendance>> dailyWorksByDate = new HashMap<GeneralDate, List<AttendanceTimeOfDailyAttendance>>(){
			{
				put(GeneralDate.ymd(2021, 1, 1), Arrays.asList(dailyAtt1));
				put(GeneralDate.ymd(2021, 1, 2), Arrays.asList(dailyAtt2));
			}
		};

		//人件費・時間
		@SuppressWarnings("serial")
		val targetLaborCost = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>(){{
			put(AggregationUnitOfLaborCosts.EXTRA, Helper.createLaborCostAndTime(LaborCostItemType.AMOUNT, NotUseAtr.USE));	//集計対象
			put(AggregationUnitOfLaborCosts.TOTAL, Helper.createLaborCostAndTime(LaborCostItemType.TIME, NotUseAtr.USE));	//集計対象
		}};

		//金額集計の2021/01/01
		@SuppressWarnings("serial")
		val laborCost_20210101 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.EXTRA, BigDecimal.valueOf(2000));
		}};

		//時間集計の2021/01/01
		@SuppressWarnings("serial")
		val laborTime_20210101 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.TOTAL, BigDecimal.valueOf(3000));
		}};

		//金額集計の2021/01/02
		@SuppressWarnings("serial")
		val laborCost_20210102 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>()
		{{
			put(AggregationUnitOfLaborCosts.EXTRA, BigDecimal.valueOf(4000));
		}};

		//時間集計の2021/01/02
		@SuppressWarnings("serial")
		val laborTime_20210102= new HashMap<AggregationUnitOfLaborCosts, BigDecimal>()
		{{
			put(AggregationUnitOfLaborCosts.TOTAL, BigDecimal.valueOf(5000));
		}};

		new Expectations(LaborCostsTotalizationService.class) {
			{
				//金額の2021/01/01
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 1)));
				result = laborCost_20210101;

				//時間の2021/01/02
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 1)));
				result = laborTime_20210101;

				//金額の2021/01/02
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 2)));
				result = laborCost_20210102;

				//時間の2021/01/02
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 2)));
				result = laborTime_20210102;
			}
		};
		val result = CountLaborCostTimeService.aggregate(require, targetOrg, datePeriod, targetLaborCost, dailyWorks);

		//・Output.Key が Input.期間 の日付をすべて含んでいること
		assertThat(result.keySet()).containsAll(datePeriod.datesBetween());

		//2021/01/01
		{
			val total = result.get(GeneralDate.ymd(2021, 1, 1));
			assertThat(total.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.AMOUNT,	AggregationUnitOfLaborCosts.EXTRA,	BigDecimal.valueOf(2000))
							,	tuple(LaborCostItemType.TIME,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(3000))
							);

			//・Output[年月日].keySet()->項目種類 に 『予算』が存在しないこと
			assertThat(	total.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.doesNotContain(LaborCostItemType.BUDGET);
		}

		//2021/01/02
		{
			val total = result.get(GeneralDate.ymd(2021, 1, 2));
			assertThat(total.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.AMOUNT,	AggregationUnitOfLaborCosts.EXTRA,	BigDecimal.valueOf(4000))
							,	tuple(LaborCostItemType.TIME,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(5000))
							);
			//・Output[年月日].keySet()->項目種類 に 『予算』が存在しないこと
			assertThat(	total.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.doesNotContain(LaborCostItemType.BUDGET);
		}
	}

	/**
	 * 予算だけがあります
	 * 期間 = 2021/01/01 -> 2021/01/02
	 * 日別勤怠= 2021/01/01 -> 2021/01/02
	 * 期待：	2021/01/01 人件費項目がない、予算がある
	 * 		2021/01/02 人件費項目がない、予算がある
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void aggregate_hava_a_budget_and_laborCostTime_empty(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2) {

		//期間 = 2021/01/01 -> 2021/01/03
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 2));

		//日別: 2021/01/01 -> 2021/01/06
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), dailyAtt1)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), dailyAtt2));

		//人件費・時間:利用する
		Map<AggregationUnitOfLaborCosts, LaborCostAndTime> targetLaborCost = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>();
		targetLaborCost.put(AggregationUnitOfLaborCosts.EXTRA, Helper.createBudget(Optional.of(NotUseAtr.USE)));
		targetLaborCost.put(AggregationUnitOfLaborCosts.TOTAL, Helper.createBudget(Optional.of(NotUseAtr.USE)));

		/**
		 * 予算金額
		 * 2021/1/1, ( 合計, 予算 ), 予算金額  = 100
		 * 2021/1/2, ( 合計, 予算 ), 予算金額  = 200
		 */
		val budgets = Arrays.asList(
				Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 1), 100)// 年月日, 予算金額
			,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 2), 200)
			);

		new Expectations(LaborCostsTotalizationService.class) {
			{
				//予算
				require.getLaborCostBudgetList((TargetOrgIdenInfor) any, (DatePeriod) any);
				result = budgets;

				//金額
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any,
						(List<AttendanceTimeOfDailyAttendance>) any);

				//時間
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any,
						(List<AttendanceTimeOfDailyAttendance>) any);
			}
		};
		val result = CountLaborCostTimeService.aggregate(require, targetOrg, datePeriod, targetLaborCost, dailyWorks);

		assertThat(result).hasSize(2);

		//・Output.Key が Input.期間 の日付をすべて含んでいること
		assertThat(result.keySet()).containsAll(datePeriod.datesBetween());

		//Output[年月日].keySet()->項目種類 に 『予算』が存在しないこと
		//2021/01/01
		{
			val budget = result.get(GeneralDate.ymd(2021, 1, 1));
			assertThat(budget.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.BUDGET,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(100))
							);
			//・Output[年月日].keySet()->項目種類 に 『金額, 時間』が存在しないこと
			assertThat(	budget.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.doesNotContainAnyElementsOf(
								Arrays.asList(LaborCostItemType.TIME, LaborCostItemType.AMOUNT));
		}

		//2021/01/02
		{
			val budget = result.get(GeneralDate.ymd(2021, 1, 2));
			assertThat(budget.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.BUDGET,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(200))
							);

			//・Output[年月日].keySet()->項目種類 に 『金額, 時間』が存在しないこと
			assertThat(	budget.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.doesNotContainAnyElementsOf(
								Arrays.asList(LaborCostItemType.TIME, LaborCostItemType.AMOUNT));
		}
	}


	/**
	 * 予算だけがあります
	 * 期間 = 2021/01/01 -> 2021/01/03
	 * 日別勤怠= 2021/01/01 -> 2021/01/03
	 * 期待：	2021/01/01 人件費項目がある、予算= 0
	 * 		2021/01/02 人件費項目がない、予算がある
	 * 		2021/01/03 人件費項目と予算がある
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void aggregate_hava_a_budget_and_laborCostTime(
				@Injectable TargetOrgIdenInfor targetOrg
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt1
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt2
			,	@Injectable AttendanceTimeOfDailyAttendance dailyAtt3) {

		//期間 = 2021/01/01 -> 2021/01/03
		val datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 3));

		//日別: 2021/01/01 -> 2021/01/06
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), dailyAtt1)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), dailyAtt2)
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 3), dailyAtt3));

		@SuppressWarnings("serial")
		Map<GeneralDate, List<AttendanceTimeOfDailyAttendance>> dailyWorksByDate = new HashMap<GeneralDate, List<AttendanceTimeOfDailyAttendance>>(){
			{
				put(GeneralDate.ymd(2021, 1, 1), Arrays.asList(dailyAtt1));
				put(GeneralDate.ymd(2021, 1, 2), Arrays.asList(dailyAtt2));
				put(GeneralDate.ymd(2021, 1, 3), Arrays.asList(dailyAtt3));

			}
		};

		//人件費・時間
		@SuppressWarnings("serial")
		val targetLaborCost = new HashMap<AggregationUnitOfLaborCosts, LaborCostAndTime>(){{
			put(AggregationUnitOfLaborCosts.EXTRA, Helper.createLaborCostAndTime(LaborCostItemType.AMOUNT, NotUseAtr.USE));		//集計対象
			put(AggregationUnitOfLaborCosts.TOTAL, Helper.createLaborCostAndTime(LaborCostItemType.TIME, NotUseAtr.USE));		//集計対象
			put(AggregationUnitOfLaborCosts.WITHIN, Helper.createLaborCostAndTime(LaborCostItemType.BUDGET, NotUseAtr.USE));	//集計対象
		}};

		//金額集計の2021/01/01
		@SuppressWarnings("serial")
		val laborCost_20210101 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.EXTRA, BigDecimal.valueOf(2000));
		}};

		//時間集計の2021/01/01
		@SuppressWarnings("serial")
		val laborTime_20210101 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.TOTAL, BigDecimal.valueOf(3000));
		}};

		//金額集計の2021/01/03
		@SuppressWarnings("serial")
		val laborCost_20210103 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.EXTRA, BigDecimal.valueOf(4000));
		}};

		//時間集計の2021/01/03
		@SuppressWarnings("serial")
		val laborTime_20210103 = new HashMap<AggregationUnitOfLaborCosts, BigDecimal>(){{
			put(AggregationUnitOfLaborCosts.TOTAL, BigDecimal.valueOf(5000));
		}};

		/**
		 * 予算金額
		 * 2021/1/2, ( 合計, 予算 ), 予算金額  = 100
		 * 2021/1/3, ( 合計, 予算 ), 予算金額  = 200
		 */
		val budgets = Arrays.asList(
				Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 2), 100)// 年月日, 予算金額
			,	Helper.createLaborCostBudget(GeneralDate.ymd(2021, 1, 3), 200)
			);

		new Expectations(LaborCostsTotalizationService.class) {
			{
				//予算
				require.getLaborCostBudgetList((TargetOrgIdenInfor) any, datePeriod);
				result = budgets;

				//金額の2021/01/01
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 1)));
				result = laborCost_20210101;

				//時間の2021/01/01
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 1)));
				result = laborTime_20210101;

				//金額の2021/01/03
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 2)));

				//時間の2021/01/03
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 2)));

				//金額の2021/01/03
				LaborCostsTotalizationService.totalizeAmounts((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 3)));
				result = laborCost_20210103;

				//時間の2021/01/03
				LaborCostsTotalizationService.totalizeTimes((List<AggregationUnitOfLaborCosts>) any
							,	dailyWorksByDate.get(GeneralDate.ymd(2021, 1, 3)));
				result = laborTime_20210103;
			}
		};

		val result = CountLaborCostTimeService.aggregate(require, targetOrg, datePeriod, targetLaborCost, dailyWorks);

		//・Output.Key が Input.期間 の日付をすべて含んでいること
		assertThat(result.keySet()).containsAll(datePeriod.datesBetween());

		//Output[年月日].keySet()->項目種類『予算』が「ゼロ」であること
		//2021/01/01
		{
			val total = result.get(GeneralDate.ymd(2021, 1, 1));
			assertThat(total.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.AMOUNT,	AggregationUnitOfLaborCosts.EXTRA,	BigDecimal.valueOf(2000))
							,	tuple(LaborCostItemType.TIME,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(3000))
							,	tuple(LaborCostItemType.BUDGET,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.ZERO)
					);
		}

		//2021/01/02
		{
			val budget = result.get(GeneralDate.ymd(2021, 1, 2));
			assertThat(budget.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.BUDGET,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(100))
							);
			//・Output[年月日].keySet()->項目種類 に 『金額, 時間』が存在しないこと
			assertThat(	budget.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.doesNotContainAnyElementsOf(
								Arrays.asList(LaborCostItemType.TIME, LaborCostItemType.AMOUNT));
		}

		//2021/01/03
		{
			val total = result.get(GeneralDate.ymd(2021, 1, 3));
			assertThat(total.entrySet())
					.extracting(c -> c.getKey().getItemType()
							,	c -> c.getKey().getUnit()
							,	c -> c.getValue())
					.containsExactlyInAnyOrder(
								tuple(LaborCostItemType.AMOUNT,	AggregationUnitOfLaborCosts.EXTRA,	BigDecimal.valueOf(4000))
							,	tuple(LaborCostItemType.TIME,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(5000))
							,	tuple(LaborCostItemType.BUDGET,	AggregationUnitOfLaborCosts.TOTAL,	BigDecimal.valueOf(200))
							);
			//・Output[年月日].keySet()->項目種類 に 『金額, 時間, 予算』が存在すること
			assertThat(	total.entrySet())
						.extracting(c -> c.getKey().getItemType())
						.containsAll(Arrays.asList(
									LaborCostItemType.TIME
								,	LaborCostItemType.AMOUNT
								,	LaborCostItemType.BUDGET));
		}
	}

	public static class Helper{

		@Injectable
		private static TargetOrgIdenInfor targetOrg;

		@Injectable
		private static WorkInfoOfDailyAttendance workInformation;

		@Injectable
		private static AffiliationInforOfDailyAttd affiliationInfor;

		/**
		 * 人件費・時間を作る
		 * @param budget 予算
		 * @return
		 */
		public static LaborCostAndTime createBudget(Optional<NotUseAtr> budget) {
			 return new LaborCostAndTime(NotUseAtr.USE, NotUseAtr.USE, NotUseAtr.USE, budget);
		 }

		/**
		 * 人件費・時間を作る
		 * @param itemType 項目種類
		 * @param laborCost 人件費・時間
		 * @return
		 */
		public static LaborCostAndTime createLaborCostAndTime(LaborCostItemType itemType,	NotUseAtr laborCost) {
			switch(itemType) {
				case AMOUNT:
					return new LaborCostAndTime(NotUseAtr.USE, NotUseAtr.NOT_USE, laborCost, Optional.empty());
				case TIME:
					return new LaborCostAndTime(NotUseAtr.USE, laborCost, NotUseAtr.NOT_USE, Optional.empty());
				case BUDGET:
					return new LaborCostAndTime(NotUseAtr.USE, laborCost, NotUseAtr.NOT_USE, Optional.of(laborCost));
				default: return null;
			}
		 }

		/**
		 * 人件費予算を作る
		 * @param ymd　年月日
		 * @param amount 予算
		 * @return
		 */
		 public static LaborCostBudget createLaborCostBudget(GeneralDate ymd, int amount) {
			 return new LaborCostBudget(targetOrg, ymd, new LaborCostBudgetAmount(new Integer(amount)));
		 }

		/**
		 * 日別勤怠(Work)	を作る
		 * @param ymd 年月日
		 * @return
		 */
		public static IntegrationOfDaily createDailyWorks(GeneralDate ymd, AttendanceTimeOfDailyAttendance dailyAtt) {
			return new IntegrationOfDaily(
						"sid", ymd, workInformation
					,	CalAttrOfDailyAttd.defaultData()
					,	affiliationInfor
					,	Optional.empty()
					,	Collections.emptyList()
					,	Optional.empty()
					,	new BreakTimeOfDailyAttd(Collections.emptyList())
					,	Optional.of(dailyAtt)
					,	Optional.empty()
					,	Optional.empty()
					,	Optional.empty()
					,	Optional.empty()
					,	Optional.empty()
					,	Collections.emptyList()
					,	Optional.empty()
					,	Collections.emptyList()
					,	Collections.emptyList()
					,	Collections.emptyList()
					,	Optional.empty());
		}
 }

}
