package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
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
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfEmployeeAttribute;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.NumberOfEmployeesByAttributeCountingService;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.NumberOfEmployeesByAttributeCountingService.Require;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.WorkInfoWithAffiliationInfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;

@RunWith(JMockit.class)
public class CountNumberOfPeopleByAttributeServiceTest {

	@Injectable
	private CountNumberOfPeopleByAttributeService.Require require;

	/**
	 * 属性別に集計する
	 * input: 日別勤怠リスト = empty
	 * output: empty
	 */
	@Test
	public void countingEachAttribute_empty() {
		//雇用場合
		Map<GeneralDate, Map<String, BigDecimal>>  result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.EMPLOYMENT, Collections.emptyList());
		assertThat(result).isEmpty();
		//分類場合
		result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.CLASSIFICATION, Collections.emptyList());
		assertThat(result).isEmpty();
		//職位場合
		result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.JOB_TITLE, Collections.emptyList());
		assertThat(result).isEmpty();
	}
	
	/**
	 * 属性別に集計する
	 * input: 日別勤怠リスト = 	[[2021, 1, 1], 分類コード = 001 ]
	 * 					,	[[2021, 1, 2], 分類コード  = 001] ] 
	 * output: 				[(2021, 1, 1], 合計人数　= empty])
	 * 					,	([2021, 1, 2], 合計人数　= empty])]
	 */
	@Test
	public void countingEachAttributeByClassification_empty() {
		/**
		 * [2021/1/26],	分類コード　 = 001
		 * [2021/1/2],	分類コード　 = 002
		 */
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("001"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), Helper.createAffiliationInfoByCls("002")));

		new MockUp<NumberOfEmployeesByAttributeCountingService>() {
			@Mock
			public Map<String, BigDecimal> count(Require require, AggregationUnitOfEmployeeAttribute unit, List<WorkInfoWithAffiliationInfo> values) {
				/** 集計　= empty */
				return Collections.emptyMap();
			}
		};

		Map<GeneralDate, Map<String, BigDecimal>>  result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.CLASSIFICATION, dailyWorks);

		assertThat(result).hasSize(2);
		assertThat(result.keySet())
				.containsAnyElementsOf(Arrays.asList(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 2)));

		Map<String, BigDecimal> value1 = result.get(GeneralDate.ymd(2021, 1, 1));
		assertThat(value1).isEmpty();

		Map<String, BigDecimal> value2 = result.get(GeneralDate.ymd(2021, 1, 2));
		assertThat(value2).isEmpty();
	}
	
	/**
	 * 分類コードで集計する
	 */
	@Test
	public void countingEachAttribute_Classfication() {
		/**
		 * 	[2021/1/1]中に
		 * 	[分類コード  = 	cls_01, 合計人数　= 	1	]
		 *	[分類コード  = 	cls_02,　合計人数 =	2	] 
		 *	[分類コード  = 	cls_03,　合計人数 = 	3 	] 
		 *	[分類コード  = 	cls_04,　合計人数 =	4	] 
		 */
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_01"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByCls("cls_04")));

		//分類コードで合計
		val numberEmpByClsCd = new HashMap<String, BigDecimal>() {
			private static final long serialVersionUID = 1L;
			{
				put("cls_01", new BigDecimal("1")); // 分類コード, 人数
				put("cls_02", new BigDecimal("2"));
				put("cls_03", new BigDecimal("3"));
				put("cls_04", new BigDecimal("4"));
			}
		};

		new MockUp<NumberOfEmployeesByAttributeCountingService>() {
			@Mock
			public Map<String, BigDecimal> count(Require require, AggregationUnitOfEmployeeAttribute unit, List<WorkInfoWithAffiliationInfo> values) {
				return numberEmpByClsCd; 
			}
		};
		
		Map<GeneralDate, Map<String, BigDecimal>>  result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.CLASSIFICATION, dailyWorks);

		assertThat(result).hasSize(1);
		
		assertThat(result.keySet())
				.containsAnyElementsOf(Arrays.asList(GeneralDate.ymd(2021, 1, 1)));

		Map<String, BigDecimal> value1 = result.get(GeneralDate.ymd(2021, 1, 1));
		assertThat(value1.entrySet())
				.extracting( Map.Entry::getKey, Map.Entry::getValue )
				.containsExactlyInAnyOrder(
						tuple("cls_01", new BigDecimal("1"))
					,	tuple("cls_02", new BigDecimal("2"))
					,	tuple("cls_03", new BigDecimal("3"))
					,	tuple("cls_04", new BigDecimal("4"))
				);

	}
	
	/**
	 * 雇用コードで集計する
	 */
	@Test
	public void countingEachAttribute_Employment() {
		/**
		 * 	[雇用コード  = 	emp_01, 合計人数　= 	1	]
		 *	[雇用コード  = 	emp_02,　合計人数 =	2	] 
		 *	[雇用コード  = 	emp_03,　合計人数 = 	3 	] 
		 *	[雇用コード = 	emp_04,　合計人数 =	4	] 
		 */
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_01"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByEmployment("emp_04")));

		/** 雇用コードで合計 */
		val numberEmpByEmplCd = new HashMap<String, BigDecimal>() {
			private static final long serialVersionUID = 1L;
			{
				put("emp_01", new BigDecimal("1")); // 雇用コード, 人数
				put("emp_02", new BigDecimal("2"));
				put("emp_03", new BigDecimal("3"));
				put("emp_04", new BigDecimal("4"));
			}
		};

		new MockUp<NumberOfEmployeesByAttributeCountingService>() {
			@Mock
			public Map<String, BigDecimal> count(Require require, AggregationUnitOfEmployeeAttribute unit, List<WorkInfoWithAffiliationInfo> values) {
				return numberEmpByEmplCd;
								 
			}
		};
		
		Map<GeneralDate, Map<String, BigDecimal>>  result  = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.EMPLOYMENT, dailyWorks);
		
		assertThat(result).hasSize(1);
		assertThat(result.keySet())
				.containsAnyElementsOf(Arrays.asList(GeneralDate.ymd(2021, 1, 1)));

		Map<String, BigDecimal> value1 = result.get(GeneralDate.ymd(2021, 1, 1));
		assertThat(value1.entrySet())
				.extracting( Map.Entry::getKey, Map.Entry::getValue )
				.containsExactlyInAnyOrder(
						tuple("emp_01", new BigDecimal("1"))
					,	tuple("emp_02", new BigDecimal("2"))
					,	tuple("emp_03", new BigDecimal("3"))
					,	tuple("emp_04", new BigDecimal("4"))
				);
	}

	/**
	 * 職位 IDで集計する
	 */
	@Test
	public void countingEachAttribute_JobTitle() {
		/**
		 *  [2021/1/1]中に
		 * 	[職位 ID  = 	jobTitle_01, 合計人数　= 	1	]
		 *	[職位 ID  = 	jobTitle_02,　合計人数 =	2	] 
		 *	[職位 ID  = 	jobTitle_03,　合計人数 = 	3 	] 
		 *	[職位 ID = 	jobTitle_04,　合計人数 =	4	] 
		 */
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_01"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_02"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_03"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_04"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_04")));

		/** 職位 IDで合計 */
		val numberEmpByJobTilte = new HashMap<String, BigDecimal>() {
			private static final long serialVersionUID = 1L;
			{
				put("jobTitle_01", new BigDecimal("1"));// 職位 ID, 人数
				put("jobTitle_02", new BigDecimal("2"));
				put("jobTitle_03", new BigDecimal("3"));
				put("jobTitle_04", new BigDecimal("4"));
			}
		};

		new MockUp<NumberOfEmployeesByAttributeCountingService>() {
			@Mock
			public Map<String, BigDecimal> count(Require require, AggregationUnitOfEmployeeAttribute unit, List<WorkInfoWithAffiliationInfo> values) {
				return numberEmpByJobTilte;
			}
		};
		
		Map<GeneralDate, Map<String, BigDecimal>> result = NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.JOB_TITLE, dailyWorks);
		
		assertThat(result).hasSize(1);
		assertThat(result.keySet())
				.containsAnyElementsOf(Arrays.asList(GeneralDate.ymd(2021, 1, 1)));

		Map<String, BigDecimal> value1 = result.get(GeneralDate.ymd(2021, 1, 1));
		assertThat(value1.entrySet())
				.extracting( Map.Entry::getKey, Map.Entry::getValue )
				.containsExactlyInAnyOrder(
						tuple("jobTitle_01", new BigDecimal("1"))
					,	tuple("jobTitle_02", new BigDecimal("2"))
					,	tuple("jobTitle_03", new BigDecimal("3"))
					,	tuple("jobTitle_04", new BigDecimal("4"))
				);
	}

	
	/**
	 * 回数_テスト
	 * @param service
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void test_count_number_times_of_call_countNumberPeople(@Mocked NumberOfEmployeesByAttributeCountingService service) {
		/**
		 * 	daily = [[2021, 1, 1], 職位 ID = jobTitle_01 ]
		 *		,	[[2021, 1, 2], 職位 ID  = jobTitle_02 ] 
		 * 
		 */
		val dailyWorks = Arrays.asList(
				Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 1), Helper.createAffiliationInfoByJobTitle("jobTitle_01"))
			,	Helper.createDailyWorks(GeneralDate.ymd(2021, 1, 2), Helper.createAffiliationInfoByJobTitle("jobTitle_02")));

		new Expectations() {
			{
				service.count(require, (AggregationUnitOfEmployeeAttribute) any, (List<WorkInfoWithAffiliationInfo> ) any);
				//*　年月日リストは「2021/1/1, 2021/1/2」なので、回数 = 2です。
				times = 2;

			}
		};
		
		NtsAssert.Invoke.staticMethod(CountNumberOfPeopleByAttributeService.class, "countingEachAttribute", require, AggregationUnitOfEmployeeAttribute.JOB_TITLE, dailyWorks);
		
	}
	
	public static class Helper{

		@Injectable
		private static WorkInfoOfDailyAttendance workInformation;

		/**
		 * 日別勤怠(Work)	を作る
		 * @param ymd
		 * @param affiliationInfor
		 * @return
		 */
		public static IntegrationOfDaily createDailyWorks(GeneralDate ymd, AffiliationInforOfDailyAttd affiliationInfor ) {
			return new IntegrationOfDaily(
					  "sid", ymd, workInformation
					, CalAttrOfDailyAttd.defaultData()
					, affiliationInfor
					, Optional.empty()
					, Collections.emptyList()
					, Optional.empty()
					, new BreakTimeOfDailyAttd(Collections.emptyList())
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Collections.emptyList()
					, Optional.empty()
					, Collections.emptyList()
					, Collections.emptyList()
					, Collections.emptyList()
					, Optional.empty());
		}

		/**
		 * 雇用コードで日別勤怠の所属情報を作る
		 * @param employmentCode 雇用コード
		 * @return
		 */
		public static AffiliationInforOfDailyAttd createAffiliationInfoByEmployment(String employmentCode) {
			return new AffiliationInforOfDailyAttd(new EmploymentCode(employmentCode), null, null, null, Optional.empty(), Optional.empty());
		}

		/**
		 * 職位IDで日別勤怠の所属情報を作る
		 * @param jobTitleID 職位ID
		 * @return
		 */
		public static AffiliationInforOfDailyAttd createAffiliationInfoByJobTitle(String jobTitleID) {
			return new AffiliationInforOfDailyAttd(null, jobTitleID, null, null, Optional.empty(), Optional.empty());
		}

		/**
		 * 分類コードで日別勤怠の所属情報を作る
		 * @param clsCode 分類コード
		 * @return
		 */
		public static AffiliationInforOfDailyAttd createAffiliationInfoByCls(String clsCode) {
			return new AffiliationInforOfDailyAttd(null, null, null, new ClassificationCode(clsCode), Optional.empty(), Optional.empty());
		}
	}

}
