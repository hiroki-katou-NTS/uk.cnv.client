/**
 * 
 */
package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

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
	
	@Test
	public void testSetOuenTime() {
		List<OuenWorkTimeOfDailyAttendance> attendances = new ArrayList<>();
		OuenWorkTimeOfDaily rs = OuenWorkTimeOfDailyHelper.getOuenWorkTimeOfDailyDefault();
		
		rs.setOuenTime(attendances);
		
		assertThat((rs.getOuenTimes()).isEmpty()).isTrue();
	}
}
