package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

/**
 * 
 * @author tutt
 *
 */
public class TaskContentTest {

	@Test
	public void getter() {
		
		TaskContent content = new TaskContent(1, new WorkCode("1"));		
		NtsAssert.invokeGetters(content);
	}
}
