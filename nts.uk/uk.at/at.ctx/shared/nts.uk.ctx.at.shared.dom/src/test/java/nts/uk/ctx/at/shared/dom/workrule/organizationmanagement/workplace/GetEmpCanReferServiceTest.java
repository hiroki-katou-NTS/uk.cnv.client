package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

@RunWith(JMockit.class)
public class GetEmpCanReferServiceTest {

	@Injectable
	GetEmpCanReferService.Require require;

	/**
	 * Expect
	 * 		require.sortEmployeeの戻り値を返してもらう
	 */
	@Test
	public void testSortEmployees() {

		List<String> employeeIdList = Arrays.asList("emp4", "emp5", "emp3", "emp1", "emp2");
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);

		new Expectations() {{
			require.sortEmployee(employeeIdList, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);
			result = Arrays.asList("emp3", "emp2", "emp1", "emp5", "emp4");
		}};

		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferService.class,
				"sortEmployees",
				require,
				date,
				employeeIdList);

		assertThat( result ).containsExactly("emp3", "emp2", "emp1", "emp5", "emp4");

	}

	/**
	 * Condition:
	 * 	parameter's workplaceId is present
	 * Expect
	 * 		setFilterByWorkplace 1回呼ばれる
	 * 		setWorkplaceIds 1回呼ばれる
	 * 		require.searchEmployeeの戻り値を返してもらう
	 */
	@Test
	public void testGetByWorkplace_WorkplaceId_isPresent(@Mocked RegulationInfoEmpQuery query) {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		String employeeId = "empId";
		String loginRoleId = "role-id";
		String workplaceId = "workplace-id";

		new Expectations() {{
			require.getRoleID();
			result = loginRoleId;

			query.setFilterByWorkplace(true);
			times = 1;

			query.setWorkplaceIds( Arrays.asList(workplaceId) );
			times = 1;

			require.searchEmployee( (RegulationInfoEmpQuery) any, loginRoleId);
			result = Arrays.asList("emp1", "emp2", "emp3", "emp4", "emp5");
		}};

		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferService.class, "getByWorkplace"
					, require, employeeId, date, DatePeriod.oneDay(date), Optional.of(workplaceId)
			);

		assertThat( result ).containsExactly("emp1", "emp2", "emp3", "emp4", "emp5");
	}

	/**
	 * Condition:
	 * 	parameter's workplaceId is empty
	 * Expect
	 * 		setFilterByWorkplace 呼ばれない
	 * 		setWorkplaceIds 呼ばれない
	 * 		require.searchEmployeeの戻り値を返してもらう
	 */

	@Test
	public void testGetByWorkplace_WorkplaceId_isEmpty(@Mocked RegulationInfoEmpQuery query) {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		String employeeId = "empId";
		String loginRoleId = "role-id";

		new Expectations() {{
			require.getRoleID();
			result = loginRoleId;

			query.setFilterByWorkplace(true);
			times = 0;

			query.setWorkplaceIds( (List<String>) any );
			times = 0;

			require.searchEmployee( (RegulationInfoEmpQuery) any, loginRoleId);
			result = Arrays.asList("emp1", "emp2", "emp3", "emp4", "emp5");
		}};

		List<String> result = NtsAssert.Invoke.staticMethod(
					GetEmpCanReferService.class, "getByWorkplace"
						, require, employeeId, date, DatePeriod.oneDay(date), Optional.empty()
				);

		assertThat( result ).containsExactly("emp1", "emp2", "emp3", "emp4", "emp5");
	}

	/**
	 * Condition
	 * 		parameter workplaceGroupId is present
	 * Expect
	 * 		require.getEmpCanReferByWorkplaceGroupを呼ぶ
	 * 		require.getAllEmpCanReferByWorkplaceGroupを呼ばない
	 */
	@Test
	public void testGetByWorkplaceGroup_workplaceGroupId_isPresent() {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		DatePeriod period = DatePeriod.oneDay(date);
		String employeeId = "empId";
		String workplaceGroupId = "wpl-group-id";

		new Expectations() {{

			require.getEmpCanReferByWorkplaceGroup(employeeId, date, period, workplaceGroupId) ;
			result = Arrays.asList("emp1", "emp2", "emp3");
		}};

		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferService.class, "getByWorkplaceGroup"
					, require, employeeId, date, period, Optional.of(workplaceGroupId)
			);

		new Verifications() {{

			require.getAllEmpCanReferByWorkplaceGroup(employeeId, date, period) ;
			times = 0;
		}};

		assertThat( result ).containsExactly("emp1", "emp2", "emp3");
	}

	/**
	 * Condition
	 * 		parameter workplaceGroupId is present
	 * Expect
	 * 		require.getAllEmpCanReferByWorkplaceGroupを呼ぶ
	 * 		require.getEmpCanReferByWorkplaceGroupを呼ばない
	 */
	@Test
	public void testGetByWorkplaceGroup_workplaceGroupId_isEmpty() {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		DatePeriod period = DatePeriod.oneDay(date);
		String employeeId = "empId";

		new Expectations() {{

			require.getAllEmpCanReferByWorkplaceGroup(employeeId, date, period) ;
			result = Arrays.asList("emp1", "emp2", "emp3");
		}};

		List<String> result = NtsAssert.Invoke.staticMethod(
				GetEmpCanReferService.class, "getByWorkplaceGroup"
					, require, employeeId, date, period, Optional.empty()
			);

		new Verifications() {{
			require.getEmpCanReferByWorkplaceGroup(employeeId, date, period, anyString);
			times = 0;
		}};

		assertThat( result ).containsExactly("emp1", "emp2", "emp3");
	}

	/**
	 * Condition
	 * 		targetUnit = WORKPLACE_GROUP
	 * Expect
	 * 		[prv-1] getByWorkplaceGroupを呼ぶ → require.getEmpCanReferByWorkplaceGroup
	 * 		require.sortEmployeeの結果を返す
	 */
	@Test
	public void testGetByOrg_WORKPLACE_GROUP() {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		DatePeriod period = DatePeriod.oneDay(date);
		String employeeId = "empId";
		String workplaceGroupId = "wpl-group-id";
		TargetOrgIdenInfor targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(workplaceGroupId);
		List<String> empCanReferByWorkplaceGroup = Arrays.asList("emp3", "emp1", "emp2");

		new Expectations() {{
			require.getEmpCanReferByWorkplaceGroup(employeeId, date, period, workplaceGroupId ) ;
			result = empCanReferByWorkplaceGroup;

			require.sortEmployee(empCanReferByWorkplaceGroup, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);
			result = Arrays.asList("emp2", "emp3", "emp1");
		}};

		List<String> result = GetEmpCanReferService.getByOrg(require, employeeId, date, period, targetOrg);

		assertThat( result ).containsExactly("emp2", "emp3", "emp1");

	}

	/**
	 * Condition
	 * 		targetUnit = WORKPLACE
	 * Expect
	 * 		[prv-2] getByWorkplaceを呼ぶ → require.searchEmployee
	 * 		require.sortEmployeeの結果を返す
	 */
	@Test
	public void testGetByOrg_WORKPLACE() {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		String employeeId = "empId";
		String workplaceId = "wpl-id";
		TargetOrgIdenInfor targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplace(workplaceId);
		List<String> empCanReferByWorkplace = Arrays.asList("emp3", "emp1", "emp2");

		new Expectations() {{
			require.searchEmployee( (RegulationInfoEmpQuery) any, anyString);
			result = empCanReferByWorkplace;

			require.sortEmployee(empCanReferByWorkplace, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);
			result = Arrays.asList("emp1", "emp2", "emp3");
		}};

		List<String> result = GetEmpCanReferService.getByOrg(require, employeeId, date, DatePeriod.oneDay(date), targetOrg);

		assertThat( result ).containsExactly("emp1", "emp2", "emp3");
	}

	/**
	 * Expect
	 * 		[prv-1] getByWorkplaceGroupを呼ぶ → require.getEmpCanReferByWorkplaceGroup
	 * 		[prv-2] getByWorkplaceを呼ぶ → require.searchEmployee
	 * 		distinct
	 * 		require.sortEmployeeの結果を返す
	 */
	@Test
	public void testGetAll() {

		GeneralDate date = GeneralDate.ymd(2020, 5, 1);
		DatePeriod period = DatePeriod.oneDay(date);
		String employeeId = "empId";

		new Expectations() {{

			require.getAllEmpCanReferByWorkplaceGroup(employeeId, date, period);
			result = Arrays.asList("emp1", "emp2", "emp3", "emp4");

			require.searchEmployee((RegulationInfoEmpQuery) any, anyString );
			result = Arrays.asList("emp3", "emp4", "emp5", "emp6");

			require.sortEmployee( (List<String>) any, EmployeeSearchCallSystemType.EMPLOYMENT, null, date, null);
			result = Arrays.asList("emp1", "emp2", "emp3", "emp4", "emp5", "emp6");
		}};

		List<String> result = GetEmpCanReferService.getAll(require, employeeId, date, period);

		assertThat( result ).containsExactly("emp1", "emp2", "emp3", "emp4", "emp5", "emp6");

	}

}
