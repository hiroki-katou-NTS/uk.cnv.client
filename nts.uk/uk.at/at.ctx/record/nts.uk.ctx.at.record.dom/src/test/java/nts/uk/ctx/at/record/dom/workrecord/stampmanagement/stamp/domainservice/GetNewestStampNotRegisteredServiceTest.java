package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetNewestStampNotRegisteredService.Require;

/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class GetNewestStampNotRegisteredServiceTest {

	@Injectable
	private Require require;
	
	@Test
	public void test_get_null() {
		
		new Expectations() {
			{	
				require.getStempRcNotResgistNumber(new DatePeriod(GeneralDate.today(), GeneralDate.today()));
				
				require.getStempRcNotResgistNumberStamp(anyString, new DatePeriod(GeneralDate.today(), GeneralDate.today()));
			}
		};
		
		List<StampInfoDisp> disps = GetNewestStampNotRegisteredService.get(require, 
				new DatePeriod(GeneralDate.today(), GeneralDate.today()),
				"contractCode");
		
		assertThat(disps).isEmpty();
	}
	
	@Test
	public void test_get() {
		
		new Expectations() {
			{	
				require.getStempRcNotResgistNumber(new DatePeriod(GeneralDate.today(), GeneralDate.today()));
				result = StampRecordHelper.getListStampRecord();
				
				require.getStempRcNotResgistNumberStamp(anyString, new DatePeriod(GeneralDate.today(), GeneralDate.today()));
				result = StampHelper.getListStampDefault();
			}
		};
		
		List<StampInfoDisp> disps = GetNewestStampNotRegisteredService.get(require, 
				new DatePeriod(GeneralDate.today(), GeneralDate.today()),
				"contractCode");
		
		assertThat(disps).isNotEmpty();
	}
}
