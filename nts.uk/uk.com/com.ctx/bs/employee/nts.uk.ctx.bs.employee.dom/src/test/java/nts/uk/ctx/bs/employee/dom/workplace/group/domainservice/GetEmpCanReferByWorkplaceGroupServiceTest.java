package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliationHelper;

@RunWith(JMockit.class)
public class GetEmpCanReferByWorkplaceGroupServiceTest {

	@Injectable
	private GetEmpCanReferByWorkplaceGroupService.Require require;

	/**
	 * Condition:
	 * 		ScopeReferWorkplaceGroup = ONLY_ME
	 * Expect:
	 * 		return only employee-id which is passed
	 * 		渡した社員IDだけもらう
	 */
	@Test
	public void testGetEmployeeIdListByReferRange_ONLY_ME() {

		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferByWorkplaceGroupService.class, "getEmployeeIdListByReferRange"
					, require, "emp-id", DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) ), "wpg-id", ScopeReferWorkplaceGroup.ONLY_ME );

		assertThat( result ).containsExactly( "emp-id" );

	}

	/**
	 * Condition:
	 * 		ScopeReferWorkplaceGroup = ALL_EMPLOYEE
	 * Expect:
	 * 		GetAllEmpWhoBelongWorkplaceGroupServiceの結果をもらって社員IDでmapしてからもらう
	 */
	@Test
	public void testGetEmployeeIdListByReferRange_ALL_EMPLOYEE() {

		DatePeriod period = DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) );
		String workplaceGroupId = "wpg-id";

		new Expectations(GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, workplaceGroupId);
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id1", "emp-id2", "emp-id3");

		}};


		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferByWorkplaceGroupService.class, "getEmployeeIdListByReferRange"
					, require, "emp-id", period, workplaceGroupId, ScopeReferWorkplaceGroup.ALL_EMPLOYEE );


		assertThat( result ).containsExactlyInAnyOrder("emp-id1", "emp-id2", "emp-id3");

	}

	/**
	 * Condition:
	 * 		パラメータの職場グループIDリストが存在する
	 * Expect:
	 * 		参照可能範囲Mapはパラメータの職場グループIDリスで絞り込まれる
	 */
	@Test
	public void testGet_canReferRangeMap_workplaceGroupIdList_isNotEmpty() {

		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		DatePeriod period = DatePeriod.oneDay( GeneralDate.ymd(2021, 5, 1) );
		String empId = "emp-id";
		// 職場グループIDリスト
		List<String> workplaceGroupIdList = Arrays.asList("wpl-group1", "wpl-group2", "wpl-group-4");

		// 参照可能範囲Map
		@SuppressWarnings("serial")
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = new HashMap<String, ScopeReferWorkplaceGroup>()
		{{
			put( "wpl-group1", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
			put( "wpl-group2", ScopeReferWorkplaceGroup.ONLY_ME );
			put( "wpl-group3", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
		}};

		new Expectations(GetWorkplaceGroupsAndEmpService.class, GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, empId);
			result = canReferRangeMap;

			// "wpl-group1" - ScopeReferWorkplaceGroup.ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, "wpl-group1");
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id1", "emp-id2", "emp-id3");

		}};


		Map<String, List<String>> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferByWorkplaceGroupService.class, "get"
					, require, empId, date, period, workplaceGroupIdList);


		assertThat( result.keySet() ).containsExactlyInAnyOrder("wpl-group1", "wpl-group2");

		assertThat( result.get("wpl-group1") ).containsExactlyInAnyOrder("emp-id1", "emp-id2", "emp-id3");
		assertThat( result.get("wpl-group2") ).containsExactlyInAnyOrder(empId);

	}


	/**
	 * Condition:
	 * 		パラメータの職場グループIDリストがempty
	 * Expect:
	 * 		参照可能範囲Mapにある職場グループIDをすべて使って社員IDリストを取得する
	 */
	@Test
	public void testGet_canReferRangeMap_workplaceGroupIdList_isEmpty() {

		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		DatePeriod period = DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) );
		String empId = "emp-id";
		// 職場グループIDリスト
		List<String> workplaceGroupIdList = Collections.emptyList();

		// 参照可能範囲Map
		@SuppressWarnings("serial")
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = new HashMap<String, ScopeReferWorkplaceGroup>()
		{{
			put( "wpl-group1", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
			put( "wpl-group2", ScopeReferWorkplaceGroup.ONLY_ME );
			put( "wpl-group3", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
		}};

		new Expectations(GetWorkplaceGroupsAndEmpService.class, GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, empId);
			result = canReferRangeMap;

			// "wpl-group1" - ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, "wpl-group1");
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id1", "emp-id2", "emp-id3");

			// "wpl-group3" - ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, "wpl-group3");
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id4", "emp-id5");

		}};


		Map<String, List<String>> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferByWorkplaceGroupService.class, "get"
					, require, empId, date, period, workplaceGroupIdList);


		assertThat( result.keySet() ).containsExactlyInAnyOrder("wpl-group1", "wpl-group2", "wpl-group3");

		assertThat( result.get("wpl-group1") ).containsExactlyInAnyOrder("emp-id1", "emp-id2", "emp-id3");
		assertThat( result.get("wpl-group2") ).containsExactlyInAnyOrder(empId);
		assertThat( result.get("wpl-group3") ).containsExactlyInAnyOrder("emp-id4", "emp-id5");

	}

	/**
	 * Condition:
	 * 		参照可能範囲Mapに参照したい職場グループIDがない
	 * Expect:
	 * 		emptyListを返す
	 */
	@Test
	public void testGetByWorkplaceGroup_result_isEmpty() {

		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		DatePeriod period = DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) );
		String empId = "emp-id";
		String workplaceGroupId = "wpl-group-id";

		// 参照可能範囲Map
		@SuppressWarnings("serial")
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = new HashMap<String, ScopeReferWorkplaceGroup>()
		{{
			put( "wpl-group1", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
			put( "wpl-group2", ScopeReferWorkplaceGroup.ONLY_ME );
		}};

		new Expectations(GetWorkplaceGroupsAndEmpService.class, GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, empId);
			result = canReferRangeMap;

		}};


		List<String> result = GetEmpCanReferByWorkplaceGroupService.getByWorkplaceGroup(require, empId, date, period, workplaceGroupId);


		assertThat( result ).isEmpty();

	}

	/**
	 * Condition:
	 * 		参照可能範囲Mapに参照したい職場グループIDがある
	 * Expect:
	 * 		社員リストを返す
	 */
	@Test
	public void testGetByWorkplaceGroup_result_isNotEmpty() {

		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		DatePeriod period = DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) );
		String empId = "emp-id";
		String workplaceGroupId = "wpl-group-id";

		@SuppressWarnings("serial")
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = new HashMap<String, ScopeReferWorkplaceGroup>()
		{{
			put( "wpl-group1", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
			put( "wpl-group2", ScopeReferWorkplaceGroup.ONLY_ME );
			put( "wpl-group-id", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
		}};

		new Expectations(GetWorkplaceGroupsAndEmpService.class, GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, empId);
			result = canReferRangeMap;

			// "wpl-group1" - ScopeReferWorkplaceGroup.ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, workplaceGroupId);
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id1", "emp-id2", "emp-id3");

		}};


		List<String> result = GetEmpCanReferByWorkplaceGroupService.getByWorkplaceGroup(require, empId, date, period, workplaceGroupId);


		assertThat( result ).containsExactlyInAnyOrder("emp-id1", "emp-id2", "emp-id3");

	}

	@Test
	public void testGetAll() {

		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		DatePeriod period = DatePeriod.years( 1, GeneralDate.ymd(2020, 5, 30) );
		String empId = "emp-id";

		// 参照可能範囲Map
		@SuppressWarnings("serial")
		Map<String, ScopeReferWorkplaceGroup> canReferRangeMap = new HashMap<String, ScopeReferWorkplaceGroup>()
		{{
			put( "wpl-group1", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
			put( "wpl-group2", ScopeReferWorkplaceGroup.ONLY_ME );
			put( "wpl-group3", ScopeReferWorkplaceGroup.ALL_EMPLOYEE );
		}};

		new Expectations(GetWorkplaceGroupsAndEmpService.class, GetAllEmpWhoBelongWorkplaceGroupService.class) {{

			GetWorkplaceGroupsAndEmpService.getWorkplaceGroup(require, date, empId);
			result = canReferRangeMap;

			// "wpl-group1" - ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, "wpl-group1");
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id1", "emp-id2", "emp-id3");

			// "wpl-group3" - ALL_EMPLOYEE
			GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, period, "wpl-group3");
			result = EmployeeAffiliationHelper.createListWithEmployeeIds("emp-id4", "emp-id5");

		}};


		// Run
		Map<String, List<String>> resultGet = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferByWorkplaceGroupService.class, "get"
					, require, empId, date, period, Collections.emptyList());
		Map<String, List<String>> resultGetAll = GetEmpCanReferByWorkplaceGroupService.getAll(require, empId, date, period);


		// Assert
		assertThat( resultGetAll ).isEqualTo( resultGet );

	}

}
