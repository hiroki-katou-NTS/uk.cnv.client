package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.workplace.EmployeeAffiliation;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.GetAllEmpWhoBelongWorkplaceGroupService.Require;

@RunWith(JMockit.class)
public class GetAllEmpWhoBelongWorkplaceGroupServiceTest {

	@Injectable
	private Require require;
	
	/**
	 * require.職場グループに所属する職場を取得する( 職場グループID ) is empty
	 */
	@Test
	public void testGetAllEmp() {
		GeneralDate baseDate = GeneralDate.today();
		String workplaceGroupId =  "workplaceGroupId";
		
		new Expectations() {
			{
				require.getWorkplaceBelongsWorkplaceGroup(workplaceGroupId);
			}
		};
		List<EmployeeAffiliation> datas =  GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, baseDate, workplaceGroupId);
		assertThat(datas.isEmpty()).isTrue();
	}
	
	/**
	 * require.職場グループに所属する職場を取得する( 職場グループID ) is not empty
	 * require.職場の所属社員を取得する( 職場ID, 対象期間 ) is empty
	 */
	@Test
	public void testGetAllEmp_1() {
		GeneralDate baseDate = GeneralDate.today();
		String workplaceGroupId =  "workplaceGroupId1";
		List<String> listWpkId = Arrays.asList("workplaceId1","workplaceId2","workplaceId3");
		new Expectations() {
			{
				require.getWorkplaceBelongsWorkplaceGroup(workplaceGroupId);
				result = listWpkId;
				
				require.getEmployeesWhoBelongWorkplace(anyString, new DatePeriod(baseDate, baseDate));
			}
		};
		List<EmployeeAffiliation> datas =  GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, baseDate, workplaceGroupId);
		assertThat(datas.isEmpty()).isTrue();
	}
	
	/**
	 * require.職場グループに所属する職場を取得する( 職場グループID ) is not empty
	 * require.職場の所属社員を取得する( 職場ID, 対象期間 ) is not empty
	 */
	@Test
	public void testGetAllEmp_2() {
		GeneralDate baseDate = GeneralDate.today();
		String workplaceGroupId =  "workplaceGroupId";
		List<String> listWpkId = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4");
		List<EmployeeInfoData> listEmployeeInfoData1 = Arrays.asList(
				new EmployeeInfoData("empId1a", "empCd1a", "empName1a")
				); 
		List<EmployeeInfoData> listEmployeeInfoData2 = Arrays.asList(
				new EmployeeInfoData("empId2a", "empCd2a", "empName2a")
				); 
		List<EmployeeInfoData> listEmployeeInfoData3 = Arrays.asList(
				new EmployeeInfoData("empId3a", "empCd3a", "empName3a")
				); 
		new Expectations() {
			{
				require.getWorkplaceBelongsWorkplaceGroup(workplaceGroupId);
				result = listWpkId;
				
				require.getEmployeesWhoBelongWorkplace("workplaceId1", new DatePeriod(baseDate, baseDate));
				result = listEmployeeInfoData1;
				
				require.getEmployeesWhoBelongWorkplace("workplaceId2", new DatePeriod(baseDate, baseDate));
				result = listEmployeeInfoData2;
				
				require.getEmployeesWhoBelongWorkplace("workplaceId3", new DatePeriod(baseDate, baseDate));
				result = listEmployeeInfoData3;
				
				require.getEmployeesWhoBelongWorkplace("workplaceId4", new DatePeriod(baseDate, baseDate));
			}
		};
		List<EmployeeAffiliation> datas =  GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, baseDate, workplaceGroupId);
		
		assertThat(datas).extracting(d -> d.getEmployeeID(), d -> d.getEmployeeCode().get().v(),
				d -> d.getBusinessName().get(), d -> d.getWorkplaceID(), d -> d.getWorkplaceGroupID().get())
				.containsExactly(
						tuple(listEmployeeInfoData1.get(0).getEmpId(),listEmployeeInfoData1.get(0).getEmpCd(),listEmployeeInfoData1.get(0).getEmpName(),listWpkId.get(0),workplaceGroupId),
						tuple(listEmployeeInfoData2.get(0).getEmpId(),listEmployeeInfoData2.get(0).getEmpCd(),listEmployeeInfoData2.get(0).getEmpName(),listWpkId.get(1),workplaceGroupId),
						tuple(listEmployeeInfoData3.get(0).getEmpId(),listEmployeeInfoData3.get(0).getEmpCd(),listEmployeeInfoData3.get(0).getEmpName(),listWpkId.get(2),workplaceGroupId)
						
						);
	}

}

