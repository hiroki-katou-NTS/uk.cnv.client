package nts.uk.ctx.at.record.dom.daily.timegroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutt
 *
 */
public class TaskTimeGroupTest {
	
	@Test
	public void getter() {

		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(1), new TimeWithDayAttr(2)),
				new ArrayList<>(Arrays.asList(new SupportFrameNo(1)))));

		TaskTimeGroup group = new TaskTimeGroup("sId", GeneralDate.ymd(2021, 9, 29), timezones);

		NtsAssert.invokeGetters(group);
	}

}
