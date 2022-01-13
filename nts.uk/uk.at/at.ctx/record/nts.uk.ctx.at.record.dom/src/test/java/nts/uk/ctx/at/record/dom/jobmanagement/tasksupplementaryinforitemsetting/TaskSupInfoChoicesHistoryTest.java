package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * 
 * @author tutt
 *
 */
public class TaskSupInfoChoicesHistoryTest {
	
	@Test
	public void testGetter() {
		
		List<DateHistoryItem> dateHistoryItems = new ArrayList<>();
		
		dateHistoryItems.add(new DateHistoryItem("1", new DatePeriod(GeneralDate.today(), GeneralDate.today())));
		
		TaskSupInfoChoicesHistory hist = new TaskSupInfoChoicesHistory(1, dateHistoryItems);
		
		NtsAssert.invokeGetters(hist);
	}
	
	@Test
	public void testGetItems() {
		
		List<DateHistoryItem> expectedHistoryItems = new ArrayList<>();
		
		expectedHistoryItems.add(new DateHistoryItem("1", new DatePeriod(GeneralDate.today(), GeneralDate.today())));
		
		TaskSupInfoChoicesHistory hist = new TaskSupInfoChoicesHistory(1, expectedHistoryItems);
		
		assertThat(expectedHistoryItems).isEqualTo(hist.items());
	}
	
	
	
	
	

}
