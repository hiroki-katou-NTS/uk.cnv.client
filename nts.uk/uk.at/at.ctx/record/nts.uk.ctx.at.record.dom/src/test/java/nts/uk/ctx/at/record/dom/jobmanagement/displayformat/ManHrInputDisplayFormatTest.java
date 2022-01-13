package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

/**
 * 
 * @author tutt
 *
 */
public class ManHrInputDisplayFormatTest {
	
	@Test
	public void getter() {
		List<RecordColumnDisplayItem> recordColumnDisplayItems = new ArrayList<>();
		recordColumnDisplayItems.add(new RecordColumnDisplayItem(1, 1, new RecordColumnDispName("name")));
		
		List<DisplayAttItem> displayAttItems = new ArrayList<>();
		displayAttItems.add(new DisplayAttItem(1, 1));
		
		List<DisplayManHrRecordItem> displayManHrRecordItems = new ArrayList<>();
		displayManHrRecordItems.add(new DisplayManHrRecordItem(1, 1));
		
		ManHrInputDisplayFormat format = new ManHrInputDisplayFormat(recordColumnDisplayItems, displayAttItems, displayManHrRecordItems);
		
		NtsAssert.invokeGetters(format);
	}
}
