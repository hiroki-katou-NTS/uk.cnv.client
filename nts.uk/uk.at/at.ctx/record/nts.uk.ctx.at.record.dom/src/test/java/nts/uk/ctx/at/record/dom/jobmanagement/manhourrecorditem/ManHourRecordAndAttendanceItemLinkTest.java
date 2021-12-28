package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;

/**
 * 
 * @author tutt
 *
 */
public class ManHourRecordAndAttendanceItemLinkTest {
	
	@Test
	public void getter() {
		ManHourRecordAndAttendanceItemLink itemLink = new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1,
				1);

		NtsAssert.invokeGetters(itemLink);
	}
}
