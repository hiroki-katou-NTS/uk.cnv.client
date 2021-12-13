package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDateTime;

/**
 * 
 * @author chungnt
 *
 */

public class ConfirmerTest {

	@Test
	public void testGetter() {
		Confirmer confirmer = new Confirmer("confirmSID", GeneralDateTime.now());
		NtsAssert.invokeGetters(confirmer);
	}

}
