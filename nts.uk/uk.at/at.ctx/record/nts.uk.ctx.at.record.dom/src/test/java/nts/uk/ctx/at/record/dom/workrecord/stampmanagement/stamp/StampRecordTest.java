package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
/**
 * 
 * @author tutk
 *
 */
public class StampRecordTest {

	@Test
	public void getters() {
		StampRecord stampRecord = StampRecordHelper.getStampRecord();
		NtsAssert.invokeGetters(stampRecord);
	}
	
	@Test
	public void test() {
		
		StampNumber stampNumber = new StampNumber("stampNumber");
		GeneralDateTime stampDateTime = GeneralDateTime.now();
		StampRecord stampRecord = new StampRecord(new ContractCode("DUMMY"), 
				stampNumber,
				stampDateTime, 
				new StampTypeDisplay("")
				);
		assertThat(stampRecord.getStampNumber()).isEqualTo(stampNumber);
		assertThat(stampRecord.getStampDateTime()).isEqualTo(stampDateTime);
	}

}
