package nts.uk.ctx.at.auth.dom.employmentrole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.dom.adapter.workplace.AffWorkplaceHistoryItemImport;
import nts.uk.ctx.at.auth.dom.employmentrole.GetListWorkplacesByEmpsService.Require;
import nts.uk.ctx.at.auth.dom.employmentrole.dto.ClosureInformation;
import nts.uk.ctx.at.auth.dom.employmentrole.dto.ClosureTime;
import nts.uk.ctx.at.auth.dom.employmentrole.dto.WorkplaceManagerDto;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureInfo;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureName;
import nts.uk.shr.com.time.calendar.date.ClosureDate;


/**
 * @author laitv
 */

@RunWith(JMockit.class)
public class GetListWorkplacesByEmpsServiceTest { 

	@Injectable
	private Require require;
	
	/**
	 *  [prv-1] 全締から職場確定できる職場を取得する
	 *  case : Optional<Integer> closureId = Optional.empty();
	 *  check : require.getCurrentMonthForAllClosure() is empty
	 */
	@Test
	public void testGetListWorkplacesByEmpsServiceTest_1() {
		String companyId = "companyId";   // dummy
		String employeeId = "employeeId"; // dummy
		Optional<Integer> closureId = Optional.empty(); // dummy
		
		new Expectations() {
			{
				require.getCurrentMonthForAllClosure();
			}
		};
		
		assertThat(GetListWorkplacesByEmpsService.get(require, companyId, employeeId, closureId).isEmpty()).isTrue();
	}
	
	
	/**
	 *  [prv-1] 全締から職場確定できる職場を取得する
	 *  case : Optional<Integer> closureId = Optional.empty();
	 *  check : require.getCurrentMonthForAllClosure() is not empty
	 */
	@Test
	public void testGetListWorkplacesByEmpsServiceTest_2() {
		String companyId = "companyId";   // dummy
		String employeeId = "employeeId"; // dummy
		Optional<Integer> closureId =  Optional.empty(); // dummy
		
		new Expectations() {
			{
				require.getCurrentMonthForAllClosure();
				result = Arrays.asList(ClosureInfo.of(ClosureId.RegularEmployee,
						new ClosureDate(1, false), 
						new ClosureName("ClosureName"), 
						new nts.arc.time.YearMonth(2020),
						new DatePeriod(GeneralDate.ymd(1900, 01, 01), GeneralDate.ymd(9999, 12, 31))));
			}
		};
		
		assertThat(GetListWorkplacesByEmpsService.get(require, companyId, employeeId, closureId).isEmpty()).isTrue();
	}
	
	/**
	 *  [prv-2] 指定締めから職場確定できる職場を取得する
	 *  case : Optional<Integer> closureId =  not empty;
	 *  check : require.getCurrentMonthPeriod(closureId) is empty
	 */
	@Test
	public void testGetListWorkplacesByEmpsServiceTest_3() {
		String companyId = "companyId";   // dummy
		String employeeId = "employeeId"; // dummy
		Optional<Integer> closureId = Optional.of(ClosureId.RegularEmployee.value); // dummy
		
		new Expectations() {
			{
				require.getCurrentMonthPeriod(closureId.get());
			}
		};
		
		assertThat(GetListWorkplacesByEmpsService.get(require, companyId, employeeId, closureId).isEmpty()).isTrue();
	} 
	
	/**
	 *  [prv-2] 指定締めから職場確定できる職場を取得する
	 *  case : Optional<Integer> closureId =  not empty;
	 *  check : require.getCurrentMonthPeriod(closureId) is empty
	 */
	@Test
	public void testGetListWorkplacesByEmpsServiceTest_4() {
		String companyId = "companyId";   // dummy
		String employeeId = "employeeId"; // dummy
		String workplaceId = "workplaceId";
		Optional<Integer> closureId = Optional.of(ClosureId.RegularEmployee.value); // dummy
		DatePeriod date = new DatePeriod(GeneralDate.ymd(1900, 01, 01), GeneralDate.ymd(9000, 12, 31));
		
		new Expectations() {
			{
				require.getCurrentMonthPeriod(closureId.get());
				result = Optional.of(date);
				
				require.getAffiliatedEmployees(workplaceId, date.end());
				result = Arrays.asList(new AffWorkplaceHistoryItemImport("historyId", employeeId, workplaceId, "normalWorkplaceId"));
				
				require.getWorkplaceManager(employeeId, GeneralDate.ymd(9000, 12, 31));
				result = new WorkplaceManagerDto("workplaceManagerId", employeeId, workplaceId, date);
				
				require.getProcessCloseCorrespondToEmps(Arrays.asList(employeeId), date.end());
				result = Arrays.asList(new ClosureInformation(employeeId, closureId.get()));
			}
		};
		
		List<String> s = GetListWorkplacesByEmpsService.get(require, companyId, employeeId, closureId);
		assertThat(s).isNotEmpty();
	}
}
