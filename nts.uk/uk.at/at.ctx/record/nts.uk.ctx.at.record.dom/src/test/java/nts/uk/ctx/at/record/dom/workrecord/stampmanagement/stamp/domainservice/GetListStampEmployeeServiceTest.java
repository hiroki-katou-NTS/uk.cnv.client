package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetListStampEmployeeService.Require;

/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class GetListStampEmployeeServiceTest {
	
	@Injectable
	private Require require;

	/**
	 * GetEmpStampDataService.get(require, employeeId, date) is empty
	 */
	@Test
	public void testGetListStampEmployeeService_1() {
		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				require.getListStampCard(anyString);
				result = new ArrayList<>();
			}
		};
		assertThat(GetListStampEmployeeService.get(require, employeeId, date)).isNotPresent();
	}
	
	/**
	 * GetEmpStampDataService.get(require, employeeId, date) not empty
	 * require.getStampRecord((List<StampNumber>) any, (GeneralDate) any) not empty
	 * require.getStamp((List<StampNumber>) any, (GeneralDate) any) is empty
	 * 
	 */
//	}
	/**
	 * GetEmpStampDataService.get(require, employeeId, date) not empty
	 * require.getStampRecord((List<StampNumber>) any, (GeneralDate) any) not empty
	 * require.getStamp((List<StampNumber>) any, (GeneralDate) any) not empty
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetListStampEmployeeService_3() {
		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				require.getListStampCard(anyString);
				result = Arrays
						.asList(StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()));
				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampDefault());
				
			}
		};
		assertThat(GetListStampEmployeeService.get(require, employeeId, date)).isPresent();
	}

}
