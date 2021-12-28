package nts.uk.ctx.at.record.dom.jobmanagement.usagesetting;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class ManHrInputUsageSettingTest {

	@Test
	public void getter() {
		ManHrInputUsageSetting setting = new ManHrInputUsageSetting("", NotUseAtr.NOT_USE);
		setting.setUsrAtr(NotUseAtr.NOT_USE);

		NtsAssert.invokeGetters(setting);
	}
}
