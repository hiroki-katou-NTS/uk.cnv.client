package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.OneMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceGettingService;
import nts.uk.ctx.at.aggregation.dom.common.IntegrationOfDailyHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.ScheRecGettingAtr;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalary;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalaryAggregationService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountHelper;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountValue;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.StepOfCriterionAmount;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * Test for EstimatedSalaryAggregationService
 * @author kumiko_otake
 */
@RunWith(JMockit.class)
public class EstimatedSalaryAggregationServiceTest {


	@Injectable EstimatedSalaryAggregationService.Require require;


	/**
	 * Target	: aggregateByMonthly
	 */
	@Test
	public void test_aggregateByMonthly() {

		// 目安金額の扱い
		val handling = CriterionAmountHelper.createHandling( 1, 2 );
		// 目安金額
		val estimateDetail = CriterionAmountHelper.createCriterionAmount(
						Arrays.asList( 0 )						// 年間
					,	Arrays.asList( 30000, 45000, 70000 )	// 月間
				);
		// 目安金額関連 Mockup設定
		CriterionAmountHelper.mockupRequireForStepOfCriterionAmount(require, handling);
		CriterionAmountHelper.mockupGettingService(estimateDetail);

		// 基準年月：2021年2月
		val BASE_YM = YearMonth.of( 2021,  2 );
		// 締め日：15日締め(2021/01/16-2021/02/15)
		val closing15 = DateInMonth.of(15);	// 15日締め
		val period15 =  new OneMonth(closing15).periodOf(BASE_YM);
		// 締め日：16日締め(2021/01/17-2021/02/16)
		val closing16 = DateInMonth.of(16);	// 16日締め
		val period16 =  new OneMonth(closing16).periodOf(BASE_YM);

		// 日別勤怠リスト
		// 社員[1]	日付：2021/01/16 金額：161
		// 社員[1]	日付：2021/01/17 金額：162
		// 社員[1]	日付：2021/02/15 金額：251
		// 社員[1]	日付：2021/02/16 金額：252
		// ※15日締め -> 合計：574
		// ※16日締め -> 合計：665
		val empId = IntegrationOfDailyHelperInAggregation.createEmpId(1);
		val list = Arrays.asList(
							Helper.createDailyAttendance( empId, period15.start(),	161 )
						,	Helper.createDailyAttendance( empId, period16.start(),	162 )
						,	Helper.createDailyAttendance( empId, period15.end(),	251 )
						,	Helper.createDailyAttendance( empId, period16.end(),	252 )
					);


		// 15日締め
		{
			// Expectation
			val result = EstimatedSalaryAggregationService
					.aggregateByMonthly( require, BASE_YM, closing15, list );

			// Assertion
			assertThat( result ).containsOnlyKeys( empId );
			assertThat( result.get(empId).getSalary() ).isEqualTo( BigDecimal.valueOf( 574 ) );
		}

		// 16日締め
		{
			// Expectation
			val result = EstimatedSalaryAggregationService
					.aggregateByMonthly( require, BASE_YM, closing16, list );

			// Assertion
			assertThat( result ).containsOnlyKeys( empId );
			assertThat( result.get(empId).getSalary() ).isEqualTo( BigDecimal.valueOf( 665 ) );
		}

	}


	/**
	 * Target	: aggregateByCumulatively
	 */
	@Test
	public void test_aggregateByCumulatively() {

		// Expectation
		val executedPeriod = Helper.mockupDailyAttendanceGettingService();
		EstimatedSalaryAggregationService.aggregateByCumulatively( require
					, YearMonth.of( 2021, 3 )	// 基準年月	：2021年3月
					, DateInMonth.of(20)		// 締め日	：20日締め
					, Collections.emptyList()	// 社員IDリスト(Dummy)
				);

		// Assertion
		assertThat( executedPeriod.optional() ).isPresent()
			// 取得期間：1/1～基準年月/締め日(2021/1/1～2021/3/20)
			.get().isEqualTo( new DatePeriod( GeneralDate.ymd( 2021, 1, 1 ), GeneralDate.ymd( 2021, 3, 20 ) ) );

	}


	/**
	 * Target	: aggregateByYearly
	 */
	@Test
	public void test_aggregateByYearly() {

		// Expectation
		val executedPeriod = Helper.mockupDailyAttendanceGettingService();
		EstimatedSalaryAggregationService.aggregateByYearly( require
				, GeneralDate.ymd( 2020, 12, 11 )	// 基準日：2020/12/11
				, Collections.emptyList()			// 社員IDリスト(Dummy)
			);

		// Assertion
		assertThat( executedPeriod.optional() ).isPresent()
			// 取得期間：1/1～基準年月/締め日(2020/1/1～2020/12/31)
			.get().isEqualTo( new DatePeriod( GeneralDate.ymd( 2020, 1, 1 ), GeneralDate.ymd( 2020, 12, 31 ) ) );

	}



	/**
	 * Target	: getAggregateResult
	 * @param req4Test
	 */
	@Test
	public void test_getAggregateResult(@Injectable EstimatedSalaryAggregationService.Require req4Test) {


		val seeds = Helper.getDataMap();


		// 目安金額の扱い
		val handling = CriterionAmountHelper.createHandling( 1, 2 );
		// 目安金額
		val estimateDetail = CriterionAmountHelper.createCriterionAmount(
						Arrays.asList( 0 )	// 年間(Dummy)
					,	Arrays.asList( 30000, 45000, 70000 )	// 月間
				);
		// 目安金額関連 Mockup設定
		CriterionAmountHelper.mockupRequireForStepOfCriterionAmount(require, handling);
		CriterionAmountHelper.mockupRequireForStepOfCriterionAmount(req4Test, handling);
		CriterionAmountHelper.mockupGettingService(estimateDetail);


		// 想定給与額
		// 社員[1] 合計 45,031
		// 社員[2] 合計 30,197
		// 社員[3] 合計 73,410
		val salaries = Helper.getEstimatedSalaries(seeds);


		// Execute
		val isNeedMonthly = true;
		Map<EmployeeId, EstimatedSalary> result = NtsAssert.Invoke.privateMethod(
					new EstimatedSalaryAggregationService()
					, "getAggregateResult"
						, require
						, GeneralDate.today()
						, isNeedMonthly
						, Helper.getDailyAttendance(seeds)
				);


		// Assertion
		// Assertion: 社員ID
		assertThat( result ).containsOnlyKeys( salaries.keySet() );
		// 社員別
		result.entrySet().forEach( byEmpId -> {

			// Assertion: 想定給与額
			val stepResult = byEmpId.getValue();
			val stepExpected = Helper.getStepOfCriterionAmount( req4Test, isNeedMonthly, stepResult.getSalary().intValue() );
			assertThat( stepResult.getSalary() ).isEqualTo( salaries.get(byEmpId.getKey()) );
			assertThat( stepResult.getCriterion() ).isEqualTo( stepExpected.getCriterionAmount() );
			assertThat( stepResult.getBackground() ).isEqualTo( stepExpected.getBackground() );

		} );

	}


	/**
	 * Target	: aggregate
	 */
	@Test
	public void test_aggregate() {


		val seeds = Helper.getDataMap();


		// 期待値：想定給与額
		// 社員[1] 合計 45,031
		// 社員[2] 合計 30,197
		// 社員[3] 合計 73,410
		val expected = Helper.getEstimatedSalaries(seeds);


		// Execute
		Map<EmployeeId, BigDecimal> result = NtsAssert.Invoke.privateMethod(
					new EstimatedSalaryAggregationService()
					, "aggregate"
						, Helper.getDailyAttendance(seeds)
				);


		// Assertion
		assertThat( result ).isEqualTo( expected );

	}


	/**
	 * Target	: getStepOfCriterionAmount
	 * @param req4Test
	 */
	@Test
	public void test_getStepOfCriterionAmount(@Injectable CriterionAmountList.Require req4Test) {

		// 目安金額の扱い
		val handling = CriterionAmountHelper.createHandling(1);
		// 目安金額
		val estimateDetail = CriterionAmountHelper.createCriterionAmount(
						Arrays.asList( 10000, 49800, 99999 )	// 年間
					,	Arrays.asList(  3000,  7200, 12000 )	// 月間
				);
		// 目安金額関連 Mockup設定
		CriterionAmountHelper.mockupRequireForStepOfCriterionAmount(require, handling);
		CriterionAmountHelper.mockupRequireForStepOfCriterionAmount(req4Test, handling);
		CriterionAmountHelper.mockupGettingService(estimateDetail);


		// 期待値
		val estimatedSalary = 12000;	// 想定給与額
		@SuppressWarnings("serial")
		val expected = new HashMap<Boolean, StepOfCriterionAmount>() {{
			// 月間を取得するか：true  → 月間目安金額リストから取得する
			put( true,	estimateDetail.getMonthly().getStepOfEstimateAmount( req4Test, new CriterionAmountValue( estimatedSalary )) );
			// 月間を取得するか：false → 年間目安金額リストから取得する
			put( false,	estimateDetail.getYearly().getStepOfEstimateAmount( req4Test, new CriterionAmountValue( estimatedSalary )) );
		}};


		// Execute & Assertion
		expected.entrySet().forEach( exp -> {

			// Execute
			StepOfCriterionAmount result = NtsAssert.Invoke.privateMethod(new EstimatedSalaryAggregationService()
						, "getStepOfCriterionAmount"
							, require, new EmployeeId("EmpId")
							, GeneralDate.today()
							, exp.getKey()
							, BigDecimal.valueOf( estimatedSalary )
					);

			// Assertion
			assertThat( result ).isEqualTo( exp.getValue() );

		} );

	}


	private static class Helper {

		/**
		 * テストデータのもととなる日別勤怠Mapを取得する
		 * @return
		 */
		@SuppressWarnings("serial")
		public static Map<EmployeeId, Map<IntegrationOfDaily, Integer>> getDataMap() {
			return new HashMap<EmployeeId, Map<IntegrationOfDaily, Integer>>() {{
				// 社員[1]
				// 金額 1日目 16,728 / 2日目 16,129 / 3日目 12,174
				// 合計 45,031
				{
					val empId = IntegrationOfDailyHelperInAggregation.createEmpId(1);
					put( empId, Helper.createDailyAttendanceMap(empId, 16728, 16129, 12174 ) );
				}
				// 社員[2]
				// 金額 1日目  6,894 / 2日目 23,303
				// 合計 30,197
				{
					val empId = IntegrationOfDailyHelperInAggregation.createEmpId(2);
					put( empId, Helper.createDailyAttendanceMap(empId, 6894, 23303 ) );
				}
				// 社員[3]
				// 金額 1日目 17,943 / 2日目 13,266 / 3日目  6,857 / 4日目 19,225 / 5日目 16,119
				// 合計 73,410
				{
					val empId = IntegrationOfDailyHelperInAggregation.createEmpId(3);
					put( empId, Helper.createDailyAttendanceMap(empId, 17943, 13266,  6857, 19225, 16119 ) );
				}
			}};
		}

		/**
		 * 日別勤怠Mapから想定給与額リストを取得する
		 * @param seeds 日別勤怠Map
		 * @return
		 */
		public static Map<EmployeeId, BigDecimal> getEstimatedSalaries(Map<EmployeeId, Map<IntegrationOfDaily, Integer>> seeds) {

			return seeds.entrySet().stream()
					.collect(Collectors.toMap( Map.Entry::getKey
							, byDate -> {
								return BigDecimal.valueOf(
											byDate.getValue().entrySet().stream()
												.mapToInt( e -> e.getValue() )
												.sum()
										);
							} ) );
		}

		/**
		 * 日別勤怠Mapから日別勤怠リストを取得する
		 * @param seeds 日別勤怠Map
		 * @return
		 */
		public static List<IntegrationOfDaily> getDailyAttendance(Map<EmployeeId, Map<IntegrationOfDaily, Integer>> seeds) {
			return seeds.entrySet().stream()
					.flatMap( e -> e.getValue().keySet().stream() )
					.collect(Collectors.toList());
		}


		/**
		 * 日別勤怠(Work)を作成する
		 * ※金額指定あり
		 * @param empId 社員ID
		 * @param date 年月日
		 * @param totalAmount 合計金額
		 * @return 日別勤怠(Work)
		 */
		public static IntegrationOfDaily createDailyAttendance(EmployeeId empId, GeneralDate date, int totalAmount) {

			val dlyAtd = IntegrationOfDailyHelperInAggregation.createDummy( empId.v(), date );
			dlyAtd.setAttendanceTimeOfDailyPerformance(
						Optional.of( IntegrationOfDailyHelperInAggregation.AtdTimeHelper.createWithAmount( totalAmount, 0 )
					));
			return dlyAtd;

		}

		/**
		 * 日別勤怠
		 * @param empId 社員ID
		 * @param totalAmounts 合計金額リスト
		 * @return
		 */
		public static Map<IntegrationOfDaily, Integer> createDailyAttendanceMap(EmployeeId empId, int...totalAmounts) {
			val day = new AtomicInteger(1);
			return IntStream.of( totalAmounts ).boxed()
					.collect(Collectors.toMap(
							amount -> Helper.createDailyAttendance(empId, IntegrationOfDailyHelperInAggregation.createDate(day.getAndIncrement()), amount)
						,	amount -> amount
					));
		}


		/**
		 * Private method『目安金額の段階を取得する』を実行する
		 * @param require require
		 * @param isNeedMonthly 月間を取得するか
		 * @param estimatedSalary 想定給与額
		 * @return
		 */
		public static StepOfCriterionAmount getStepOfCriterionAmount(EstimatedSalaryAggregationService.Require require
				, boolean isNeedMonthly
				, int estimatedSalary
		) {
			return (StepOfCriterionAmount)NtsAssert.Invoke.privateMethod(new EstimatedSalaryAggregationService()
						, "getStepOfCriterionAmount"
							, require
							, new EmployeeId("EmpId")	// dummy
							, GeneralDate.today()		// dummy
							, isNeedMonthly
							, BigDecimal.valueOf( estimatedSalary )
					);
		}


		/**
		 * Mockup設定 『日別勤怠を取得する#取得する』実行用
		 * ※実行時の期間を取得
		 * @return 実行時の期間
		 */
		public static MutableValue<DatePeriod> mockupDailyAttendanceGettingService() {

			val executedPeriod = new MutableValue<DatePeriod>();

			// 日別勤怠関連 Mockup設定
			new MockUp<DailyAttendanceGettingService>() {
				// 日別勤怠を取得する
				@SuppressWarnings({ "unused", "serial" })
				@Mock public Map<ScheRecGettingAtr, List<IntegrationOfDaily>> get(DailyAttendanceGettingService.Require require, List<EmployeeId> empIds, DatePeriod period, ScheRecGettingAtr target) {
					// 取得期間を保存
					executedPeriod.set(period);
					return new HashMap<ScheRecGettingAtr, List<IntegrationOfDaily>>() {{
						put( target, Collections.emptyList() );
					}};
				}
			};

			return executedPeriod;

		}

	}

}
