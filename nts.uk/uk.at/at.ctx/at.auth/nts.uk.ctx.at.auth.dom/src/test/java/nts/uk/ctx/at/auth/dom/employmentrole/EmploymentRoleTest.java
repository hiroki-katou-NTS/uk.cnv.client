package nts.uk.ctx.at.auth.dom.employmentrole;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class EmploymentRoleTest {
	
	@Test
	public void testCreateRoleAttendance() {
		val roleId = "roleId";
		val companyId = "000000000001-0001";
	
		val roleAttendance = EmploymentRole.createEmploymentRole(roleId, companyId);
		
		assertThat(roleAttendance.getRoleId()).isEqualTo(roleId);
		assertThat(roleAttendance.getCompanyId()).isEqualTo(companyId);
		assertThat(roleAttendance.getFutureDateRefPermit()).isEqualTo(NotUseAtr.NOT_USE);
		
	}

}
