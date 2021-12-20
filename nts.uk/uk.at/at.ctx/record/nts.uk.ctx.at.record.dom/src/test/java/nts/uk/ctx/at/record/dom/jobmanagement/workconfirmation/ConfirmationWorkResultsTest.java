package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

/**
 * 
 * @author chungnt
 *
 */

public class ConfirmationWorkResultsTest {

	private String targetSID = "targetSID";
	private GeneralDate targetYMD = GeneralDate.today();
	private List<Confirmer> confirmers = new ArrayList<>();
	private String confirmSID = "confirmSID";
	
	@Test
	public void testGetter() {
		
		ConfirmationWorkResults confirmationWorkResults = new ConfirmationWorkResults(targetSID, targetYMD, confirmers);
		NtsAssert.invokeGetters(confirmationWorkResults);
		
	}
	
	@Test
	public void testAddConfirmer() {
		
		Confirmer confirmer = new Confirmer(confirmSID, GeneralDateTime.now());
		
		ConfirmationWorkResults confirmationWorkResults = new ConfirmationWorkResults(targetSID, targetYMD, confirmers);
		
		confirmationWorkResults.addConfirmer(confirmer);
		
		assertThat((confirmationWorkResults).getConfirmers().isEmpty())
		.isFalse();
		
		assertThat((confirmationWorkResults).getConfirmers().size())
		.isEqualTo(1);
		assertThat((confirmationWorkResults).getConfirmers().get(0).getConfirmSID())
		.isEqualTo(confirmSID);
		
	}
	
	@Test
	public void testRemoveConfirmer() {
		
		Confirmer confirmer = new Confirmer(confirmSID, GeneralDateTime.now());
		
		confirmers.add(confirmer);
		
		ConfirmationWorkResults confirmationWorkResults = new ConfirmationWorkResults(targetSID, targetYMD, confirmers);
		
		confirmationWorkResults.removeConfirmer(confirmSID);
		
		assertThat((confirmationWorkResults).getConfirmers().isEmpty())
		.isTrue();
		
	}
}
