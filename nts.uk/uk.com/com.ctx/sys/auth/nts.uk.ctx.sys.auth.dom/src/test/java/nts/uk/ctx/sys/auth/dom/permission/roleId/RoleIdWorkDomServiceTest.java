package nts.uk.ctx.sys.auth.dom.permission.roleId;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.AllArgsConstructor;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.permission.roleId.RoleIdWorkDomService.Require;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(JMockit.class)
public class RoleIdWorkDomServiceTest {
	
	@Injectable
	private Require require;
	
	
	// 社員IDList  empty
	
	/**
	 * output: Map<社員ID、ロールID> is empty
	 */
	@Test
	public void testEmptySids() {
		List<String> sids = 
				Collections.emptyList();
		
		GeneralDate date = GeneralDate.today();
		
		Map<String, String> roles = RoleIdWorkDomService.get(require, sids, date);
		
		assertThat(roles.isEmpty()).isEqualTo(true);
		
	}
	
	//  社員IDList not empty
	
	
	/**
	 * 
	 * Map<社員ID、ユーザID> not empty
	 * Map<社員ID、ロールID>  not empty
	 * 
	 * output: Map<社員ID、ロールID> not empty
	 * 
	 */
	@Test
	public void testNotEmptyAll() {
		
		List<String> sids = 
				RoleIdWorkDomServiceHelper.Helper.SIDS;

		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				// e1
				require.getUserIDByEmpID(RoleIdWorkDomServiceHelper.Helper.E1);
				result = RoleIdWorkDomServiceHelper.Helper.U1OP;
				
				require.getRoleSetFromUserId("u1", date);
						
				result = RoleIdWorkDomServiceHelper.Helper.R1_ROLESET;
				
				// e2
				require.getUserIDByEmpID("e2");
				result = Optional.of("u2");
				
				require.getRoleSetFromUserId("u2", date);
				result = RoleIdWorkDomServiceHelper.Helper.R2_ROLESET;
				
				// e3
				require.getUserIDByEmpID("e3");
				result = Optional.of("u3");
				
				require.getRoleSetFromUserId("u3", date);
				result = RoleIdWorkDomServiceHelper.Helper.R3_ROLESET;
				
			}
		};
		
		Map<String, String> roles = RoleIdWorkDomService.get(require, sids, date);
				
		assertThat(
				roles.entrySet()
				.stream()
				.map(x -> new Result(x.getKey(), x.getValue()))
				.collect(Collectors.toList())
						)
		.extracting(
				d -> d.getSid(),
				d -> d.getRole())
		.containsExactly(
				tuple("e1", "r1"),
				tuple("e2", "r2"),
				tuple("e3", "r3")
				);
		

		
	}
	
	/**
	 * Map<社員ID、ユーザID> empty
	 * output: Map<社員ID、ロールID> empty
	 */
	
	@Test
	public void testEmptyUserId() {
		List<String> sids = 
				RoleIdWorkDomServiceHelper.Helper.SIDS;

		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				// e1
				require.getUserIDByEmpID(anyString);	
				
			}
		};
		
		Map<String, String> roles = RoleIdWorkDomService.get(require, sids, date);
				
		assertThat(roles.isEmpty()).isEqualTo(true);
		

		
	}
	
	/**
	 * Map<社員ID、ユーザID> not empty
	 * Map<社員ID、ロールID> empty
	 * output: Map<社員ID、ロールID> empty
	 */
	@Test
	public void testEmptyRoles() {
		List<String> sids = 
				RoleIdWorkDomServiceHelper.Helper.SIDS;

		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				// e1
				require.getUserIDByEmpID(RoleIdWorkDomServiceHelper.Helper.E1);
				result = RoleIdWorkDomServiceHelper.Helper.U1OP;
				
				// e2
				require.getUserIDByEmpID(RoleIdWorkDomServiceHelper.Helper.E2);
				result = RoleIdWorkDomServiceHelper.Helper.U2OP;
				
				// e3
				require.getUserIDByEmpID(RoleIdWorkDomServiceHelper.Helper.E3);
				result = RoleIdWorkDomServiceHelper.Helper.U3OP;
				
				require.getRoleSetFromUserId(anyString, date);
			}
		};
		
		Map<String, String> roles = RoleIdWorkDomService.get(require, sids, date);
				
		assertThat(roles.isEmpty()).isEqualTo(true);
		

		
	}
	
	
	
	@AllArgsConstructor
	public class Result {
		private String sid;
		private String role;
		
		public String getSid() {
			return sid;
		}
		
		public String getRole() {
			return role;
		}
	}

}
