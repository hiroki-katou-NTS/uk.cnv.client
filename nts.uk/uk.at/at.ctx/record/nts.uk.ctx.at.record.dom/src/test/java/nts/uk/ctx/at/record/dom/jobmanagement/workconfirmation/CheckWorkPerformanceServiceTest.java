package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class CheckWorkPerformanceServiceTest {

	@Injectable
	private CheckWorkPerformanceService.Require require;

	String targetSid = "targetSid";
	GeneralDate targetYMD = GeneralDate.today();
	String confirmSid = "confirmSid";
	
	private List<Confirmer> confirmers = new ArrayList<>();
	
	Confirmer confirmer = new Confirmer(confirmSid, GeneralDateTime.now());
	ConfirmationWorkResults confirmationWorkResultsNew = new ConfirmationWorkResults(targetSid, targetYMD,
			Arrays.asList(confirmer));

	// if $作業実績の確認.isEmpty
	@Test
	public void test() {
		new Expectations() {
			{
				require.get(targetSid, targetYMD);
			}
		};

		AtomTask atomtask = CheckWorkPerformanceService.check(require, targetSid, targetYMD, confirmSid);
		NtsAssert.atomTask(() -> atomtask,
				any -> require.insert(confirmationWorkResultsNew));
	}

	// if $作業実績の確認.isNotEmpty
	@Test
	public void test1() {
		confirmers.add(confirmer);
		confirmers.add(confirmer);
		
		ConfirmationWorkResults confirmationWorkResults = new ConfirmationWorkResults(targetSid, targetYMD, confirmers);
		
		new Expectations() {
			{
				require.get(targetSid, targetYMD);
				result = Optional.of(confirmationWorkResults);
			}
		};

		AtomTask atomtask = CheckWorkPerformanceService.check(require, targetSid, targetYMD, confirmSid);
		NtsAssert.atomTask(() -> atomtask,
				any -> require.update(confirmationWorkResultsNew));
	}

}
