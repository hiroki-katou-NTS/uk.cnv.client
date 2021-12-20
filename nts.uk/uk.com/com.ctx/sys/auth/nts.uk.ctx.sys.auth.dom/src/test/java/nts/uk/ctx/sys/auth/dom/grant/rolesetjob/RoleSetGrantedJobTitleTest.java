package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;

public class RoleSetGrantedJobTitleTest {
	
	@Test
	public void getters() {
		
		val domain = new RoleSetGrantedJobTitle("companyId", "jobTitleId", new RoleSetCode("roleSetCd"));
		
		NtsAssert.invokeGetters(domain);
	}
}
