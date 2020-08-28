package nts.uk.ctx.at.auth.dom.employmentrole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.dom.employmentrole.GetListWorkplacesByEmpsService.Require;
import nts.uk.ctx.at.auth.dom.employmentrole.dto.ClosureTime;
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
	
	@Test
	public void gettes() {
		GetListWorkplacesByEmpsService getListWorkplacesByEmpsService = new GetListWorkplacesByEmpsService();
		NtsAssert.invokeGetters(getListWorkplacesByEmpsService);
	}
	
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
		Optional<Integer> closureId = Optional.of(ClosureId.RegularEmployee.value); // dummy
		
		new Expectations() {
			{
				require.getCurrentMonthPeriod(closureId.get());
				result = Optional.of(new ClosureTime(GeneralDate.ymd(1900, 01, 01), GeneralDate.ymd(9999, 12, 31)));
			}
		};
		
		assertThat(GetListWorkplacesByEmpsService.get(require, companyId, employeeId, closureId).isEmpty()).isTrue();
	}
	

}
