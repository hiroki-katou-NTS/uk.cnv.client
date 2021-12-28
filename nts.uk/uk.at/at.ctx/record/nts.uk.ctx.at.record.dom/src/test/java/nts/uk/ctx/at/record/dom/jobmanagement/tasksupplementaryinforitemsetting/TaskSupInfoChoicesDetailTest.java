package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * 
 * @author tutt
 *
 */
public class TaskSupInfoChoicesDetailTest {

	@Test
	public void testGetter() {
		TaskSupInfoChoicesDetail detail = new TaskSupInfoChoicesDetail("123", 1, new ChoiceCode("1"),
				new ChoiceName("name"), Optional.of(new ExternalCode("123")));

		NtsAssert.invokeGetters(detail);

	}

}
