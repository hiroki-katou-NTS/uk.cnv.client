package nts.uk.ctx.at.auth.dom.employmentrole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.auth.dom.adapter.workplace.AffWorkplaceHistoryItemImport;
import nts.uk.ctx.at.auth.dom.employmentrole.dto.ClosureInformation;

/**
 * @author laitv
 */

@RunWith(JMockit.class)
public class GetWorkplacesServiceTest {

	@Injectable
	private GetWorkplacesService.Require require;

	/**
	 *  [prv-1] 指定職場に同じ締めの社員があるか
	 *  check : require.getAffiliatedEmployees(workplaceId, baseDate) is empty
	 */
	@Test
	public void testGetWorkplacesServiceTest_1() {
		List<String> workplaceIds = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4","workplaceId5");// dummy
		GeneralDate baseDate = GeneralDate.today(); // dummy
		Integer closureId = 1; // dummy
		new Expectations() {
			{
				require.getAffiliatedEmployees(workplaceIds.get(0), baseDate);
			}
		};
		
		assertThat(GetWorkplacesService.get(require, closureId, workplaceIds, baseDate).isEmpty()).isTrue();
	}
	
	/**
	 *  [prv-1] 指定職場に同じ締めの社員があるか 
	 *  require.getAffiliatedEmployees(workplaceId, baseDate) is not empty
	 *  check require.getProcessCloseCorrespondToEmps is empty
	 */
	@Test
	public void testGetWorkplacesServiceTest_2() {
		List<String> workplaceIds = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4","workplaceId5"); // dummy 
		GeneralDate baseDate      = GeneralDate.today();// dummy
		Integer closureId         = 1;// dummy
		List<String> sids         = Arrays.asList("sid","sid","sid","sid","sid");// dummy
		new Expectations() { 
			{
				require.getAffiliatedEmployees(workplaceIds.get(0), baseDate);
				result = Arrays.asList(new AffWorkplaceHistoryItemImport("historyId","employeeId","workplaceId","normalWorkplaceId"));
				
				require.getProcessCloseCorrespondToEmps((List<String>) any, baseDate);
				
			} 
		};
		
		assertThat(GetWorkplacesService.get(require, closureId, workplaceIds, baseDate).isEmpty()).isTrue(); 
	}
	
	/**
	 *  [prv-1] 指定職場に同じ締めの社員があるか
	 *  require.getAffiliatedEmployees(workplaceId, baseDate) is not empty
	 *  require.getProcessCloseCorrespondToEmps is not empty
	 *  listClosureInformation.stream().anyMatch(item -> item.getClosureID() == closureId) = false
	 */
//	@Test
//	public void testGetWorkplacesServiceTest_3() {
//		List<String> workplaceIds = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4","workplaceId5"); // dummy
//		GeneralDate baseDate      = GeneralDate.today();// dummy
//		Integer closureId         = 1;// dummy
//		List<String> sids         = Arrays.asList("employeeId", "employeeId2", "employeeId3");// dummy
//		new Expectations() {
//			{ 
//				require.getAffiliatedEmployees(workplaceIds.get(0), baseDate);
//				result = Arrays.asList(new AffWorkplaceHistoryItemImport("historyId","employeeId","workplaceId","normalWorkplaceId"));
//				
//				require.getProcessCloseCorrespondToEmps((List<String>) any, (GeneralDate) any);
//				result = Arrays.asList(new ClosureInformation("employeeId", 1));
//			}
//		};
//		
//		assertThat(GetWorkplacesService.get(require, closureId, workplaceIds, baseDate).isEmpty()).isFalse();
//	} 
	
	
	/**
	 *  [prv-1] 指定職場に同じ締めの社員があるか
	 *  require.getAffiliatedEmployees(workplaceId, baseDate) is not empty
	 *  require.getProcessCloseCorrespondToEmps is not empty
	 *  listClosureInformation.stream().anyMatch(item -> item.getClosureID() == closureId) = true
	 */
	@Test
	public void testGetWorkplacesServiceTest_4() {
		List<String> workplaceIds = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4","workplaceId5"); // dummy
		GeneralDate baseDate      = GeneralDate.today();// dummy
		Integer closureId         = 1;// dummy
		List<String> sids         = Arrays.asList("sid");// dummy
		new Expectations() {
			{
				require.getAffiliatedEmployees(workplaceIds.get(0), baseDate);
				result = Arrays.asList(new AffWorkplaceHistoryItemImport("historyId","employeeId","workplaceId","normalWorkplaceId"));
				
				require.getProcessCloseCorrespondToEmps((List<String>) any, (GeneralDate) any);
				result = Arrays.asList(new ClosureInformation("employeeID", 1));
			}
		};
		
		assertThat(GetWorkplacesService.get(require, closureId, workplaceIds, baseDate).isEmpty()).isFalse();
	}
	
	/**
	 *  [prv-1] 指定職場に同じ締めの社員があるか
	 *  require.getAffiliatedEmployees(workplaceId, baseDate) is not empty
	 *  require.getProcessCloseCorrespondToEmps is not empty
	 *  listClosureInformation.stream().anyMatch(item -> item.getClosureID() == closureId) = false
	 */
	@Test
	public void testGetWorkplacesServiceTest_5() {
		List<String> workplaceIds = Arrays.asList("workplaceId1","workplaceId2","workplaceId3","workplaceId4","workplaceId5"); // dummy
		GeneralDate baseDate      = GeneralDate.today();// dummy
		Integer closureId         = 1;// dummy
		List<String> sids         = Arrays.asList("sid");// dummy
		new Expectations() {
			{
				require.getAffiliatedEmployees(workplaceIds.get(0), baseDate);
				result = Arrays.asList(new AffWorkplaceHistoryItemImport("historyId","employeeId","workplaceId","normalWorkplaceId"));
				
				require.getProcessCloseCorrespondToEmps((List<String>) any, (GeneralDate) any);
				result = Arrays.asList(new ClosureInformation("employeeID", 2));
			}
		};
		
		assertThat(GetWorkplacesService.get(require, closureId, workplaceIds, baseDate).isEmpty()).isTrue();
	}



}
