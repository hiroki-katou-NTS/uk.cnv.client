package nts.uk.ctx.cloud.operate.dom.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;

@RunWith(JMockit.class)
public class CreateDefaultCompanyServiceTest {

	@Injectable
	private CreateDefaultCompanyService.Require require; 
	
	@Test
	public void test() {
		String contractCode = "1";
		String companyName = "テスト会社名";
		
		NtsAssert.atomTask(() -> CreateDefaultCompanyService.create(require, contractCode, companyName),
										any -> require.saveDefaultCompany(any.get()));
	}
}
