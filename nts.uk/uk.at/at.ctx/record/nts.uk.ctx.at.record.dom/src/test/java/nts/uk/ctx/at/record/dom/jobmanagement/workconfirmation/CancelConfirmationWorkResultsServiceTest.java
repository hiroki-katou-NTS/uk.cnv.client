package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
public class CancelConfirmationWorkResultsServiceTest {

	@Injectable
	private CancelConfirmationWorkResultsService.Require require;

	private String targetSid = "targetSid";
	private String confirmSid = "confirmSid";
	private GeneralDate targetYMD = GeneralDate.today();
	private GeneralDateTime dateTime = GeneralDateTime.now();
	
	private List<Confirmer> confirmers = new ArrayList<>();


//	 if (!confirmationWorkResults.isPresent())
//	 return Optional.Empty
	@Test
	public void test() {
		new Expectations() {
			{
				require.get(targetSid, targetYMD);
			}
		};

		assertThat((CancelConfirmationWorkResultsService.check(require, targetSid, targetYMD, confirmSid)).isPresent())
				.isFalse();
	}

//	 require.delete(confirmationWorkResults.get());
	@Test
	public void test_1() {

		confirmers.add(new Confirmer(confirmSid, dateTime));

		ConfirmationWorkResults confirmationWorkResults = new ConfirmationWorkResults(targetSid, targetYMD, confirmers);

		new Expectations() {
			{
				require.get(targetSid, targetYMD);
				result = Optional.of(confirmationWorkResults);
			}
		};

		Optional<AtomTask> result = CancelConfirmationWorkResultsService.check(require, targetSid, targetYMD,
				confirmSid);
		NtsAssert.atomTask(() -> result.get(), any 
				-> require.delete(confirmationWorkResults));
	}
	
	// require.update(confirmationWorkResults.get());
	@Test
	public void test_2() {
		Confirmer confirmer = new Confirmer("confirmSid", dateTime);
		Confirmer confirmer1 = new Confirmer("confirmSid1", dateTime);
		Confirmer confirmer2 = new Confirmer("confirmSid2", dateTime);
		
		List<Confirmer> confirmers1 = new ArrayList<>();
		confirmers1.add(confirmer);
		confirmers1.add(confirmer1);
		confirmers1.add(confirmer2);
		
		Optional<ConfirmationWorkResults> confirmationWorkResults = Optional.of(new ConfirmationWorkResults(targetSid, targetYMD, confirmers1));
		
		new Expectations() {
			{
				require.get(targetSid, targetYMD);
				result = confirmationWorkResults;
			}
		};

		Optional<AtomTask> result = CancelConfirmationWorkResultsService.check(require, targetSid, targetYMD,
				confirmSid);
		NtsAssert.atomTask(() -> result.get(),
				any -> require.update(confirmationWorkResults.get()));
	}
}
