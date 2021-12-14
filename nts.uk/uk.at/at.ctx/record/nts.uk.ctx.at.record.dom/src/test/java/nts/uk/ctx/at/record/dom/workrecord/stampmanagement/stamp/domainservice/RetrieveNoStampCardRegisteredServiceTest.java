package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.RetrieveNoStampCardRegisteredService.Require;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class RetrieveNoStampCardRegisteredServiceTest {

	@Injectable
	private Require require;
	
	private String contractCode = "Contract01";
	
	/**
	 * require.getStempRcNotResgistNumber(period) is null
	 */
	@Test
	public void testRetrieveNoStampCardRegisteredService_1() {
		DatePeriod period = new DatePeriod(GeneralDate.today().addDays(-1), GeneralDate.today().addDays(1));
		new Expectations() {
			{
				require.getStempRcNotResgistNumber(period);
				result = new ArrayList<>();
				
				require.getStempRcNotResgistNumberStamp(anyString, period);
				result = new ArrayList<>();
			}
		};
		
		assertThat(RetrieveNoStampCardRegisteredService.get(require, period, contractCode).isEmpty()).isTrue();
	}
	/**
	 * require.getStempRcNotResgistNumber(period) not null
	 * require.getStempRcNotResgistNumberStamp(period) is null
	 */
	@Test
	public void testRetrieveNoStampCardRegisteredService_2() {
		DatePeriod period = new DatePeriod(GeneralDate.today().addDays(-1), GeneralDate.today().addDays(1));
		new Expectations() {
			{
				require.getStempRcNotResgistNumber(period);
				result = StampRecordHelper.getListStampRecord();
				
				require.getStempRcNotResgistNumberStamp(anyString, period);
				result = new ArrayList<>();
			}
		};
		
		assertThat(RetrieveNoStampCardRegisteredService.get(require, period, contractCode).isEmpty()).isFalse();
	}

	/**
	 * require.getStempRcNotResgistNumber(period) not null
	 * require.getStempRcNotResgistNumberStamp(period) not null
	 */
	@Test
	public void testRetrieveNoStampCardRegisteredService_3() {
		DatePeriod period = new DatePeriod(GeneralDate.today().addDays(-1), GeneralDate.today().addDays(1));
		new Expectations() {
			{
				require.getStempRcNotResgistNumber(period);
				result = StampRecordHelper.getListStampRecord();
				
				require.getStempRcNotResgistNumberStamp(anyString, period);
				result = StampHelper.getListStampDefault();
			}
		};
		assertThat(RetrieveNoStampCardRegisteredService.get(require, period, contractCode).isEmpty()).isFalse();
	}
}
