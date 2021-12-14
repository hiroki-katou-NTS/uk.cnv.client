/**
 * 
 */
package nts.uk.ctx.at.record.dom.daily.ouen;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

/**
 * @author laitv
 *
 */
public class OuenWorkTimeOfDailyTest {
	
	@Test
	public void testOuenWorkTimeOfDaily_contructor() {
		OuenWorkTimeOfDaily rs = OuenWorkTimeOfDailyHelper.getOuenWorkTimeOfDailyDefault();
		NtsAssert.invokeGetters(rs);
	}

}
