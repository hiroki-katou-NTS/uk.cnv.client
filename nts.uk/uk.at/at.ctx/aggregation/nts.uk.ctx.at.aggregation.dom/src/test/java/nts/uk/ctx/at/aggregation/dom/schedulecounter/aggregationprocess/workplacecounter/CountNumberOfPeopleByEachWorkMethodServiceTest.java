package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.IntegrationOfDailyHelperInAggregation;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfWorkMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeAbbreviationName;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeName;

public class CountNumberOfPeopleByEachWorkMethodServiceTest {

	@Injectable
	CountNumberOfPeopleByEachWorkMethodService.Require require;

	@Test
	public void testMapping() {

		Map<String, BigDecimal> planNumberList = new HashMap<>();
		Map<String, BigDecimal> scheduleNumberList = new HashMap<>();
		Map<String, BigDecimal> actualNumberList = new HashMap<>();

		planNumberList.put("001", BigDecimal.valueOf(1));
		// schedule -
		// actual   -

		planNumberList.put("002", BigDecimal.valueOf(2));
		scheduleNumberList.put("002", BigDecimal.valueOf(4));
		// actual

		planNumberList.put("003", BigDecimal.valueOf(3));
		scheduleNumberList.put("003", BigDecimal.valueOf(6));
		actualNumberList.put("003", BigDecimal.valueOf(9));

		// plan     -
		scheduleNumberList.put("004", BigDecimal.valueOf(8));
		actualNumberList.put("004", BigDecimal.valueOf(12));

		// plan     -
		// schedule -
		actualNumberList.put("005", BigDecimal.valueOf(15));

		List<NumberOfPeopleByEachWorkMethod<String>> result =
				NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByEachWorkMethodService.class, "mapping",
					planNumberList,
					scheduleNumberList,
					actualNumberList);

		assertThat( result )
			.extracting(
					d -> d.getWorkMethod(),
					d -> d.getPlanNumber().intValue(),
					d -> d.getScheduleNumber().intValue(),
					d -> d.getActualNumber().intValue() )
			.containsOnly(
					tuple( "001", 1, 0, 0),
					tuple( "002", 2, 4, 0),
					tuple( "003", 3, 6, 9),
					tuple( "004", 0, 8, 12),
					tuple( "005", 0, 0, 15)
					);

	}

	@Test
	public void testCount(
			@Injectable WorkInfoOfDailyAttendance dummyWorkInfo
			) {

		GeneralDate ymd0401 = GeneralDate.ymd(2021, 4, 1);
		// GeneralDate ymd0402
		GeneralDate ymd0403 = GeneralDate.ymd(2021, 4, 3);
		GeneralDate ymd0404 = GeneralDate.ymd(2021, 4, 4);
		DatePeriod period = new DatePeriod(ymd0401, ymd0403);

		@SuppressWarnings("serial")
		List<IntegrationOfDaily> workList = new ArrayList<IntegrationOfDaily>() {{
			// 4/1
			add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0401, Helper.createWorkInfo("dummy", "001")) );
			// 4/2 empty
			// 4/3
			add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0403, Helper.createWorkInfo("dummy", "001")) );
			add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp2", ymd0403, Helper.createWorkInfo("dummy", "001")) );
			add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp3", ymd0403, Helper.createWorkInfo("dummy", "002")) );
			// 4/4
			add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp3", ymd0404, dummyWorkInfo) );
		}} ;

		Map<GeneralDate, Map<WorkTimeCode, BigDecimal>> result = NtsAssert.Invoke.staticMethod(
				CountNumberOfPeopleByEachWorkMethodService.class,
				"count",
				require,
				period,
				workList,
				AggregationUnitOfWorkMethod.WORK_TIME,
				( (Function<String, WorkTimeCode>) WorkTimeCode::new)
				);

		assertThat( result ).containsOnlyKeys( ymd0401, ymd0403 );

		assertThat( result.get(ymd0401).entrySet() )
			.extracting( Map.Entry::getKey, Map.Entry::getValue )
			.containsExactlyInAnyOrder(
				tuple( new WorkTimeCode("001"), BigDecimal.valueOf(1)) );

		assertThat( result.get(ymd0403).entrySet() )
			.extracting( Map.Entry::getKey, Map.Entry::getValue )
			.containsExactlyInAnyOrder(
					tuple( new WorkTimeCode("001"), BigDecimal.valueOf(2)),
					tuple( new WorkTimeCode("002"), BigDecimal.valueOf(1)));
	}

	@Test
	public void testCountByWorkMethod(
			@Injectable TargetOrgIdenInfor targetOrg,
			@Injectable WorkInfoOfDailyAttendance dummyWorkInfo
			) {

		GeneralDate ymd0401 = GeneralDate.ymd(2021, 4, 1);
		GeneralDate ymd0402 = GeneralDate.ymd(2021, 4, 2);
		GeneralDate ymd0403 = GeneralDate.ymd(2021, 4, 3);
		GeneralDate ymd0404 = GeneralDate.ymd(2021, 4, 4);
		GeneralDate ymd0405 = GeneralDate.ymd(2021, 4, 5);
		DatePeriod period = new DatePeriod(ymd0401, ymd0404);

		val scheList = new ArrayList<>();
		val actList = new ArrayList<>();
		{
			/** 4/1 */
			// [001] schedule: 1 / actual: 0
			scheList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0401, Helper.createWorkInfo("dummy", "001")) );
			// [002] schedule: 0 / actual: 2
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp2", ymd0401, Helper.createWorkInfo("dummy", "002")) );
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp3", ymd0401, Helper.createWorkInfo("dummy", "002")) );

			/** 4/2 */
			// [003] schedule: 0 / actual: 1
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0402, Helper.createWorkInfo("dummy", "003")) );

			/** 4/3 */
			// [---] schedule: 0 / actual: 0

			/** 4/4 */
			// [001] schedule: 2 / actual: 0
			scheList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0404, Helper.createWorkInfo("dummy", "001")) );
			scheList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp2", ymd0404, Helper.createWorkInfo("dummy", "001")) );
			// [002] schedule: 1 / actual: 1
			scheList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp3", ymd0404, Helper.createWorkInfo("dummy", "002")) );
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp4", ymd0404, Helper.createWorkInfo("dummy", "002")) );
			// [003] schedule: 0 / actual: 3
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp5", ymd0404, Helper.createWorkInfo("dummy", "003")) );
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp6", ymd0404, Helper.createWorkInfo("dummy", "003")) );
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp7", ymd0404, Helper.createWorkInfo("dummy", "003")) );

			/** 4/5 */
			// [dummy] schedule: 1 / actual: 1
			scheList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp1", ymd0405, dummyWorkInfo ) );
			actList.add( IntegrationOfDailyHelperInAggregation.createWithWorkInfo("emp2", ymd0405, dummyWorkInfo ) );

		}

		Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkTimeCode>> > result = NtsAssert.Invoke.staticMethod(
				CountNumberOfPeopleByEachWorkMethodService.class,
				"countByWorkMethod",
				require,
				targetOrg,
				period,
				scheList,
				actList,
				AggregationUnitOfWorkMethod.WORK_TIME,
				( (Function<String, WorkTimeCode>) WorkTimeCode::new)
				);

		// test key
		assertThat( result ).containsOnlyKeys( period.datesBetween() );

		// 2021/4/1
		assertThat( result.get( ymd0401) )
			.extracting(
					d -> d.getWorkMethod().v(),
					d -> d.getPlanNumber().intValue(),
					d -> d.getScheduleNumber().intValue(),
					d -> d.getActualNumber().intValue())
			.containsOnly(
					tuple( "001", 0, 1, 0),
					tuple( "002", 0, 0, 2)
					);
		// 2021/4/2
		assertThat( result.get(ymd0402) )
		.extracting(
				d -> d.getWorkMethod().v(),
				d -> d.getPlanNumber().intValue(),
				d -> d.getScheduleNumber().intValue(),
				d -> d.getActualNumber().intValue())
		.containsOnly(
				tuple( "003", 0, 0, 1)
				);

		// 2021/4/3
		assertThat( result.get(ymd0403) ).isEmpty();

		// 2021/4/4
		assertThat( result.get(ymd0404) )
		.extracting(
				d -> d.getWorkMethod().v(),
				d -> d.getPlanNumber().intValue(),
				d -> d.getScheduleNumber().intValue(),
				d -> d.getActualNumber().intValue())
		.containsOnly(
				tuple( "001", 0, 2, 0),
				tuple( "002", 0, 1, 1),
				tuple( "003", 0, 0, 3)
				);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetOnlyWorkingDay() {
		
		val workType_1 = Helper.createWorkType("wtp001");
		val workType_2 = Helper.createWorkType("wtp003");
		val workType_3 = Helper.createWorkType("wtp004");
		val workType_4 = Helper.createWorkType("wtp005");
		
		val dailyWork1 = Helper.createIntegrationOfDaily("wtp001", "dummy");
		val dailyWork2 = Helper.createIntegrationOfDaily("wtp002", "");//filter
		val dailyWork3 = Helper.createIntegrationOfDaily("wtp003", "dummy" );
		val dailyWork4 = Helper.createIntegrationOfDaily("wtp004", "dummy");
		val dailyWork5 = Helper.createIntegrationOfDaily("wtp005", "dummy" );//duplicate
		val dailyWork6 = Helper.createIntegrationOfDaily("wtp005", "dummy" );//duplicate
		val dailyWork7 = Helper.createIntegrationOfDaily("wtp006", "dummy" );//master not existed
		
		val dailyWorks = Arrays.asList( dailyWork1, dailyWork2, dailyWork3, dailyWork4, dailyWork5, dailyWork6, dailyWork7 );
		
		val workTypes = Arrays.asList( workType_1, workType_2, workType_3, workType_4 );
		
		new Expectations( workType_1, workType_2, workType_3, workType_4 ) {
			{
				require.getWorkTypes( (List<WorkTypeCode>) any);
				result = workTypes;
				
				workType_1.isWorkingDay();
				result = true;
				
				workType_2.isWorkingDay();
				result = false;
				
				workType_3.isWorkingDay();
				result = true;
				
				workType_4.isWorkingDay();
				result = false;
			}
		};
		
		//Act
		List<IntegrationOfDaily> result = NtsAssert.Invoke.staticMethod(
				CountNumberOfPeopleByEachWorkMethodService.class,
				"getOnlyWorkingDay",
				require,
				dailyWorks
				);
		
		//Assert
		assertThat( result ).containsExactly( dailyWork1, dailyWork4 );
		
	}


	public static class Helper {
		
		/**
		 * 日別勤怠(Work)を作る
		 * @param workTypeCode 勤務種類コード(String)
		 * @param workTimeCode 就業時間帯コード(String)
		 * @return
		 */
		public static IntegrationOfDaily createIntegrationOfDaily( String workTypeCode, String workTimeCode ) {
			
			return IntegrationOfDailyHelperInAggregation
					.createWithWorkInfo("dummy", GeneralDate.today(), createWorkInfo( workTypeCode, workTimeCode));
		}

		/**
		 * 日別勤怠の勤務情報を作成する
		 * @param workTypeCode 勤務種類コード(String)
		 * @param workTimeCode 就業時間帯コード(String)
		 * @return
		 */
		public static WorkInfoOfDailyAttendance createWorkInfo(String workTypeCode, String workTimeCode) {
			return IntegrationOfDailyHelperInAggregation
					.WorkInfoHelper.createWithCode(workTypeCode, Optional.ofNullable(workTimeCode));
		}
		
		/**
		 * 勤務種類を作る
		 * @param workTypeCode 勤務種類コード(String)
		 * @return
		 */
		public static WorkType createWorkType( String workTypeCode ) {
			
			return new WorkType( "cid"
					, new WorkTypeCode( workTypeCode )
					, new WorkTypeName("dummy")
					, new WorkTypeAbbreviationName("dummy"));
		}
		
	}

}
