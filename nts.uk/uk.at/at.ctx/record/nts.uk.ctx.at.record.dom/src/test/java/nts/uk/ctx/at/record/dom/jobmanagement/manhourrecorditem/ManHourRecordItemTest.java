package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;

/**
 * 
 * @author tutt
 *
 */
public class ManHourRecordItemTest {
	
	@Test
	public void getter() {
		ManHourRecordItem item = new ManHourRecordItem(1, "name", UseAtr.USE);
		
		NtsAssert.invokeGetters(item);
	}

}
