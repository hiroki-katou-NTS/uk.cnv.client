package nts.uk.ctx.at.record.dom.daily.ouen;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class OuenWorkTimeSheetOfDailyTest {

	@Test
	public void testOuenWorkTimeSheetOfDaily_contructor() {
		OuenWorkTimeSheetOfDaily rs = OuenWorkTimeSheetOfDailyHelper.getOuenWorkTimeSheetOfDailyDefault();
		NtsAssert.invokeGetters(rs);
	}

}
