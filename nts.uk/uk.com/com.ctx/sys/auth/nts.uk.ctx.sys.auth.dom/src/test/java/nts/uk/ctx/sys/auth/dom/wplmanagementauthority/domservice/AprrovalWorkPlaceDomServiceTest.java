package nts.uk.ctx.sys.auth.dom.wplmanagementauthority.domservice;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.permission.roleId.RoleIdWorkDomService;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.domservice.AprrovalWorkPlaceDomService.Require;

@RunWith(JMockit.class)
public class AprrovalWorkPlaceDomServiceTest {

	@Injectable
	private Require require;
	
	/**
	 *  取得する(職場ID, 期間) empty
	 *  取得する(会社ID, 社員ID, 年月日) empty
	 *  承認権限がある就業ロールを取得する  empty
	 *  
	 *  output 承認者一覧	 empty
	 */
	@Test
	public void testEmptyAll() {
		List<String> workPlaceIds = 
				AprrovalWorkPlaceDomServiceHelper.Helper.WPLS;
		GeneralDate date = GeneralDate.today();
		Map<String, String> inputResult = new HashMap<String, String>();
		new Expectations() {
			{
				require.getSids(
						workPlaceIds,
						new DatePeriod(date, date));
				
				new MockUp<RoleIdWorkDomService>() {
					@Mock
					public Map<String, String> get(
							RoleIdWorkDomService.Require require,
							List<String> sids,
							GeneralDate date
							) {
						return inputResult;
					}
				};
				
				require.getRoles("cid");
						
			}
		};
		
		
		List<String> sidsResult = AprrovalWorkPlaceDomService.get(require, workPlaceIds, date, "cid");
		
		assertThat(CollectionUtil.isEmpty(sidsResult))
			.isEqualTo(true);
		
	}
	
	/**
	 *  取得する(職場ID, 期間) empty
	 *  取得する(会社ID, 社員ID, 年月日) empty
	 *  承認権限がある就業ロールを取得する not empty
	 *  
	 *  output 承認者一覧	 empty
	 */
	@Test
	public void testEmptySids() {
		List<String> workPlaceIds = 
				AprrovalWorkPlaceDomServiceHelper.Helper.WPLS;
		
		GeneralDate date = GeneralDate.today();
		Map<String, String> inputResult = new HashMap<String, String>();
		new Expectations() {
			{
				require.getSids(
						workPlaceIds,
						new DatePeriod(date, date));
				
				new MockUp<RoleIdWorkDomService>() {
					@Mock
					public Map<String, String> get(
							RoleIdWorkDomService.Require require,
							List<String> sids,
							GeneralDate date
							) {
						return inputResult;
					}
				};
				
				require.getRoles("cid");
				result = AprrovalWorkPlaceDomServiceHelper.Helper.ROLE_IDS;
				
						
			}
		};
		
		
		List<String> sidsResult = AprrovalWorkPlaceDomService.get(require, workPlaceIds, date, "cid");
		
		assertThat(CollectionUtil.isEmpty(sidsResult))
			.isEqualTo(true);
		
	}
	
	/**
	 *  
	 *  取得する(会社ID, 社員ID, 年月日) not empty
	 *  承認権限がある就業ロールを取得する not empty
	 *  
	 *  output 承認者一覧	 not empty
	 */
	@Test
	public void testNotEmptyRoles() {
		List<String> workPlaceIds = 
				AprrovalWorkPlaceDomServiceHelper.Helper.WPLS;
		
		List<String> sids = 
				AprrovalWorkPlaceDomServiceHelper.Helper.SIDS;
		
		GeneralDate date = GeneralDate.today();
		Map<String, String> inputResult = AprrovalWorkPlaceDomServiceHelper.Helper.ROLE_IDS;
		new Expectations() {
			{
				require.getSids(
						workPlaceIds,
						new DatePeriod(date, date));
				result = sids;
				new MockUp<RoleIdWorkDomService>() {
					@Mock
					public Map<String, String> get(
							RoleIdWorkDomService.Require require,
							List<String> sids,
							GeneralDate date
							) {
						return inputResult;
					}
				};
				
				require.getRoles("cid");
				result = AprrovalWorkPlaceDomServiceHelper.Helper.ROLES;
					
				
				
						
			}
		};
		
		
		List<String> sidsResult = AprrovalWorkPlaceDomService.get(require, workPlaceIds, date, "cid");
		
		
		assertThat(sidsResult)
			.extracting(
					d -> d)
			.containsExactly(
					"e1",
					"e2",
					"e3"
					);
		
	}
	
	
	/**
	 *  
	 *  取得する(会社ID, 社員ID, 年月日) not empty
	 *  承認権限がある就業ロールを取得する  empty
	 *  
	 *  output 承認者一覧	 empty
	 */
	@Test
	public void testNotInRole() {
		List<String> workPlaceIds = 
				AprrovalWorkPlaceDomServiceHelper.Helper.WPLS;
		
		GeneralDate date = GeneralDate.today();
		Map<String, String> inputResult = 
				AprrovalWorkPlaceDomServiceHelper.Helper.ROLE_IDS;
		new Expectations() {
			{
				require.getSids(
						workPlaceIds,
						new DatePeriod(date, date));
				
				new MockUp<RoleIdWorkDomService>() {
					@Mock
					public Map<String, String> get(
							RoleIdWorkDomService.Require require,
							List<String> sids,
							GeneralDate date
							) {
						return inputResult;
					}
				};
				
				require.getRoles("cid");
				
				
				
						
			}
		};
		
		
		List<String> sidsResult = AprrovalWorkPlaceDomService.get(require, workPlaceIds, date, "cid");
		
		assertThat(CollectionUtil.isEmpty(sidsResult)).isEqualTo(true);
	}

}
